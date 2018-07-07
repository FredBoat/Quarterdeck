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

import com.fredboat.backend.shared.RepeatMode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import space.npstr.sqlsauce.entities.SaucedEntity;
import space.npstr.sqlsauce.fp.types.EntityKey;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by napster on 28.03.18.
 */
@Entity
@Table(name = "guild_players")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "guild_player")
@SuppressWarnings("unused")
public class GuildPlayer extends SaucedEntity<Long, GuildPlayer> {

    public static final int DEFAULT_VOLUME = 100;

    @Id
    @NaturalId
    @Column(name = "guild_id", nullable = false)
    private long guildId;

    @Column(name = "voice_channel_id", nullable = false)
    private long voiceChannelId;

    @Column(name = "active_text_channel_id", nullable = false)
    private long activeTextChannelId;

    @Column(name = "is_paused", nullable = false)
    private boolean isPaused = true;

    //constraints: 0 - 150
    @Column(name = "volume", nullable = false)
    private int volume = DEFAULT_VOLUME;

    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    @Column(name = "repeat_mode", nullable = false)
    private RepeatMode repeatMode = RepeatMode.OFF;

    @Column(name = "is_shuffled", nullable = false)
    private boolean isShuffled = false;


    //for jpa / db wrapper
    GuildPlayer() {
    }

    public static EntityKey<Long, GuildPlayer> key(long guildId) {
        return EntityKey.of(guildId, GuildPlayer.class);
    }

    @Override
    public GuildPlayer setId(Long guildId) {
        this.guildId = guildId;
        return this;
    }

    @Override
    public Long getId() {
        return this.guildId;
    }

    public long getVoiceChannelId() {
        return this.voiceChannelId;
    }

    public GuildPlayer setVoiceChannelId(long voiceChannelId) {
        this.voiceChannelId = voiceChannelId;
        return this;
    }

    public long getActiveTextChannelId() {
        return this.activeTextChannelId;
    }

    public GuildPlayer setActiveTextChannelId(long activeTextChannelId) {
        this.activeTextChannelId = activeTextChannelId;
        return this;
    }

    public boolean isPaused() {
        return this.isPaused;
    }

    public GuildPlayer setPaused(boolean paused) {
        this.isPaused = paused;
        return this;
    }

    public int getVolume() {
        return this.volume;
    }

    public GuildPlayer setVolume(int volume) {
        this.volume = volume;
        return this;
    }

    public RepeatMode getRepeatMode() {
        return this.repeatMode;
    }

    public GuildPlayer setRepeatMode(RepeatMode repeatMode) {
        this.repeatMode = repeatMode;
        return this;
    }

    public boolean isShuffled() {
        return this.isShuffled;
    }

    public GuildPlayer setShuffled(boolean shuffled) {
        this.isShuffled = shuffled;
        return this;
    }
}
