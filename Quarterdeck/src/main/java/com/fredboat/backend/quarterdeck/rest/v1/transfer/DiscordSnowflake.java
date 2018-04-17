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

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by napster on 01.04.18.
 */
public class DiscordSnowflake {

    private final long snowflakeId;

    public DiscordSnowflake(long snowflakeId) {
        this.snowflakeId = snowflakeId;
    }

    /**
     * @throws NumberFormatException
     *         if the passed snowflakeId is not an unsigned long
     */
    public DiscordSnowflake(String snowflakeId) throws NumberFormatException {
        this.snowflakeId = Long.parseUnsignedLong(snowflakeId);
    }

    @JsonValue
    public String getSnowflakeId() {
        return Long.toString(this.snowflakeId);
    }

    public long longValue() {
        return this.snowflakeId;
    }

    @Override
    public String toString() {
        return getSnowflakeId();
    }
}
