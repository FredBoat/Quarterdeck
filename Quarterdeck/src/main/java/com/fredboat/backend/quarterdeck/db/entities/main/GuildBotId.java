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

package com.fredboat.backend.quarterdeck.db.entities.main;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by napster on 03.04.18.
 * <p>
 * Used as a compound id by some of our entities to save bot specific (think public and patron) guild information.
 */
@Embeddable
public class GuildBotId implements Serializable {
    private static final long serialVersionUID = 2057084374531313455L;

    @Column(name = "guild_id", nullable = false)
    private long guildId;

    @Column(name = "bot_id", nullable = false)
    private long botId;

    //for jpa & the database wrapper
    GuildBotId() {
    }

    public GuildBotId(long guildId, long botId) {
        this.guildId = guildId;
        this.botId = botId;
    }

    public long getGuildId() {
        return this.guildId;
    }

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    public long getBotId() {
        return this.botId;
    }

    public void setBotId(long botId) {
        this.botId = botId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.guildId, this.botId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GuildBotId)) return false;
        GuildBotId other = (GuildBotId) o;
        return this.guildId == other.guildId && this.botId == other.botId;
    }


    @Override
    public String toString() {
        return GuildBotId.class.getSimpleName() + String.format("(G %s, B %s)", this.guildId, this.botId);
    }
}
