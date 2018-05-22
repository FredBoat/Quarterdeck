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

package com.fredboat.backend.quarterdeck.rest.v1.transfer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fredboat.backend.quarterdeck.ratelimit.Rate;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Created by napster on 09.04.18.
 * <p>
 * Summary:
 * Currently there are four generally possible answers for a {@link RatelimitRequest}:
 * - The request is granted
 * - The request is not granted (= the user is ratelimited)
 * - The user is already blacklisted
 * - The request caused the user to be blacklisted
 *
 * @see com.fredboat.backend.quarterdeck.rest.v1.transfer (package-info.java)
 */
public class RatelimitResponse {

    public static final long NOT_BLACKLISTED = -1;
    public static final long IS_BLACKLISTED = 0;

    private static final RatelimitResponse GRANTED = new RatelimitResponse(null, NOT_BLACKLISTED);
    private static final RatelimitResponse ALREADY_BLACKLISTED = new RatelimitResponse(null, IS_BLACKLISTED);

    //information we need to transport:
    // - if a ratelimit was hit, which one was it?
    // - if a blacklisting was issued, for how long?
    @Nullable
    private final Rate rate;
    private final long blacklistedLength;

    /**
     * @param rate
     *         not null if the request was ratelimited
     * @param blacklistedLength
     *         the amount of time for how long a user was blacklisted.
     */
    private RatelimitResponse(@Nullable Rate rate, long blacklistedLength) {
        this.rate = rate;
        this.blacklistedLength = blacklistedLength;
    }

    /**
     * @return a {@link RatelimitResponse} for a granted {@link RatelimitRequest}
     */
    public static RatelimitResponse granted() {
        return GRANTED;
    }

    /**
     * @param rate
     *         The rate that was hit
     *
     * @return a {@link RatelimitResponse} for a ratelimited {@link RatelimitRequest}
     */
    public static RatelimitResponse ratelimited(Rate rate) {
        return new RatelimitResponse(rate, NOT_BLACKLISTED);
    }

    /**
     * @return a {@link RatelimitResponse} for a {@link RatelimitRequest} for a user that was already blacklisted
     */
    public static RatelimitResponse alreadyBlacklisted() {
        return ALREADY_BLACKLISTED;
    }

    /**
     * @param rate
     *         The rate that was hit
     * @param blacklistedLengthMillis
     *         For how long the blacklist has been issued.
     *
     * @return a {@link RatelimitResponse} for a {@link RatelimitRequest} that led to the blacklisting being issued.
     */
    public static RatelimitResponse blacklistIssued(Rate rate, long blacklistedLengthMillis) {
        return new RatelimitResponse(rate, blacklistedLengthMillis);
    }


    /**
     * @return rate is present when a ratelimit was hit, detailing which rate exactly was hit.
     */
    public Optional<Rate> getRate() {
        return Optional.ofNullable(this.rate);
    }

    /**
     * In milliseconds
     * < 0 not blacklisted
     * = 0 is blacklisted, request should be ignored. If more information is needed, send a request to the blacklist endpoint.
     * > 0 a new blacklist request was issued for this length.
     */
    @JsonSerialize(using = ToStringSerializer.class)
    public long getBlacklistedLength() {
        return this.blacklistedLength;
    }
}
