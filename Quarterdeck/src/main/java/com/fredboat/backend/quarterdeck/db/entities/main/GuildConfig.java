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
import space.npstr.sqlsauce.entities.SaucedEntity;
import space.npstr.sqlsauce.fp.types.EntityKey;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "guild_config")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "guild_config")
public class GuildConfig extends SaucedEntity<String, GuildConfig> {

    @Id
    @Column(name = "guildid", nullable = false)
    private String guildId;

    @Column(name = "track_announce", nullable = false)
    private boolean trackAnnounce = false;

    @Column(name = "auto_resume", nullable = false)
    private boolean autoResume = false;

    @Column(name = "lang", nullable = false)
    private String lang = "en_US";

    //for jpa / db wrapper
    GuildConfig() {
    }

    public static EntityKey<String, GuildConfig> key(String guildId) {
        return EntityKey.of(guildId, GuildConfig.class);
    }

    public static EntityKey<String, GuildConfig> key(long guildId) {
        return key(Long.toString(guildId));
    }

    @Override
    public GuildConfig setId(String id) {
        this.guildId = id;
        return this;
    }

    @Override
    public String getId() {
        return this.guildId;
    }

    public boolean isTrackAnnounce() {
        return this.trackAnnounce;
    }

    public GuildConfig setTrackAnnounce(boolean trackAnnounce) {
        this.trackAnnounce = trackAnnounce;
        return this;
    }

    public boolean isAutoResume() {
        return this.autoResume;
    }

    public GuildConfig setAutoResume(boolean autoplay) {
        this.autoResume = autoplay;
        return this;
    }

    public String getLang() {
        return this.lang;
    }

    public GuildConfig setLang(String lang) {
        this.lang = lang;
        return this;
    }

}
