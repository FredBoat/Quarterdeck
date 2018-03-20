/*
 * MIT License
 *
 * Copyright (c) 2016-2018 The FredBoat Org https://github.com/FredBoat/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.fredboat.backend.quarterdeck.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTrackerFactory;
import io.prometheus.client.hibernate.HibernateStatisticsCollector;
import net.sf.ehcache.CacheManager;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.npstr.sqlsauce.DatabaseConnection;
import space.npstr.sqlsauce.DatabaseException;
import space.npstr.sqlsauce.DatabaseWrapper;

import javax.annotation.Nullable;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class DatabaseManager {

    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);

    private static final String MAIN_PERSISTENCE_UNIT_NAME = "fredboat.main";
    private static final String CACHE_PERSISTENCE_UNIT_NAME = "fredboat.cache";

    @Nullable
    private final HibernateStatisticsCollector hibernateStats;
    @Nullable
    private final PrometheusMetricsTrackerFactory hikariStats;
    private final int poolsize;
    private final String appName;
    private final boolean migrateAndValidate;
    private final String mainJdbc;
    @Nullable
    private final String cacheJdbc;
    @Nullable
    private final DatabaseConnection.EntityManagerFactoryBuilder entityManagerFactoryBuilder;

    @Nullable
    private DatabaseConnection mainDbConn;
    private final Object mainDbConnInitLock = new Object();
    private boolean mainConnBuilt = false;
    @Nullable
    private volatile DatabaseWrapper mainDbWrapper;
    private final Object mainDbWrapperInitLock = new Object();

    @Nullable
    private DatabaseConnection cacheDbConn;
    private final Object cacheDbConnInitLock = new Object();
    private boolean cacheConnBuilt = false;
    @Nullable
    private volatile DatabaseWrapper cacheDbWrapper;
    private final Object cacheDbWrapperInitLock = new Object();

    public DatabaseManager(@Nullable HibernateStatisticsCollector hibernateStats,
                           @Nullable PrometheusMetricsTrackerFactory hikariStats,
                           int poolsize,
                           String appName,
                           boolean migrateAndValidate,
                           String mainJdbc,
                           @Nullable String cacheJdbc,
                           @Nullable DatabaseConnection.EntityManagerFactoryBuilder entityManagerFactoryBuilder) {
        this.hibernateStats = hibernateStats;
        this.hikariStats = hikariStats;
        this.poolsize = poolsize;
        this.appName = appName;
        this.migrateAndValidate = migrateAndValidate;
        this.mainJdbc = mainJdbc;
        this.cacheJdbc = cacheJdbc;
        this.entityManagerFactoryBuilder = entityManagerFactoryBuilder;

        if (mainJdbc.isEmpty()) {
            log.error("Main jdbc url is empty - database creation will fail");
        }
        if (cacheJdbc != null && cacheJdbc.isEmpty()) {
            log.error("Cache jdbc url is empty - database creation will fail");
        }
    }

    public DatabaseConnection getMainDbConn() {
        DatabaseConnection singleton = this.mainDbConn;
        if (singleton == null) {
            synchronized (this.mainDbConnInitLock) {
                singleton = this.mainDbConn;
                if (singleton == null) {
                    this.mainDbConn = singleton = initMainDbConn();
                    this.mainConnBuilt = true;
                }
            }
        }
        return singleton;
    }

    public DatabaseWrapper getMainDbWrapper() {
        DatabaseWrapper singleton = this.mainDbWrapper;
        if (singleton == null) {
            synchronized (this.mainDbWrapperInitLock) {
                singleton = this.mainDbWrapper;
                if (singleton == null) {
                    this.mainDbWrapper = singleton = new DatabaseWrapper(getMainDbConn());
                }
            }
        }
        return singleton;
    }


    @Nullable //may return null if no cache db is configured
    public DatabaseConnection getCacheDbConn() {
        if (this.cacheJdbc == null || this.cacheJdbc.isEmpty()) {
            return null;
        }
        DatabaseConnection singleton = this.cacheDbConn;
        if (singleton == null) {
            synchronized (this.cacheDbConnInitLock) {
                singleton = this.cacheDbConn;
                if (singleton == null) {
                    this.cacheDbConn = singleton = initCacheConn(this.cacheJdbc);
                    this.cacheConnBuilt = true;
                }
            }
        }
        return singleton;
    }

    @Nullable //may return null if no cache db is configured
    public DatabaseWrapper getCacheDbWrapper() {
        if (this.cacheJdbc == null) {
            return null;
        }
        DatabaseWrapper singleton = this.cacheDbWrapper;
        if (singleton == null) {
            synchronized (this.cacheDbWrapperInitLock) {
                singleton = this.cacheDbWrapper;
                if (singleton == null) {
                    DatabaseConnection cacheDbConn = getCacheDbConn();
                    if (cacheDbConn != null) {
                        this.cacheDbWrapper = singleton = new DatabaseWrapper(cacheDbConn);
                    }
                }
            }
        }
        return singleton;
    }

    public boolean isMainConnBuilt() {
        return this.mainConnBuilt;
    }

    public boolean isCacheConnBuilt() {
        return this.cacheConnBuilt;
    }

    private DatabaseConnection initMainDbConn() throws DatabaseException {

        Flyway flyway = null;
        if (this.migrateAndValidate) {
            flyway = buildFlyway("classpath:com/fredboat/backend/quarterdeck/db/migrations/main");
        }

        DatabaseConnection databaseConnection = getBasicConnectionBuilder(MAIN_PERSISTENCE_UNIT_NAME, this.mainJdbc)
                .setHibernateProps(buildHibernateProps("ehcache_main.xml"))
                .addEntityPackage("com.fredboat.backend.quarterdeck.db.entities.main")
                .setFlyway(flyway)
                .build();

        log.debug(CacheManager.getCacheManager("MAIN_CACHEMANAGER").getActiveConfigurationText());

        return databaseConnection;
    }


    public DatabaseConnection initCacheConn(String jdbc) throws DatabaseException {

        Flyway flyway = null;
        if (this.migrateAndValidate) {
            flyway = buildFlyway("classpath:com/fredboat/backend/quarterdeck/db/migrations/cache");
        }

        DatabaseConnection databaseConnection = getBasicConnectionBuilder(CACHE_PERSISTENCE_UNIT_NAME, jdbc)
                .setHibernateProps(buildHibernateProps("ehcache_cache.xml"))
                .addEntityPackage("com.fredboat.backend.quarterdeck.db.entities.cache")
                .setFlyway(flyway)
                .build();

        log.debug(CacheManager.getCacheManager("CACHE_CACHEMANAGER").getActiveConfigurationText());

        return databaseConnection;
    }

    private DatabaseConnection.Builder getBasicConnectionBuilder(String connectionName, String jdbcUrl) {
        DatabaseConnection.Builder builder = new DatabaseConnection.Builder(connectionName, jdbcUrl)
                .setHikariConfig(buildHikariConfig())
                .setDialect("org.hibernate.dialect.PostgreSQL95Dialect")
                .setAppName("FredBoat_" + this.appName)
                .setHikariStats(this.hikariStats)
                .setHibernateStats(this.hibernateStats)
                .setProxyDataSourceBuilder(new ProxyDataSourceBuilder()
                        .logSlowQueryBySlf4j(10, TimeUnit.SECONDS, SLF4JLogLevel.WARN, "SlowQueryLog")
                        .multiline()
                );
        if (this.entityManagerFactoryBuilder != null) {
            builder = builder.setEntityManagerFactoryBuilder(this.entityManagerFactoryBuilder);
        }
        return builder;
    }

    private Flyway buildFlyway(String locations) {
        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);
        flyway.setBaselineVersion(MigrationVersion.fromVersion("0"));
        flyway.setBaselineDescription("Base Migration");
        flyway.setLocations(locations);

        return flyway;
    }

    private HikariConfig buildHikariConfig() {
        HikariConfig hikariConfig = DatabaseConnection.Builder.getDefaultHikariConfig();
        hikariConfig.setMaximumPoolSize(this.poolsize);
        return hikariConfig;
    }

    private Properties buildHibernateProps(String ehcacheXmlFile) {
        Properties hibernateProps = DatabaseConnection.Builder.getDefaultHibernateProps();
        hibernateProps.put("hibernate.cache.use_second_level_cache", "true");
        hibernateProps.put("hibernate.cache.use_query_cache", "true");
        hibernateProps.put("net.sf.ehcache.configurationResourceName", ehcacheXmlFile);
        hibernateProps.put("hibernate.cache.provider_configuration_file_resource_path", ehcacheXmlFile);
        hibernateProps.put("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
        //hide some exception spam on start, as postgres does not support CLOBs
        // https://stackoverflow.com/questions/43905119/postgres-error-method-org-postgresql-jdbc-pgconnection-createclob-is-not-imple
        hibernateProps.put("hibernate.jdbc.lob.non_contextual_creation", "true");

        if (this.migrateAndValidate) {
            hibernateProps.put("hibernate.hbm2ddl.auto", "validate");
        } else {
            hibernateProps.put("hibernate.hbm2ddl.auto", "none");
        }

        return hibernateProps;
    }
}
