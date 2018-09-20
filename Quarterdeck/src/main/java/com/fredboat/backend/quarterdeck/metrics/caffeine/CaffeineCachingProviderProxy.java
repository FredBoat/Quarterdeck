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

import com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider;

import javax.cache.CacheManager;
import javax.cache.configuration.OptionalFeature;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Properties;

/**
 * Created by napster on 20.09.18.
 * <p>
 * Just a proxy, no logic in here. See {@link InstrumentedCaffeineCacheManager} for the actual logic.
 * We can't just extend the {@link CaffeineCachingProvider} directly, because it is a final class.
 */
public class CaffeineCachingProviderProxy implements CachingProvider {

    protected final CaffeineCachingProvider delegate;

    public CaffeineCachingProviderProxy(CaffeineCachingProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    public CacheManager getCacheManager(URI uri, ClassLoader classLoader, Properties properties) {
        return this.delegate.getCacheManager(uri, classLoader, properties);
    }

    @Override
    public ClassLoader getDefaultClassLoader() {
        return this.delegate.getDefaultClassLoader();
    }

    @Override
    public URI getDefaultURI() {
        return this.delegate.getDefaultURI();
    }

    @Override
    public Properties getDefaultProperties() {
        return this.delegate.getDefaultProperties();
    }

    @Override
    public CacheManager getCacheManager(URI uri, ClassLoader classLoader) {
        return this.delegate.getCacheManager(uri, classLoader);
    }

    @Override
    public CacheManager getCacheManager() {
        return this.delegate.getCacheManager();
    }

    @Override
    public void close() {
        this.delegate.close();
    }

    @Override
    public void close(ClassLoader classLoader) {
        this.delegate.close(classLoader);
    }

    @Override
    public void close(URI uri, ClassLoader classLoader) {
        this.delegate.close(uri, classLoader);
    }

    @Override
    public boolean isSupported(OptionalFeature optionalFeature) {
        return this.delegate.isSupported(optionalFeature);
    }
}
