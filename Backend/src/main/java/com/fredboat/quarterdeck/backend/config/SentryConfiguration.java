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

package com.fredboat.quarterdeck.backend.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;
import com.fredboat.quarterdeck.backend.config.property.SentryConfig;
import com.fredboat.quarterdeck.backend.info.GitRepoState;
import io.sentry.Sentry;
import io.sentry.logback.SentryAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * Created by napster on 08.03.18.
 */
@Configuration
public class SentryConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SentryConfiguration.class);
    private static final String SENTRY_APPENDER_NAME = "SENTRY";
    private final GitRepoState gitRepoState;

    public SentryConfiguration(SentryConfig sentryConfig, GitRepoState gitRepoState) {
        this.gitRepoState = gitRepoState;

        String dsn = sentryConfig.getDsn();

        //noinspection ConstantConditions
        if (dsn != null && !dsn.isEmpty()) {
            turnOn(dsn);
        } else {
            turnOff();
        }

    }

    private void turnOn(String dsn) {
        log.info("Turning on sentry");
        Sentry.init(dsn).setRelease(this.gitRepoState.getCommitId());
        getSentryLogbackAppender().start();
    }


    private static void turnOff() {
        log.info("Turning off sentry");
        Sentry.close();
        getSentryLogbackAppender().stop();
    }

    //programmatically creates a sentry appender if it doesn't exist yet
    private static synchronized SentryAppender getSentryLogbackAppender() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger root = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);

        SentryAppender sentryAppender = (SentryAppender) root.getAppender(SENTRY_APPENDER_NAME);
        if (sentryAppender == null) {
            sentryAppender = new SentryAppender();
            sentryAppender.setName(SENTRY_APPENDER_NAME);

            ThresholdFilter warningsOrAboveFilter = new ThresholdFilter();
            warningsOrAboveFilter.setLevel(Level.WARN.levelStr);
            warningsOrAboveFilter.start();
            sentryAppender.addFilter(warningsOrAboveFilter);

            sentryAppender.setContext(loggerContext);
            root.addAppender(sentryAppender);
        }
        return sentryAppender;
    }
}
