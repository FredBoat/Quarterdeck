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
import com.fredboat.backend.quarterdeck.ratelimit.BlacklistService;

/**
 * Created by napster on 09.04.18.
 * <p>
 * Answers a request
 */
public class BlacklistEntry {

    private final DiscordSnowflake snowflakeId;
    private final int level;
    private final long blacklistedUntil;

    private BlacklistEntry(DiscordSnowflake snowflakeId, int level, long blacklistedUntil) {
        this.snowflakeId = snowflakeId;
        this.level = level;
        this.blacklistedUntil = blacklistedUntil;
    }

    public static BlacklistEntry of(com.fredboat.backend.quarterdeck.db.entities.main.BlacklistEntry entry) {
        return new BlacklistEntry(new DiscordSnowflake(entry.getId()), entry.getLevel(),
                entry.getBlacklistedTimestamp() + BlacklistService.getBlacklistTimeLength(entry.getLevel()));
    }

    public DiscordSnowflake getSnowflakeId() {
        return this.snowflakeId;
    }

    public int getLevel() {
        return this.level;
    }

    /**
     * @return epoch millis
     */
    @JsonSerialize(using = ToStringSerializer.class)
    public long getBlacklistedUntil() {
        return this.blacklistedUntil;
    }
}
