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

package com.fredboat.backend.quarterdeck.config;

import com.fredboat.backend.quarterdeck.config.property.DatabaseConfig;
import fredboat.db.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import space.npstr.sqlsauce.DatabaseConnection;
import space.npstr.sqlsauce.DatabaseWrapper;

import javax.annotation.Nullable;

/**
 * Created by napster on 23.02.18.
 * <p>
 * Provides database related beans
 */
@Configuration
public class DatabaseConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);
    private final DatabaseConfig dbConf;

    public DatabaseConfiguration(DatabaseConfig dbConf) {
        this.dbConf = dbConf;
    }

    @Primary
    @Bean
    public DatabaseWrapper mainDbWrapper() throws InterruptedException {
        return new DatabaseWrapper(mainDbConn());
    }

    @Primary
    @Bean
    public DatabaseConnection mainDbConn() throws InterruptedException {
        //attempt to connect to the database a few times
        // this is relevant in a dockerized environment because after a reboot there is no guarantee that the db
        // container will be started before the fredboat one
        int dbConnectionAttempts = 0;
        DatabaseConnection mainDbConn = null;
        while ((mainDbConn == null || !mainDbConn.isAvailable()) && dbConnectionAttempts++ < 10) {
            try {
                if (mainDbConn != null) {
                    mainDbConn.shutdown();
                }
                mainDbConn = databaseManager().getMainDbConn();
            } catch (Exception e) {
                log.info("Could not connect to the database. Retrying in a moment...", e);
                Thread.sleep(6000);
            }
        }
        if (mainDbConn == null || !mainDbConn.isAvailable()) {
            String message = "Could not establish database connection. Exiting...";
            log.error(message);
            System.exit(1);
        }

        return mainDbConn;
    }

    @Bean
    @Nullable
    public DatabaseWrapper cacheDbWrapper() {
        DatabaseConnection cacheDbConn = cacheDbConn();
        return cacheDbConn == null ? null : new DatabaseWrapper(cacheDbConn);
    }

    @Bean
    @Nullable
    public DatabaseConnection cacheDbConn() {
        try {
            return databaseManager().getCacheDbConn();
        } catch (Exception e) {
            String message = "Exception when connecting to cache db";
            log.error(message, e);
            throw new RuntimeException(message);
        }
    }

//    @Nullable
//    @Bean
//    public HibernateStatisticsCollector hibernateStats() {
//        return null; //todo
//    }
//
//    @Nullable
//    @Bean
//    public PrometheusMetricsTrackerFactory hikariStats() {
//        return null; //todo
//    }

    @Bean
    public DatabaseManager databaseManager() {
//                                           HibernateStatisticsCollector hibernateStats,
//                                           PrometheusMetricsTrackerFactory hikariStats) {
        DatabaseManager databaseManager = new DatabaseManager(null, null,
                this.dbConf.getHikariPoolSize(), "Quarterdeck", true,
                this.dbConf.getMainJdbcUrl(), this.dbConf.getCacheJdbcUrl(),
                (puName, dataSource, properties, entityPackages) -> {
                    LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
                    emfb.setDataSource(dataSource);
                    emfb.setPackagesToScan(entityPackages.toArray(new String[entityPackages.size()]));

                    JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
                    emfb.setJpaVendorAdapter(vendorAdapter);
                    emfb.setJpaProperties(properties);

                    emfb.afterPropertiesSet(); //initiate creation of the native emf
                    return emfb.getNativeEntityManagerFactory();
                });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (databaseManager.isCacheConnBuilt()) {
                DatabaseConnection cacheDbConn = databaseManager.getCacheDbConn();
                if (cacheDbConn != null) {
                    cacheDbConn.shutdown();
                }
            }
            if (databaseManager.isMainConnBuilt()) {
                databaseManager.getMainDbConn().shutdown();
            }
        }, "databasemanager-shutdown-hook"));

        return databaseManager;
    }
}
