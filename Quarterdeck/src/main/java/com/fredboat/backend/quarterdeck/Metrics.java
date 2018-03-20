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

package com.fredboat.backend.quarterdeck;

import ch.qos.logback.classic.LoggerContext;
import com.fredboat.backend.quarterdeck.config.DatabaseConfiguration;
import com.fredboat.backend.quarterdeck.db.DatabaseManager;
import io.prometheus.client.Counter;
import io.prometheus.client.hibernate.HibernateStatisticsCollector;
import io.prometheus.client.hotspot.DefaultExports;
import io.prometheus.client.logback.InstrumentedAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import space.npstr.sqlsauce.DatabaseWrapper;

/**
 * Created by napster on 18.03.18.
 */
@Component
public class Metrics {

    private static final Logger log = LoggerFactory.getLogger(Metrics.class);

    public Metrics(InstrumentedAppender prometheusAppender, HibernateStatisticsCollector hibernateStats,
                   DatabaseWrapper mainDbWrapper, DatabaseConfiguration dbConfig, DatabaseManager databaseManager) {
        //log metrics
        final LoggerContext factory = (LoggerContext) LoggerFactory.getILoggerFactory();
        final ch.qos.logback.classic.Logger root = factory.getLogger(Logger.ROOT_LOGGER_NAME);
        prometheusAppender.setContext(root.getLoggerContext());
        prometheusAppender.start();
        root.addAppender(prometheusAppender);

        //jvm (hotspot) metrics
        DefaultExports.initialize();

        //hibernate stats
        // 1. ensure all connections have been created
        mainDbWrapper.getName();
        dbConfig.cacheDbConn(databaseManager);
        // 2. register the metrics
        hibernateStats.register();

        log.info("Metrics set up");
    }


    //incoming
    public static final Counter apiRequests = Counter.build()
            .name("fredboat_quarterdeck_api_requests_total")
            .help("Total api calls served")
            .labelNames("path") // like /stats, /metrics, etc
            .register();
}
