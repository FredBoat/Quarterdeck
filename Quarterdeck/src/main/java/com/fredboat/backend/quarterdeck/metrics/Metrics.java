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

package com.fredboat.backend.quarterdeck.metrics;

import ch.qos.logback.classic.LoggerContext;
import com.fredboat.backend.quarterdeck.config.DatabaseConfiguration;
import com.fredboat.backend.quarterdeck.db.repositories.api.SearchResultRepo;
import com.fredboat.backend.quarterdeck.metrics.collectors.JCacheCollector;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Summary;
import io.prometheus.client.hibernate.HibernateStatisticsCollector;
import io.prometheus.client.hotspot.DefaultExports;
import io.prometheus.client.logback.InstrumentedAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import space.npstr.sqlsauce.DatabaseWrapper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by napster on 18.03.18.
 */
@Component
public class Metrics {

    private static final Logger log = LoggerFactory.getLogger(Metrics.class);

    //use to schedule "expensive" jobs for collecting metrics that we want decoupled from scrapes.
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(
            r -> new Thread(r, "metrics scheduler")
    );

    public Metrics(InstrumentedAppender prometheusAppender, HibernateStatisticsCollector hibernateStats,
                   DatabaseWrapper mainWrapper, DatabaseConfiguration databaseConfiguration,
                   SearchResultRepo searchResultRepo, JCacheCollector jCacheCollector) {
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
        mainWrapper.getName(); //to avoid unused warnings of the parameter
        databaseConfiguration.getCacheDbWrapper();
        // 2. register the metrics
        hibernateStats.register();

        //custom collectors
        jCacheCollector.register();

        this.scheduler.scheduleAtFixedRate(() -> {
            try {
                searchResultCacheSize.set(searchResultRepo.estimateSize());
            } catch (Exception e) {
                log.error("Failed to estimate track search results size", e);
            }

        }, 0, 5, TimeUnit.MINUTES);

        log.info("Metrics set up");
    }


    //incoming
    public static final Summary apiRequestsExecutionTime = Summary.build()
            .name("fredboat_quarterdeck_api_request_duration_seconds")
            .help("How long a request takes to be executed")
            .labelNames(
                    "path",   // like /stats, /metrics, etc
                    "method"  // GET, POST, etc
            )
            .register();

    public static final Counter apiRequestsNotInstrumented = Counter.build()
            .name("fredboat_quarterdeck_api_requests_not_instrumented_total")
            .help("Api calls that we did not instrument") //meaning the regexes for instrumenting them need a fix. this number should be 0.
            .register();

    public static final Gauge searchResultCacheSize = Gauge.build()
            .name("fredboat_quarterdeck_search_result_cache_size")
            .help("Size of the search result cache")
            .register();


    public static final Counter autoBlacklistsIssued = Counter.build()
            .name("fredboat_quarterdeck_autoblacklists_issued_total")
            .help("How many users were blacklisted on a particular level")
            .labelNames("level") //blacklist level
            .register();
}
