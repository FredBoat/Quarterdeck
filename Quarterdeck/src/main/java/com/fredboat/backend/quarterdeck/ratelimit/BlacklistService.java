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

import com.fredboat.backend.quarterdeck.Metrics;
import com.fredboat.backend.quarterdeck.config.property.WhitelistConfig;
import com.fredboat.backend.quarterdeck.db.entities.main.BlacklistEntry;
import com.fredboat.backend.quarterdeck.db.repositories.api.BlacklistRepo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * Created by napster on 17.04.17.
 * <p>
 * Provides a forgiving blacklist with progressively increasing blacklist lengths
 */
@Component
public class BlacklistService {

    //this holds progressively increasing lengths of blacklisting in milliseconds
    public static final List<Long> BLACKLIST_LEVELS = List.of(
            1000L * 60,                     //one minute
            1000L * 600,                    //ten minutes
            1000L * 3600,                   //one hour
            1000L * 3600 * 24,              //24 hours
            1000L * 3600 * 24 * 7           //a week
    );

    public static final int RATE_LIMIT_HITS_BEFORE_BLACKLIST = 10;

    //users that can never be blacklisted
    private final Set<Long> userWhiteList;

    private final BlacklistRepo blacklistRepo;


    public BlacklistService(BlacklistRepo blacklistRepo, WhitelistConfig whitelistConfig) {
        this.blacklistRepo = blacklistRepo;
        this.userWhiteList = Collections.unmodifiableSet(whitelistConfig.getWhitelist());
    }

    /**
     * @param id
     *         check whether this id is blacklisted
     *
     * @return the epoch millis time stamp of the end of the last blacklisting period. may be 0 if id was never blacklisted.
     */
    //This will be called really often, should be able to be accessed non-synchronized for performance
    // -> don't do any writes in here
    // -> don't call expensive methods
    public long blacklistedUntil(long id) {

        //first of all, ppl that can never get blacklisted no matter what
        if (this.userWhiteList.contains(id)) {
            return 0;
        }

        BlacklistEntry blEntry = this.blacklistRepo.fetch(id);
        if (blEntry.getLevel() < 0) {
            return 0; //blacklist entry exists, but id hasn't actually been blacklisted yet
        }

        return blEntry.getBlacklistedTimestamp() + (getBlacklistTimeLength(blEntry.getLevel()));
    }

    /**
     * @return length if issued blacklisting, 0 if none has been issued
     */
    public long hitRateLimit(long id) {
        if (this.userWhiteList.contains(id)) {
            return 0;
        }
        AtomicLong blacklistingLength = new AtomicLong(0);
        Function<BlacklistEntry, BlacklistEntry> hitRatelimit = blEntry -> {
            long now = System.currentTimeMillis();

            //is the last ratelimit hit a long time away (1 hour)? then reset the ratelimit hits
            if (now - blEntry.getRateLimitReachedTimestamp() > 60 * 60 * 1000) {
                blEntry.setRateLimitReached(0);
            }
            blEntry.incRateLimitReached();
            blEntry.setRateLimitReachedTimestamp(now);
            if (blEntry.getRateLimitReached() >= RATE_LIMIT_HITS_BEFORE_BLACKLIST) {
                //issue blacklist incident
                blEntry.incLevel();
                if (blEntry.getLevel() < 0) blEntry.setLevel(0);
                Metrics.autoBlacklistsIssued.labels(Integer.toString(blEntry.getLevel())).inc();
                blEntry.setBlacklistedTimestamp(now);
                blEntry.setRateLimitReached(0); //reset these for the next time

                blacklistingLength.set(getBlacklistTimeLength(blEntry.getLevel()));
            }

            return blEntry;
        };

        this.blacklistRepo.transform(id, hitRatelimit);
        return blacklistingLength.get();
    }

    /**
     * @return the BlacklistEntry for the provided id
     */
    public BlacklistEntry getBlacklistEntry(long id) {
        return this.blacklistRepo.fetch(id);
    }

    /**
     * completely resets a blacklist for an id
     */
    public void liftBlacklist(long id) {
        this.blacklistRepo.delete(id);
    }

    /**
     * Return length of a blacklist incident in milliseconds depending on the blacklist level
     */
    public static long getBlacklistTimeLength(int blacklistLevel) {
        if (blacklistLevel < 0) return 0;
        return blacklistLevel >= BLACKLIST_LEVELS.size()
                ? BLACKLIST_LEVELS.get(BLACKLIST_LEVELS.size() - 1)
                : BLACKLIST_LEVELS.get(blacklistLevel);
    }
}
