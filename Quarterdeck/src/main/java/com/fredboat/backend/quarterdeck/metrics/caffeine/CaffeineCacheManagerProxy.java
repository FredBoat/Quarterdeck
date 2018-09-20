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
import com.github.benmanes.caffeine.jcache.CacheProxy;

import javax.annotation.Nullable;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Properties;

/**
 * Created by napster on 20.09.18.
 * <p>
 * Just a proxy, no logic in here. See {@link InstrumentedCaffeineCachingProvider} for the actual logic.
 * We can't just extend the {@link CacheManagerImpl} directly, because it is a final class.
 */
public class CaffeineCacheManagerProxy implements CacheManager {

    protected final CacheManagerImpl delegate;

    public CaffeineCacheManagerProxy(CacheManagerImpl delegate) {
        this.delegate = delegate;
    }

    @Override
    public CachingProvider getCachingProvider() {
        return this.delegate.getCachingProvider();
    }

    @Override
    public URI getURI() {
        return this.delegate.getURI();
    }

    @Override
    public @Nullable ClassLoader getClassLoader() {
        return this.delegate.getClassLoader();
    }

    @Override
    public Properties getProperties() {
        return this.delegate.getProperties();
    }

    @Override
    public <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(String cacheName, C configuration)
            throws IllegalArgumentException {

        return this.delegate.createCache(cacheName, configuration);
    }

    @Override
    public @Nullable <K, V> Cache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType) {
        return this.delegate.getCache(cacheName, keyType, valueType);
    }

    @Override
    public @Nullable <K, V> CacheProxy<K, V> getCache(String cacheName) {
        return this.delegate.getCache(cacheName);
    }

    @Override
    public Iterable<String> getCacheNames() {
        return this.delegate.getCacheNames();
    }

    @Override
    public void destroyCache(String cacheName) {
        this.delegate.destroyCache(cacheName);
    }

    @Override
    public void enableManagement(String cacheName, boolean enabled) {
        this.delegate.enableManagement(cacheName, enabled);
    }

    @Override
    public void enableStatistics(String cacheName, boolean enabled) {
        this.delegate.enableStatistics(cacheName, enabled);
    }

    @Override
    public void close() {
        this.delegate.close();
    }

    @Override
    public boolean isClosed() {
        return this.delegate.isClosed();
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        return this.delegate.unwrap(clazz);
    }
}
