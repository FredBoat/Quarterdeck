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

package com.fredboat.backend.quarterdeck.config.property;

import com.fredboat.backend.quarterdeck.config.InvalidConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

/**
 * Created by napster on 03.03.18.
 */
@Component
@ConfigurationProperties(prefix = "database")
@SuppressWarnings("unused")
public class DatabaseConfigProperties implements DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfigProperties.class);

    private Db main = new Db();
    private Db cache = new Db();

    @Override
    public String getMainJdbcUrl() {
        String jdbcUrl = this.main.getJdbcUrl();
        //noinspection ConstantConditions
        if (jdbcUrl == null || jdbcUrl.isEmpty()) {
            if ("docker".equals(System.getenv("ENV"))) {
                log.info("No main JDBC URL found, docker environment detected. Using default docker main JDBC url");
                jdbcUrl = "jdbc:postgresql://db:5432/fredboat?user=fredboat";
                this.main.setJdbcUrl(jdbcUrl);
            } else {
                String message = "No main jdbcUrl provided in a non-docker environment. FredBoat cannot work without a database.";
                log.error(message);
                throw new InvalidConfigurationException(message);
            }
        }
        return jdbcUrl;
    }

    private boolean hasWarnedEmptyCacheJdbc = false;
    private boolean hasWarnedSameJdbcs = false;


    @Nullable
    @Override
    public String getCacheJdbcUrl() {
        String jdbcUrl = this.cache.getJdbcUrl();
        //noinspection ConstantConditions
        if (jdbcUrl == null || jdbcUrl.isEmpty()) {
            if ("docker".equals(System.getenv("ENV"))) {
                log.info("No cache jdbcUrl found, docker environment detected. Using default docker cache JDBC url");
                jdbcUrl = "jdbc:postgresql://db:5432/fredboat_cache?user=fredboat";
                this.cache.setJdbcUrl(jdbcUrl);
            } else {
                if (!this.hasWarnedEmptyCacheJdbc) {
                    log.warn("No cache jdbcUrl provided in a non-docker environment. This may lead to a degraded performance, "
                            + "especially in a high usage environment, or when using Spotify playlists.");
                    this.hasWarnedEmptyCacheJdbc = true;
                }
                jdbcUrl = "";
            }
        }

        if (!jdbcUrl.isEmpty() && jdbcUrl.equals(getMainJdbcUrl())) {
            if (!this.hasWarnedSameJdbcs) {
                log.warn("The main and cache jdbc urls may not point to the same database due to how flyway handles migrations. "
                        + "The cache database will not be available in this execution of FredBoat. This may lead to a degraded performance, "
                        + "especially in a high usage environment, or when using Spotify playlists.");
                this.hasWarnedSameJdbcs = true;
            }
            jdbcUrl = "";
        }

        return !jdbcUrl.isEmpty() ? jdbcUrl : null;
    }

    public void setMain(Db main) {
        this.main = main;
    }

    public void setCache(Db cache) {
        this.cache = cache;
    }

    protected static class Db {
        private String jdbcUrl = "";

        public String getJdbcUrl() {
            return this.jdbcUrl;
        }

        public void setJdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }
    }
}
