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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fredboat.backend.quarterdeck.ratelimit.Rate;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Created by napster on 09.04.18.
 */
public class RatelimitRequest {

    //which rates to check
    private final List<Rate> rates;
    @Nullable
    private final DiscordSnowflake userId;
    @Nullable
    private final DiscordSnowflake guildId;
    private final int weight;

    public RatelimitRequest(@JsonProperty("rates") Rate[] rates,
                            @JsonProperty("userId") @Nullable DiscordSnowflake userId,
                            @JsonProperty("guildId") @Nullable DiscordSnowflake guildId,
                            @JsonProperty("weight") int weight) {
        this.rates = List.of(rates);
        this.userId = userId;
        this.guildId = guildId;
        this.weight = weight;
    }


    public List<Rate> getRates() {
        return this.rates;
    }

    public Optional<DiscordSnowflake> getUserId() {
        return Optional.ofNullable(this.userId);
    }

    public Optional<DiscordSnowflake> getGuildId() {
        return Optional.ofNullable(this.guildId);
    }

    public int getWeight() {
        return this.weight;
    }
}
