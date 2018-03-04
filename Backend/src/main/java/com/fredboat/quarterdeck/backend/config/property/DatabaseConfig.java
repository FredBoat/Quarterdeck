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

package com.fredboat.quarterdeck.backend.config.property;

import space.npstr.sqlsauce.ssh.SshTunnel;

import javax.annotation.Nullable;

/**
 * Created by napster on 19.02.18.
 */
public interface DatabaseConfig {

    /**
     * JdbcUrl of the main database
     */
    String getMainJdbcUrl();

    /**
     * @return may return null if no tunnel shall be created for the main database connection
     */
    @Nullable
    SshTunnel.SshDetails getMainSshTunnelConfig();

    /**
     * @return JdbcUrl of the cache database, may return null if no cache database was provided.
     */
    @Nullable
    String getCacheJdbcUrl();

    /**
     * @return may return null if no tunnel shall be created for the cache database connection
     */
    @Nullable
    SshTunnel.SshDetails getCacheSshTunnelConfig();

    /**
     * @return database connection poolsize
     */
    default int getHikariPoolSize() {
        //more database connections don't help with performance, so use a value based on available cores, but not too low
        //http://www.dailymotion.com/video/x2s8uec_oltp-performance-concurrent-mid-tier-connections_tech
        return Math.max(4, Runtime.getRuntime().availableProcessors());
    }
}
