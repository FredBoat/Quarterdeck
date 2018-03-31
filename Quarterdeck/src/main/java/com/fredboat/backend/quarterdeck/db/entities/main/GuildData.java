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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ColumnDefault;
import space.npstr.sqlsauce.entities.SaucedEntity;
import space.npstr.sqlsauce.fp.types.EntityKey;

import javax.annotation.CheckReturnValue;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by napster on 23.01.18.
 * <p>
 * FredBoat internal info kept on guilds
 */
@Entity
@Table(name = "guild_data")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "guild_data")
public class GuildData extends SaucedEntity<Long, GuildData> {

    @Id
    @Column(name = "guild_id", nullable = false)
    private long guildId;

    @Column(name = "ts_hello_sent",
            nullable = false)
    @ColumnDefault(value = "0")
    private long timestampHelloSent;


    //for jpa / db wrapper
    GuildData() {
    }

    public static EntityKey<Long, GuildData> key(long guildId) {
        return EntityKey.of(guildId, GuildData.class);
    }

    @Override
    public GuildData setId(Long guildId) {
        this.guildId = guildId;
        return this;
    }

    @Override
    public Long getId() {
        return this.guildId;
    }

    public long getTimestampHelloSent() {
        return this.timestampHelloSent;
    }

    @CheckReturnValue
    public GuildData helloSent() {
        this.timestampHelloSent = System.currentTimeMillis();
        return this;
    }

    @CheckReturnValue
    public GuildData helloSent(long timestampHelloSent) {
        this.timestampHelloSent = timestampHelloSent;
        return this;
    }
}
