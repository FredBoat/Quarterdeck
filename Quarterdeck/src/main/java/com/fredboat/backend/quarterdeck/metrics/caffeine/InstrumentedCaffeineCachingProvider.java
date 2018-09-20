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

import com.github.benmanes.caffeine.jcache.CacheManagerImpl;
import com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider;

import javax.annotation.concurrent.GuardedBy;
import javax.cache.CacheManager;
import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;

/**
 * Created by napster on 20.09.18.
 * <p>
 * This class ensures that all {@link CacheManagerImpl}s are proxied through {@link InstrumentedCaffeineCacheManager}s
 */
public class InstrumentedCaffeineCachingProvider extends CaffeineCachingProviderProxy {

    @GuardedBy("itself")
    private final Map<CacheManagerImpl, InstrumentedCaffeineCacheManager> cacheManagers = new WeakHashMap<>();

    /**
     * Keep a default constructor around, this class is loaded by the {@link java.util.ServiceLoader}
     */
    public InstrumentedCaffeineCachingProvider() {
        super(new CaffeineCachingProvider());
    }

    @Override
    public CacheManager getCacheManager(URI uri, ClassLoader classLoader, Properties properties) {
        CacheManagerImpl cacheManager = (CacheManagerImpl) super.getCacheManager(uri, classLoader, properties);

        InstrumentedCaffeineCacheManager result;
        synchronized (this.cacheManagers) {
            result = this.cacheManagers.get(cacheManager);
            if (result == null) {
                result = new InstrumentedCaffeineCacheManager(cacheManager, CaffeineCacheMetricsCollectorHolder.get());
                this.cacheManagers.put(cacheManager, result);
            }
        }

        return result;
    }

    @Override
    public CacheManager getCacheManager(URI uri, ClassLoader classLoader) {
        return getCacheManager(uri, classLoader, getDefaultProperties());
    }

    @Override
    public CacheManager getCacheManager() {
        return getCacheManager(getDefaultURI(), getDefaultClassLoader());
    }

}
