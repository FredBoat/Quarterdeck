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

package com.fredboat.backend.quarterdeck.metrics.collectors;

import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;
import org.springframework.stereotype.Component;

import javax.cache.management.CacheStatisticsMXBean;
import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by napster on 22.09.18.
 */
@Component
public class JCacheCollector extends Collector {

    private static final int MICROSECONDS_PER_SECOND = 1000_000;
    private static final String CACHE_BEAN_COORDINATES = "javax.cache:type=CacheStatistics,CacheManager=*,Cache=*";

    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> mfs = new ArrayList<>();
        List<String> labelNames = Collections.singletonList("cache");

        // ommitted:
        // hitPercentage  because it can be calculated as hits   divided by gets * 100
        // missPercentage because it can be calculated as misses divided by gets * 100.

        CounterMetricFamily hits = new CounterMetricFamily(
                "jcache_hits_total",
                "The number of get requests that were satisfied by the cache.",
                labelNames
        );
        mfs.add(hits);

        CounterMetricFamily misses = new CounterMetricFamily(
                "jcache_misses_total",
                "A miss is a get request that is not satisfied.",
                labelNames
        );
        mfs.add(misses);

        CounterMetricFamily gets = new CounterMetricFamily(
                "jcache_gets_total",
                "The total number of requests to the cache. This will be equal to the sum of the hits and misses.",
                labelNames
        );
        mfs.add(gets);

        CounterMetricFamily puts = new CounterMetricFamily(
                "jcache_puts_total",
                "The total number of puts to the cache.",
                labelNames
        );
        mfs.add(puts);

        CounterMetricFamily removals = new CounterMetricFamily(
                "jcache_removals_total",
                "The total number of removals from the cache. This does not include evictions, "
                        + "where the cache itself initiates the removal to make space.",
                labelNames
        );
        mfs.add(removals);

        CounterMetricFamily evictions = new CounterMetricFamily(
                "jcache_evictions_total",
                "The total number of evictions from the cache. An eviction is a removal "
                        + "initiated by the cache itself to free up space. An eviction is not treated as "
                        + "a removal and does not appear in the removal counts.",
                labelNames
        );
        mfs.add(evictions);

        GaugeMetricFamily averageGetTime = new GaugeMetricFamily(
                "jcache_average_get_seconds",
                "The mean time to execute gets.",
                labelNames
        );
        mfs.add(averageGetTime);

        GaugeMetricFamily averagePutTime = new GaugeMetricFamily(
                "jcache_average_put_seconds",
                "The mean time to execute puts.",
                labelNames
        );
        mfs.add(averagePutTime);

        GaugeMetricFamily averageRemoveTime = new GaugeMetricFamily(
                "jcache_average_remove_seconds",
                "The mean time to execute removes.",
                labelNames
        );
        mfs.add(averageRemoveTime);

        for (Map.Entry<String, CacheStatisticsMXBean> cacheBeanEntry : getCacheBeans().entrySet()) {
            String cacheName = cacheBeanEntry.getKey();
            CacheStatisticsMXBean cacheBean = cacheBeanEntry.getValue();

            List<String> labels = Collections.singletonList(cacheName);

            hits.addMetric(labels, cacheBean.getCacheHits());
            misses.addMetric(labels, cacheBean.getCacheMisses());
            gets.addMetric(labels, cacheBean.getCacheGets());
            puts.addMetric(labels, cacheBean.getCachePuts());
            removals.addMetric(labels, cacheBean.getCacheRemovals());
            evictions.addMetric(labels, cacheBean.getCacheEvictions());
            averageGetTime.addMetric(labels, cacheBean.getAverageGetTime() / MICROSECONDS_PER_SECOND);
            averagePutTime.addMetric(labels, cacheBean.getAveragePutTime() / MICROSECONDS_PER_SECOND);
            averageRemoveTime.addMetric(labels, cacheBean.getAverageRemoveTime() / MICROSECONDS_PER_SECOND);
        }

        return mfs;
    }

    private Map<String, CacheStatisticsMXBean> getCacheBeans() {
        final MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName;
        try {
            objectName = ObjectName.getInstance(CACHE_BEAN_COORDINATES);
        } catch (MalformedObjectNameException e) {
            throw new IllegalStateException("Illegal ObjectName '" + CACHE_BEAN_COORDINATES
                    + "', failed to get CacheStatisticsMXBeans", e);
        }
        Set<ObjectInstance> objectInstances = beanServer.queryMBeans(objectName, null);

        Map<String, CacheStatisticsMXBean> result = new HashMap<>();
        objectInstances.forEach(cacheBean -> {
            CacheStatisticsMXBean cacheStatisticsMXBean = MBeanServerInvocationHandler.newProxyInstance(
                    beanServer,
                    cacheBean.getObjectName(),
                    CacheStatisticsMXBean.class,
                    false
            );
            String cacheName = cacheBean.getObjectName().getKeyProperty("Cache");
            result.put(cacheName, cacheStatisticsMXBean);
        });
        return result;
    }
}
