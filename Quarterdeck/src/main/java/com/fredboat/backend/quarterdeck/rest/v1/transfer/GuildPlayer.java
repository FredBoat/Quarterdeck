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

import fredboat.definitions.RepeatMode;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by napster on 31.03.18.
 *
 * @see com.fredboat.backend.quarterdeck.rest.v1.transfer (package-info.java)
 */
public class GuildPlayer {

    private final DiscordSnowflake guildId;
    private final DiscordSnowflake voiceChannelId;
    private final DiscordSnowflake activeTextChannelId;
    private final boolean isPaused;
    private final int volume;
    private final RepeatMode repeatMode;
    private final boolean isShuffled;


    public static GuildPlayer of(com.fredboat.backend.quarterdeck.db.entities.main.GuildPlayer guildPlayer) {
        return new GuildPlayer(guildPlayer);
    }

    private GuildPlayer(com.fredboat.backend.quarterdeck.db.entities.main.GuildPlayer guildPlayer) {
        this.guildId = new DiscordSnowflake(guildPlayer.getId());
        this.voiceChannelId = new DiscordSnowflake(guildPlayer.getVoiceChannelId());
        this.activeTextChannelId = new DiscordSnowflake(guildPlayer.getActiveTextChannelId());
        this.isPaused = guildPlayer.isPaused();
        this.volume = guildPlayer.getVolume();
        this.repeatMode = guildPlayer.getRepeatMode();
        this.isShuffled = guildPlayer.isShuffled();
    }


    // the getters are picked up by springfox for the documentation
    @ApiModelProperty(position = -1)
    public DiscordSnowflake getGuildId() {
        return this.guildId;
    }

    public DiscordSnowflake getVoiceChannelId() {
        return this.voiceChannelId;
    }

    public DiscordSnowflake getActiveTextChannelId() {
        return this.activeTextChannelId;
    }

    public boolean isPaused() {
        return this.isPaused;
    }

    @ApiModelProperty(allowableValues = "range[0, 150]", example = "72")
    public int getVolume() {
        return this.volume;
    }

    public RepeatMode getRepeatMode() {
        return this.repeatMode;
    }

    public boolean isShuffled() {
        return this.isShuffled;
    }
}
