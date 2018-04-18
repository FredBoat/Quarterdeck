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

package com.fredboat.backend.quarterdeck.config;

import com.fredboat.backend.quarterdeck.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by napster on 17.02.18.
 */
@Configuration
public class RequestLoggerConfiguration {

    @Bean
    public AbstractRequestLoggingFilter logFilter() {
        RequestLogger filter = new RequestLogger();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        return filter;
    }

    private static class RequestLogger extends AbstractRequestLoggingFilter {
        private static final Logger log = LoggerFactory.getLogger(RequestLogger.class);

        @Override
        protected void beforeRequest(HttpServletRequest request, String message) {
            log.debug(message);
            instrumentApiRequest(request);
        }

        @Override
        protected void afterRequest(HttpServletRequest request, String message) {
            log.debug(message);
        }


        //compared via "starts with"
        private final Set<String> IGNORED_PATHS = Set.of(
                "/swagger-ui.html",
                "/v2/api-docs",
                "/webjars/springfox-swagger-ui",
                "/swagger-resources"
        );

        //compared via "equals"
        private final Set<String> ACCEPTED_ROOT_PATHS = Set.of(
                "/metrics",
                "/brew",
                "/info/api/versions"
        );

        //v0 paths look like
        // /v0/{entity}/[fetch|delete|merge|getraw]
        //
        // https://regex101.com/r/cUrr2d/2
        private final Pattern V0_ALL_PATHS_REGEX = Pattern.compile("^/v0/[a-z]+/(?:fetch|merge|delete|getraw|getmaxaged)$");

        //v1 paths look like
        // /v1/guilds/{guildId}/{endpoint}
        // + a few special cases
        //
        // https://regex101.com/r/QKUTbi/1
        // group 1: everything before the guildId
        // group 2: everything after the guildId
        private final Pattern V1_GUILDS_PATHS_REGEX = Pattern.compile("^(/v1/guilds/)[0-9]+/([a-z]+)$");


        /**
         * Only instrument paths of known complexity to avoid an explosion of metric samples due to path variables.
         */
        private void instrumentApiRequest(HttpServletRequest request) {
            String servletPath = request.getServletPath().toLowerCase();

            for (String ignored : this.IGNORED_PATHS) {
                if (servletPath.startsWith(ignored)) {
                    return;
                }
            }

            for (String ok : this.ACCEPTED_ROOT_PATHS) {
                if (servletPath.equalsIgnoreCase(ok)) {
                    Metrics.apiRequests.labels(servletPath).inc();
                    return;
                }
            }


            if (this.V0_ALL_PATHS_REGEX.matcher(servletPath).matches()) {
                Metrics.apiRequests.labels(servletPath).inc();
                return;
            }

            Matcher v1GuildsMatcher = this.V1_GUILDS_PATHS_REGEX.matcher(servletPath);
            if (v1GuildsMatcher.matches()) {
                String invariantPath = v1GuildsMatcher.group(1) + v1GuildsMatcher.group(2);
                Metrics.apiRequests.labels(invariantPath).inc();
                return;
            }

            //todo v1 paths are not done yet

            log.debug("Did not instrument unknown path: " + servletPath);
            Metrics.apiRequestsNotInstrumented.inc();
        }
    }
}
