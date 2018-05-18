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

import com.fredboat.backend.quarterdeck.rest.v1.transfer.DiscordSnowflake;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.RatelimitRequest;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.RatelimitResponse;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import fredboat.util.rest.CacheUtil;
import io.prometheus.client.guava.cache.CacheMetricsCollector;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by napster on 17.04.17.
 * <p>
 * This class uses an algorithm based on leaky bucket, but is optimized, mainly we work around having tons of threads for
 * each bucket filling/emptying it, instead saving timestamps. As a result this class works better for shorter time
 * periods, as the amount of timestamps to hold decreases.
 * some calculations can be found here: https://docs.google.com/spreadsheets/d/1Afdn25AsFD-v3WQGp56rfVwO1y2d105IQk3dtfTcKwA/edit#gid=0
 */
public class Ratelimit {

    private final static Logger log = LoggerFactory.getLogger(Ratelimit.class);


    private final LoadingCache<Long, Bucket> rates;
    private final Rate rate;

    /**
     * @param rate
     *         a definition object that provides a name, scope, allowed requests and a timeframe for this ratelimit to
     *         operate on
     * @param cacheMetrics
     *         metrics collector to register the rates cache of this ratelimit with
     */
    public Ratelimit(Rate rate, CacheMetricsCollector cacheMetrics) {
        this.rates = CacheBuilder.newBuilder()
                .recordStats()
                //we can completely forget the object after this period, the rates would be reset anyways
                .expireAfterAccess(rate.getTimeFrameMillis(), TimeUnit.MILLISECONDS)
                .build(CacheLoader.from(Bucket::new));
        cacheMetrics.addCache(rate.getAltName() + "Ratelimit", this.rates);

        this.rate = rate;
    }

    /**
     * @return a RateResult object containing information whether the users request is rate limited or not and the reason for that
     * <p>
     * Caveat: This allows requests to overstep the ratelimit with single high weight requests.
     * The clearing of timestamps ensures it will take longer for them to get available again though.
     */
    public RatelimitResponse isAllowed(RatelimitRequest request) {
        //This gets called real often, right before every command execution. Keep it light, don't do any blocking stuff,
        //ensure whatever you do in here is threadsafe, but minimize usage of synchronized as it adds overhead
        Optional<DiscordSnowflake> idOpt = request.getUserId();

        //user or guild scope?
        if (this.rate.getScope() == Rate.Scope.GUILD) {
            idOpt = request.getGuildId();
        }

        if (!idOpt.isPresent()) {
            //this ratelimiter does not cover this request.
            return RatelimitResponse.granted();
        }

        long id = idOpt.get().longValue();

        Bucket bucket = CacheUtil.getUncheckedUnwrapped(this.rates, id);
        if (bucket == null) {
            log.warn("Shiver me timbers, cache calling new Bucket({}) returned null", id);
            return RatelimitResponse.granted(); //not expected to happen, let it slip in a user friendly way
        }

        //synchronize on the individual bucket objects since we are about to change and save them
        // we can use these to synchronize because they are backed by a cache, subsequent calls to fetch them
        // will return the same object
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (bucket) {
            long now = System.currentTimeMillis();

            //clear outdated timestamps
            long maxTimeStampsToClear = (now - bucket.lastUpdated) * this.rate.getRequests() / this.rate.getTimeFrameMillis();
            long cleared = 0;
            while (bucket.timeStamps.size() > 0
                    && bucket.timeStamps.getLong(0) + this.rate.getTimeFrameMillis() < now
                    && cleared < maxTimeStampsToClear) {
                bucket.timeStamps.removeLong(0);
                cleared++;
            }

            bucket.lastUpdated = now;
            //granted?
            if (bucket.timeStamps.size() < this.rate.getRequests()) {
                for (int i = 0; i < request.getWeight(); i++)
                    bucket.timeStamps.add(now);
                //everything is fine, get out of this method
                return RatelimitResponse.granted();
            }
        }

        //reaching this point in the code means a rate limit was hit
        //the following code has to handle that

        return RatelimitResponse.ratelimited(this.rate);
    }

    /**
     * completely resets a limit for an id (user or guild for example)
     */
    public synchronized void liftLimit(long id) {
        this.rates.invalidate(id);
    }

    class Bucket {
        //to whom this belongs
        final long id;

        //last time this object was updated
        //useful for keeping track of how many timeStamps should be removed to ensure the limit is enforced
        long lastUpdated;

        //collects the requests
        LongArrayList timeStamps;

        private Bucket(long id) {
            this.id = id;
            this.lastUpdated = System.currentTimeMillis();
            this.timeStamps = new LongArrayList();
        }

        @Override
        public int hashCode() {
            return Long.hashCode(this.id);
        }
    }
}
