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

import com.fredboat.backend.quarterdeck.rest.v0.transfer.PrefixTransfer;
import org.hibernate.annotations.Type;
import space.npstr.sqlsauce.entities.SaucedEntity;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by napster on 22.12.17.
 * <p>
 * The caching of this entity is not managed by ehcache, instead a guava cache is used on the client side.
 */
@Entity
@Table(name = "prefixes")
public class Prefix extends SaucedEntity<Prefix.GuildBotId, Prefix> {

    @SuppressWarnings("NullableProblems")
    @EmbeddedId
    private GuildBotId id;


    @Type(type = "hash-set-string")
    @Column(name = "pvalues") //values is a semi-reserved keyword
    private HashSet<String> values = new HashSet<>(Set.of(";;", "!"));

    //for jpa & the database wrapper
    Prefix() {
    }

    @Override
    public Prefix setId(GuildBotId id) {
        this.id = id;
        return this;
    }

    @Override
    public GuildBotId getId() {
        return this.id;
    }

    @Override
    public Class<Prefix> getClazz() {
        return Prefix.class;
    }

    /**
     * @deprecated switch to v1 asap pl0x, this method
     */
    @Deprecated
    @Nullable
    public String getPrefix() {
        if (this.values.isEmpty()
                || (this.values.size() == 2 && this.values.contains(";;") && this.values.contains("!"))) {
            return null; //the client is expected to pick their default one
        } else {
            //after the migration, if there was a custom prefix in the old format, it will be the only one in the set
            return this.values.iterator().next();
        }
    }

    @Deprecated
    public static Prefix fromTransfer(PrefixTransfer transfer) {
        Prefix result = new Prefix().setId(transfer.getId());
        result.values = new HashSet<>();
        String newPrefix = transfer.getPrefix();
        if (newPrefix != null) {
            result.values.add(newPrefix);
        }

        return result;
    }

    @CheckReturnValue
    public Prefix addPrefix(@Nullable String prefix) {
        this.values.add(prefix);
        return this;
    }

    @CheckReturnValue
    public Prefix removePrefix(@Nullable String prefix) {
        this.values.remove(prefix);
        return this;
    }

    public static class GuildBotId implements Serializable {
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

}
