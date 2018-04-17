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

package com.fredboat.backend.quarterdeck.rest.v0.transfer;

import com.fredboat.backend.quarterdeck.db.entities.main.GuildBotId;
import com.fredboat.backend.quarterdeck.db.entities.main.Prefix;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

/**
 * @deprecated move to v1 asap pl0x
 */
@Deprecated
public class PrefixTransfer {

    private GuildBotId id;
    @Nullable
    private String prefix;

    PrefixTransfer(GuildBotId id, @Nullable String prefix) {
        this.id = id;
        this.prefix = prefix;
    }

    public static PrefixTransfer of(Prefix entity) {
        return new PrefixTransfer(entity.getId(), entity.getPrefix());
    }

    private PrefixTransfer() {
    }

    public void setId(GuildBotId id) {
        this.id = id;
    }

    public GuildBotId getId() {
        return this.id;
    }

    @Nullable
    public String getPrefix() {
        return this.prefix;
    }

    @CheckReturnValue
    public PrefixTransfer setPrefix(@Nullable String prefix) {
        this.prefix = prefix;
        return this;
    }
}
