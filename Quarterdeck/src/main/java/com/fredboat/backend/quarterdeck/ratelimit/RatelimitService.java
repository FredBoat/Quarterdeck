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

package com.fredboat.backend.quarterdeck.ratelimit;

import com.fredboat.backend.quarterdeck.config.property.WhitelistConfig;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.DiscordSnowflake;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.RatelimitRequest;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.RatelimitResponse;
import io.prometheus.client.guava.cache.CacheMetricsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by napster on 17.04.17.
 * <p>
 * This object is threadsafe.
 * <p>
 * http://i.imgur.com/ha0R3XZ.gif
 */
@Component
public class RatelimitService {

    private static final Logger log = LoggerFactory.getLogger(RatelimitService.class);

    private final Map<Rate, Ratelimit> ratelimits;
    private final BlacklistService blacklistService;
    private final Set<Long> whitelist;

    public RatelimitService(BlacklistService blacklistService, CacheMetricsCollector cacheMetrics,
                            WhitelistConfig whitelistConfig) {
        this.blacklistService = blacklistService;
        this.whitelist = Collections.unmodifiableSet(whitelistConfig.getWhitelist());

        //Create all the rate limiters we have defined
        this.ratelimits = new EnumMap<>(Rate.class);
        for (Rate rate : Rate.values()) {
            this.ratelimits.put(rate, new Ratelimit(rate, cacheMetrics));
        }
    }

    /**
     * @param request
     *         request containing further information like which ratelimits to check, the weight of the request, and
     *         the user and guild sources of the request.
     *
     * @return A {@link RatelimitResponse} that contains further information, whether this request is granted,
     * if the requesting entity should be ratelimited, or even blacklisted. The client needs to take care of messaging
     * the user with the appropriate output.
     */
    public RatelimitResponse isRatelimited(RatelimitRequest request) {
        Optional<DiscordSnowflake> userId = request.getUserId();

        if (userId.isPresent()) {
            if (this.whitelist.contains(userId.get().longValue())) {
                return RatelimitResponse.granted();
            }
            if (this.blacklistService.blacklistedUntil(userId.get().longValue()) > System.currentTimeMillis()) {
                return RatelimitResponse.alreadyBlacklisted();
            }
        }

        RatelimitResponse response = RatelimitResponse.granted();
        for (Rate rate : request.getRates()) {
            Ratelimit ratelimit = this.ratelimits.get(rate);
            if (ratelimit == null) {
                log.error("Rate {} has no corresponding ratelimit. Did the initialazation go wrong?", rate);
                return RatelimitResponse.granted();
            }

            response = ratelimit.isAllowed(request);

            //hit a ratelimit? check the user for being blacklisted.
            if (response.getRate().isPresent()
                    && rate.getScope() == Rate.Scope.USER // only blacklist users, not guilds
                    && userId.isPresent()) {
                long blacklistLength = this.blacklistService.hitRateLimit(userId.get().longValue());
                if (blacklistLength > 0) {
                    response = RatelimitResponse.blacklistIssued(response.getRate().get(), blacklistLength);
                }
            }

            if (response.getBlacklistedLength() > RatelimitResponse.IS_BLACKLISTED
                    || response.getRate().isPresent()) {
                return response;//return early if the user is blacklisted / ratelimited
            }
        }
        return response;
    }

    /**
     * Reset rate limits for the given id and removes it from the blacklist
     */
    public void liftLimit(long id) {
        for (Ratelimit ratelimit : this.ratelimits.values()) {
            ratelimit.liftLimit(id);
        }
    }
}
