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

import com.fredboat.backend.quarterdeck.config.property.DatabaseConfig;
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
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;
import space.npstr.sqlsauce.DatabaseConnection;
import space.npstr.sqlsauce.DatabaseException;

import javax.annotation.Nullable;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Component
public class Database {

    private static final Logger log = LoggerFactory.getLogger(Database.class);

    private static final String MAIN_PERSISTENCE_UNIT_NAME = "fredboat.main";
    private static final String CACHE_PERSISTENCE_UNIT_NAME = "fredboat.cache";

    private final DatabaseConfig dbConf;
    private final HibernateStatisticsCollector hibernateStats;
    private final PrometheusMetricsTrackerFactory hikariStats;

    private final DatabaseConnection.EntityManagerFactoryBuilder entityManagerFactoryBuilder =
            (puName, dataSource, properties, entityPackages) -> {
                LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
                emfb.setDataSource(dataSource);
                emfb.setPackagesToScan(entityPackages.toArray(new String[0]));

                JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
                emfb.setJpaVendorAdapter(vendorAdapter);
                emfb.setJpaProperties(properties);

                emfb.afterPropertiesSet(); //initiate creation of the native emf
                return emfb.getNativeEntityManagerFactory();
            };

    @Nullable
    private volatile DatabaseConnection mainDbConn;
    private final Object mainDbConnInitLock = new Object();

    @Nullable
    private volatile DatabaseConnection cacheDbConn;
    private final Object cacheDbConnInitLock = new Object();

    public Database(DatabaseConfig dbConf, HibernateStatisticsCollector hibernateStats,
                    PrometheusMetricsTrackerFactory hikariStats) {
        this.dbConf = dbConf;
        this.hibernateStats = hibernateStats;
        this.hikariStats = hikariStats;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            final DatabaseConnection cache = this.cacheDbConn;
            if (cache != null) {
                cache.shutdown();
            }
            final DatabaseConnection main = this.mainDbConn;
            if (main != null) {
                main.shutdown();
            }
        }, "databasemanager-shutdown-hook"));
    }

    /**
     * @return the connection to the main db.
     * If not connected yet, a connection will be attempted, which may take a long time (seconds).
     *
     * @throws DatabaseException
     *         if anything went wrong
     */
    public DatabaseConnection getMainDbConn() {
        DatabaseConnection singleton = this.mainDbConn;
        if (singleton == null) {
            synchronized (this.mainDbConnInitLock) {
                singleton = this.mainDbConn;
                if (singleton == null) {
                    this.mainDbConn = singleton = initMainDbConn();
                }
            }
        }
        return singleton;
    }


    /**
     * @return the connection to the cache db. May be null if the cache db is not configured.
     * If not connected yet, a connection will be attempted, which may take a long time (seconds).
     *
     * @throws DatabaseException
     *         if anything went wrong
     */
    @Nullable
    public DatabaseConnection getCacheDbConn() {
        final String cacheJdbcUrl = this.dbConf.getCacheJdbcUrl();
        if (cacheJdbcUrl == null || cacheJdbcUrl.isEmpty()) {
            return null;
        }
        DatabaseConnection singleton = this.cacheDbConn;
        if (singleton == null) {
            synchronized (this.cacheDbConnInitLock) {
                singleton = this.cacheDbConn;
                if (singleton == null) {
                    this.cacheDbConn = singleton = initCacheConn(cacheJdbcUrl);
                }
            }
        }
        return singleton;
    }

    /**
     * @throws DatabaseException
     *         if anything went wrong
     */
    private DatabaseConnection initMainDbConn() {

        Flyway flyway = buildFlyway("classpath:com/fredboat/backend/quarterdeck/db/migrations/main");

        DatabaseConnection databaseConnection = getBasicConnectionBuilder(MAIN_PERSISTENCE_UNIT_NAME, this.dbConf.getMainJdbcUrl())
                .setHibernateProps(buildHibernateProps("ehcache_main.xml"))
                .addEntityPackage("com.fredboat.backend.quarterdeck.db.entities.main")
                .setFlyway(flyway)
                .build();

        log.debug(CacheManager.getCacheManager("MAIN_CACHEMANAGER").getActiveConfigurationText());

        return databaseConnection;
    }

    /**
     * @throws DatabaseException
     *         if anything went wrong
     */
    private DatabaseConnection initCacheConn(String jdbc) {

        Flyway flyway = buildFlyway("classpath:com/fredboat/backend/quarterdeck/db/migrations/cache");

        DatabaseConnection databaseConnection = getBasicConnectionBuilder(CACHE_PERSISTENCE_UNIT_NAME, jdbc)
                .setHibernateProps(buildHibernateProps("ehcache_cache.xml"))
                .addEntityPackage("com.fredboat.backend.quarterdeck.db.entities.cache")
                .setFlyway(flyway)
                .build();

        log.debug(CacheManager.getCacheManager("CACHE_CACHEMANAGER").getActiveConfigurationText());

        return databaseConnection;
    }

    private DatabaseConnection.Builder getBasicConnectionBuilder(String connectionName, String jdbcUrl) {
        return new DatabaseConnection.Builder(connectionName, jdbcUrl)
                .setHikariConfig(buildHikariConfig())
                .setDialect("org.hibernate.dialect.PostgreSQL95Dialect")
                .setAppName("FredBoat_Quarterdeck")
                .setHikariStats(this.hikariStats)
                .setHibernateStats(this.hibernateStats)
                .setProxyDataSourceBuilder(new ProxyDataSourceBuilder()
                        .logSlowQueryBySlf4j(10, TimeUnit.SECONDS, SLF4JLogLevel.WARN, "SlowQueryLog")
                        .multiline()
                )
                .setEntityManagerFactoryBuilder(this.entityManagerFactoryBuilder);
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
        hikariConfig.setMaximumPoolSize(this.dbConf.getHikariPoolSize());
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
        hibernateProps.put("hibernate.hbm2ddl.auto", "validate");

        return hibernateProps;
    }
}
