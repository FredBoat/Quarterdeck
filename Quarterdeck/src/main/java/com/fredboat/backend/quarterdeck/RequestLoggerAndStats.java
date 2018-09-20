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

import com.fredboat.backend.quarterdeck.metrics.Metrics;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalNotification;
import io.prometheus.client.Counter;
import io.prometheus.client.Summary;
import io.prometheus.client.guava.cache.CacheMetricsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by napster on 19.04.18.
 */
public class RequestLoggerAndStats extends AbstractRequestLoggingFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggerAndStats.class);

    private static final String REQUEST_NUMBER_ATTRIBUTE_KEY = RequestLoggerAndStats.class.getCanonicalName().toLowerCase() + ".requestnumber";

    private final AtomicLong requestCounter = new AtomicLong(0);

    private final Cache<Long, Summary.Timer> timers = CacheBuilder.newBuilder()
            .recordStats()
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .removalListener((RemovalNotification<Long, Summary.Timer> notif) -> {
                if (notif.getCause() == RemovalCause.EXPIRED) {
                    log.warn("A request took over 60 seconds to complete, or forgot to remove its timer");
                    notif.getValue().observeDuration();
                }
            })
            .build();

    private final Counter apiRequestsNotInstrumented;

    public RequestLoggerAndStats(CacheMetricsCollector cacheMetrics, Counter apiRequestsNotInstrumented) {
        this.apiRequestsNotInstrumented = apiRequestsNotInstrumented;
        cacheMetrics.addCache("requestTimers", this.timers);
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        log.debug(message);
        instrumentBeforeApiRequest(request);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        log.debug(message);
        instrumentAfterApiRequest(request);
    }

    //compared via "starts with"
    private static final Set<String> IGNORED_PATHS = Set.of(
            "/swagger-ui.html",
            "/v2/api-docs",
            "/webjars/springfox-swagger-ui",
            "/swagger-resources"
    );

    //compared via "equals"
    private static final Set<String> ACCEPTED_ROOT_PATHS = Set.of(
            "/metrics",
            "/brew",
            "/info/api/versions"
    );

    //v0 paths look like
    // /v0/{entity}/[fetch|delete|merge|getraw]
    //
    // https://regex101.com/r/cUrr2d/2
    private static final Pattern V0_ALL_PATHS_REGEX
            = Pattern.compile("^/v0/[a-z]+/(?:fetch|merge|delete|getraw|getmaxaged)$");

    //v1 paths look like
    // /v1/guilds/{guildId}/{endpoint}
    // + a few special cases
    //
    // https://regex101.com/r/QKUTbi/1
    // group 1: path before the guildId
    // group 2: path after the guildId
    private static final Pattern V1_GUILDS_PATHS_REGEX
            = Pattern.compile("^(/v1/guilds/)[0-9]+/([a-z]+)$");

    // https://regex101.com/r/nwWAvn/1/
    // group 1: the controller path, which is enough with these currently
    private static final Pattern V1_BLACKLIST_AND_RATELIMIT_PATHS_REGEX
            = Pattern.compile("^(/v1/(?:ratelimit|blacklist))/?[0-9]*$");

    // https://regex101.com/r/h8JNt9/1
    // group 1: path before the guildId
    // group 2: module specific path after the guildId
    private static final Pattern V1_GUILD_MODULES_REGEX
            = Pattern.compile("^(/v1/guilds/)[0-9]+/(modules/[a-z]+)$");

    // https://regex101.com/r/vmf6YG/1
    // group 1: path before the guildId
    // group 2: permissions specific path between guildId and userId
    private static final Pattern V1_GUILD_PERMISSIONS_REGEX
            = Pattern.compile("^(/v1/guilds/)[0-9]+/(permissions/[a-z]+)/[0-9]+$");

    // https://regex101.com/r/ERsucr/1/
    // group 1: path before the search term
    private static final Pattern V1_TRACKS_REGEX
            = Pattern.compile("^(/v1/tracks/search/[a-z]+)/.+$");

    /**
     * Only instrument paths of known complexity to avoid an explosion of metric samples due to path variables.
     */
    private void instrumentBeforeApiRequest(HttpServletRequest request) {
        String servletPath = request.getServletPath().toLowerCase();
        if (servletPath.isEmpty()) { //this can happen in tests
            servletPath = request.getRequestURI().toLowerCase();
        }

        for (String ignored : IGNORED_PATHS) {
            if (servletPath.startsWith(ignored)) {
                return;
            }
        }

        for (String ok : ACCEPTED_ROOT_PATHS) {
            if (servletPath.equalsIgnoreCase(ok)) {
                countIt(servletPath, request);
                return;
            }
        }


        if (V0_ALL_PATHS_REGEX.matcher(servletPath).matches()) {
            countIt(servletPath, request);
            return;
        }

        Matcher v1GuildsMatcher = V1_GUILDS_PATHS_REGEX.matcher(servletPath);
        if (v1GuildsMatcher.matches()) {
            String invariantPath = v1GuildsMatcher.group(1) + v1GuildsMatcher.group(2);
            countIt(invariantPath, request);
            return;
        }

        Matcher v1BlacklistRatelimitMatcher = V1_BLACKLIST_AND_RATELIMIT_PATHS_REGEX.matcher(servletPath);
        if (v1BlacklistRatelimitMatcher.matches()) {
            String invariantPath = v1BlacklistRatelimitMatcher.group(1);
            countIt(invariantPath, request);
            return;
        }

        Matcher v1GuildModulesMatcher = V1_GUILD_MODULES_REGEX.matcher(servletPath);
        if (v1GuildModulesMatcher.matches()) {
            String invariantPath = v1GuildModulesMatcher.group(1) + v1GuildModulesMatcher.group(2);
            countIt(invariantPath, request);
            return;
        }

        Matcher v1GuildPermissionsMatcher = V1_GUILD_PERMISSIONS_REGEX.matcher(servletPath);
        if (v1GuildPermissionsMatcher.matches()) {
            String invariantPath = v1GuildPermissionsMatcher.group(1) + v1GuildPermissionsMatcher.group(2);
            countIt(invariantPath, request);
            return;
        }

        Matcher v1TracksMatcher = V1_TRACKS_REGEX.matcher(servletPath);
        if (v1TracksMatcher.matches()) {
            String invariantPath = v1TracksMatcher.group(1);
            countIt(invariantPath, request);
            return;
        }


        log.debug("Did not instrument unknown path: {}", servletPath);
        this.apiRequestsNotInstrumented.inc();
    }

    private void countIt(String sanitizedPath, HttpServletRequest request) {
        Summary.Timer timer = Metrics.apiRequestsExecutionTime.labels(sanitizedPath, request.getMethod()).startTimer();
        long requestNumber = this.requestCounter.incrementAndGet();
        request.setAttribute(REQUEST_NUMBER_ATTRIBUTE_KEY, requestNumber);
        this.timers.put(requestNumber, timer);
    }

    private void instrumentAfterApiRequest(HttpServletRequest request) {
        Object requestNumber = request.getAttribute(REQUEST_NUMBER_ATTRIBUTE_KEY);
        if (requestNumber == null) {
            return; //not an instrumented request
        }
        Optional.ofNullable(this.timers.getIfPresent(requestNumber)).ifPresent(Summary.Timer::observeDuration);
        this.timers.invalidate(requestNumber);
    }
}
