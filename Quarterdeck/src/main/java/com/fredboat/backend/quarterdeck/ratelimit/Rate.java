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

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by napster on 09.04.18.
 */
public enum Rate {

    //@formatter:off
    USER_SHARDS_COMM    ("userShardsComm",      Scope.USER,     2,      TimeUnit.SECONDS.toMillis(30)),
    USER_SKIP_COMM      ("userSkipComm",        Scope.USER,     5,      TimeUnit.SECONDS.toMillis(20)),
    USER_EXPORT_COMM    ("userExportComm",      Scope.USER,     2,      TimeUnit.MINUTES.toMillis(1)),
    USER_ALL_COMMS      ("userAllComms",        Scope.USER,     5,      TimeUnit.SECONDS.toMillis(10)),

    GUILD_WEATHER_COMM  ("guildWeatherComm",    Scope.GUILD,    30,     TimeUnit.MINUTES.toMillis(3)),
    GUILD_SONGS_ADDED   ("guildSongsAdded",     Scope.GUILD,    1000,   TimeUnit.MINUTES.toMillis(2)),
    GUILD_ALL_COMMS     ("guildAllComms",       Scope.GUILD,    10,     TimeUnit.SECONDS.toMillis(10));
    //@formatter:on


    private final String altName;
    private final Scope scope;
    private final int requests;
    private final long timeFrameMillis;

    Rate(String altName, Scope scope, int requests, long timeFrameMillis) {
        this.altName = altName;
        this.scope = scope;
        this.requests = requests;
        this.timeFrameMillis = timeFrameMillis;
    }


    public String getAltName() {
        return this.altName;
    }

    public Scope getScope() {
        return this.scope;
    }

    public int getRequests() {
        return this.requests;
    }

    public long getTimeFrameMillis() {
        return this.timeFrameMillis;
    }

    /**
     * This method tries to parse an input into a ratelimit that we recognize.
     * It will try to make use of the ratelimit enum itself and the alternative name to match the input to a known ratelimit.
     *
     * @param input
     *         input to be parsed into a ratelimit known to us (= defined in this enum)
     *
     * @return the optional ratelimit identified from the input.
     */
    public static Optional<Rate> parse(String input) {
        for (Rate ratelimit : Rate.values()) {
            if (ratelimit.name().equalsIgnoreCase(input)
                    || ratelimit.getAltName().equalsIgnoreCase(input)) {
                return Optional.of(ratelimit);
            }
        }

        return Optional.empty();
    }

    public enum Scope {
        USER,
        GUILD
    }
}
