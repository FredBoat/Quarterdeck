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

import com.fredboat.backend.quarterdeck.db.entities.main.GuildPlayer;

/**
 * Created by napster on 31.03.18.
 */
public class GuildPlayerTransfer {

    private final long guildId;
    private final long voiceChannelId;
    private final long activeTextChannelId;
    private final boolean isPaused;
    private final int volume;
    private final String repeatMode;
    private final boolean isShuffled;


    public static GuildPlayerTransfer of(GuildPlayer guildPlayer) {
        return new GuildPlayerTransfer(guildPlayer);
    }

    private GuildPlayerTransfer(GuildPlayer guildPlayer) {
        this.guildId = guildPlayer.getId();
        this.voiceChannelId = guildPlayer.getVoiceChannelId();
        this.activeTextChannelId = guildPlayer.getActiveTextChannelId();
        this.isPaused = guildPlayer.isPaused();
        this.volume = guildPlayer.getVolume();
        this.repeatMode = guildPlayer.getRepeatMode().name();
        this.isShuffled = guildPlayer.isShuffled();
    }
}
