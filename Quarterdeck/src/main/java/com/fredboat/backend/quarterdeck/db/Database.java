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
import com.github.benmanes.caffeine.jcache.configuration.CaffeineConfiguration;
import com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTrackerFactory;
import io.prometheus.client.hibernate.HibernateStatisticsCollector;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.hibernate.cache.jcache.internal.JCacheRegionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.schema.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;
import space.npstr.sqlsauce.DatabaseConnection;
import space.npstr.sqlsauce.DatabaseException;

import javax.annotation.Nullable;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Factory;
import javax.cache.expiry.ExpiryPolicy;
import java.time.Duration;
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
                .setHibernateProps(buildHibernateProps())
                .addEntityPackage("com.fredboat.backend.quarterdeck.db.entities.main")
                .setFlyway(flyway)
                .build();

        log2ndLevelCacheConfig();
        return databaseConnection;
    }

    /**
     * @throws DatabaseException
     *         if anything went wrong
     */
    private DatabaseConnection initCacheConn(String jdbc) {

        Flyway flyway = buildFlyway("classpath:com/fredboat/backend/quarterdeck/db/migrations/cache");

        DatabaseConnection databaseConnection = getBasicConnectionBuilder(CACHE_PERSISTENCE_UNIT_NAME, jdbc)
                .setHibernateProps(buildHibernateProps())
                .addEntityPackage("com.fredboat.backend.quarterdeck.db.entities.cache")
                .setFlyway(flyway)
                .build();

        log2ndLevelCacheConfig();
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

    private Properties buildHibernateProps() {
        Properties hibernateProps = DatabaseConnection.Builder.getDefaultHibernateProps();
        hibernateProps.put(Environment.USE_SECOND_LEVEL_CACHE, true);
        hibernateProps.put(Environment.USE_QUERY_CACHE, true);
        hibernateProps.put(Environment.CACHE_REGION_FACTORY, JCacheRegionFactory.class.getName());
        hibernateProps.put("hibernate.javax.cache.provider", CaffeineCachingProvider.class.getName());
        hibernateProps.put("hibernate.javax.cache.missing_cache_strategy", "fail");
        //hide some exception spam on start, as postgres does not support CLOBs
        // https://stackoverflow.com/questions/43905119/postgres-error-method-org-postgresql-jdbc-pgconnection-createclob-is-not-imple
        hibernateProps.put(Environment.NON_CONTEXTUAL_LOB_CREATION, true);
        hibernateProps.put(Environment.HBM2DDL_AUTO, Action.VALIDATE);

        return hibernateProps;
    }

    private void log2ndLevelCacheConfig() {
        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
        cacheManager.getCacheNames().forEach(name -> {
            Cache<Object, Object> cache = cacheManager.getCache(name);
            @SuppressWarnings("unchecked")
            CaffeineConfiguration cacheConfiguration = cache.getConfiguration(CaffeineConfiguration.class);
            String configAsString = caffeineConfigToString(cacheConfiguration);
            log.debug("\nCache config: {}\n{}", name, configAsString);
        });
    }

    private String caffeineConfigToString(CaffeineConfiguration<?, ?> caffeineConfig) {
        Factory<ExpiryPolicy> expiryPolicyFactory = caffeineConfig.getExpiryPolicyFactory();
        ExpiryPolicy expiryPolicy = expiryPolicyFactory.create();
        javax.cache.expiry.Duration expiryForCreation = expiryPolicy.getExpiryForCreation();
        javax.cache.expiry.Duration expiryForUpdate = expiryPolicy.getExpiryForUpdate();
        javax.cache.expiry.Duration expiryForAccess = expiryPolicy.getExpiryForAccess();
        Duration creation = Duration.of(expiryForCreation.getDurationAmount(), expiryForCreation.getTimeUnit().toChronoUnit());
        Duration update = Duration.of(expiryForUpdate.getDurationAmount(), expiryForUpdate.getTimeUnit().toChronoUnit());
        Duration access = Duration.of(expiryForAccess.getDurationAmount(), expiryForAccess.getTimeUnit().toChronoUnit());
        return "  Key type: " + caffeineConfig.getKeyType()
                + "\n  Value type: " + caffeineConfig.getValueType()
                + "\n  Store by value: " + caffeineConfig.isStoreByValue()
                + "\n  Read through: " + caffeineConfig.isReadThrough()
                + "\n  Write through: " + caffeineConfig.isWriteThrough()
                + "\n  Statistics: " + caffeineConfig.isStatisticsEnabled()
                + "\n  Management: " + caffeineConfig.isManagementEnabled()
                + "\n  Lazy creation expiration: " + creation
                + "\n  Lazy update expiration: " + update
                + "\n  Lazy access expiration: " + access
                + "\n  Eager expire after access: " + caffeineConfig.getExpireAfterAccess()
                + "\n  Eager expire after write: " + caffeineConfig.getExpireAfterWrite()
                + "\n  Refresh after write: " + caffeineConfig.getRefreshAfterWrite()
                + "\n  Maximum size: " + caffeineConfig.getMaximumSize()
                + "\n  Maximum wight: " + caffeineConfig.getMaximumWeight()
                ;
    }
}
