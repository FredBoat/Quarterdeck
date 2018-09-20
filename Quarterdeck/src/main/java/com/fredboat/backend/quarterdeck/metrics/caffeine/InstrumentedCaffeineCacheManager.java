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

package com.fredboat.backend.quarterdeck.metrics.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.jcache.CacheManagerImpl;
import com.github.benmanes.caffeine.jcache.CacheProxy;
import io.prometheus.client.cache.caffeine.CacheMetricsCollector;

import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.cache.configuration.Configuration;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Created by napster on 20.09.18.
 * <p>
 * This class ensures that all caches created will be registered in our cache metrics.
 */
public class InstrumentedCaffeineCacheManager extends CaffeineCacheManagerProxy {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(InstrumentedCaffeineCacheManager.class);

    private final CacheMetricsCollector cacheMetrics;
    @GuardedBy("itself")
    private final Map<String, WeakReference<Cache<?, ?>>> instrumentedCaches = new HashMap<>();

    public InstrumentedCaffeineCacheManager(CacheManagerImpl delegate, CacheMetricsCollector cacheMetrics) {
        super(delegate);
        this.cacheMetrics = cacheMetrics;
    }

    @Override
    public <K, V, C extends Configuration<K, V>> javax.cache.Cache<K, V> createCache(String cacheName, C configuration) {
        javax.cache.Cache<K, V> cache = super.createCache(cacheName, configuration);
        @SuppressWarnings("unchecked") Cache<K, V> caffeineCache = cache.unwrap(Cache.class);

        instrumentCache(cache.getName(), caffeineCache);
        return cache;
    }

    @Override
    public @Nullable <K, V> CacheProxy<K, V> getCache(String cacheName) {
        CacheProxy<K, V> cache = super.getCache(cacheName);
        if (cache == null) return null;
        @SuppressWarnings("unchecked") Cache<K, V> caffeineCache = cache.unwrap(Cache.class);

        instrumentCache(cache.getName(), caffeineCache);
        return cache;
    }

    /**
     * Copy of {@link CacheManagerImpl#getCache(String, Class, Class)} to ensure its call to {@link javax.cache.CacheManager#getCache(String)}
     * is handled by this class.
     */
    @Override
    public @Nullable <K, V> javax.cache.Cache<K, V> getCache(
            String cacheName, Class<K> keyType, Class<V> valueType) {
        CacheProxy<K, V> cache = getCache(cacheName);
        if (cache == null) {
            return null;
        }
        requireNonNull(keyType);
        requireNonNull(valueType);

        Configuration<?, ?> config = cache.getConfiguration();
        if (keyType != config.getKeyType()) {
            throw new ClassCastException("Incompatible cache key types specified, expected " +
                    config.getKeyType() + " but " + keyType + " was specified");
        } else if (valueType != config.getValueType()) {
            throw new ClassCastException("Incompatible cache value types specified, expected " +
                    config.getValueType() + " but " + valueType + " was specified");
        }
        return cache;
    }

    @Override
    public void destroyCache(String cacheName) {
        super.destroyCache(cacheName);
        synchronized (this.instrumentedCaches) {
            this.cacheMetrics.removeCache(cacheName);
            this.instrumentedCaches.remove(cacheName);
        }
    }

    @Override
    public void close() {
        super.close();
        synchronized (this.instrumentedCaches) {
            this.instrumentedCaches.forEach((name, cache) -> this.cacheMetrics.removeCache(name));
            this.instrumentedCaches.clear();
        }
    }

    private void instrumentCache(String name, Cache<?, ?> cache) {
        synchronized (this.instrumentedCaches) {
            Cache<?, ?> instrumentedCache = null;
            WeakReference<Cache<?, ?>> cacheWeakReference = this.instrumentedCaches.get(name);
            if (cacheWeakReference != null)
                instrumentedCache = cacheWeakReference.get();

            if (instrumentedCache == null || cache != instrumentedCache) { //instance comparison
                log.info("Instrumenting cache {}", name);
                this.cacheMetrics.addCache(name, cache);
                this.instrumentedCaches.put(name, new WeakReference<>(cache));
            }
        }
    }
}
