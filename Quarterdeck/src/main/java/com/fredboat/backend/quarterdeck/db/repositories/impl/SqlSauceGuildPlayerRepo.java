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

package com.fredboat.backend.quarterdeck.db.repositories.impl;

import com.fredboat.backend.quarterdeck.db.entities.main.GuildPlayer;
import com.fredboat.backend.quarterdeck.db.repositories.api.GuildPlayerRepo;
import com.fredboat.backend.quarterdeck.parsing.PatchParseUtil;
import com.fredboat.backend.quarterdeck.parsing.RepeatModeParseException;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.DiscordSnowflake;
import com.fredboat.backend.shared.RepeatMode;
import org.springframework.stereotype.Component;
import space.npstr.sqlsauce.DatabaseWrapper;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by napster on 28.03.18.
 */
@Component
public class SqlSauceGuildPlayerRepo extends SqlSauceRepo<Long, GuildPlayer> implements GuildPlayerRepo {

    public SqlSauceGuildPlayerRepo(DatabaseWrapper dbWrapper) {
        super(dbWrapper, GuildPlayer.class);
    }

    @Override
    public GuildPlayer patch(Long id, Map<String, Object> partialUpdate) {

        Function<GuildPlayer, GuildPlayer> update = guildPlayer -> guildPlayer;

        //voice channel id
        if (partialUpdate.containsKey("voiceChannelId")) {
            DiscordSnowflake voiceChannelId = PatchParseUtil.parseDiscordSnowflake("voiceChannelId", partialUpdate);
            update = update.andThen(guildPlayer -> guildPlayer.setVoiceChannelId(voiceChannelId.longValue()));
        }

        //active text channel id
        if (partialUpdate.containsKey("activeTextChannelId")) {
            DiscordSnowflake activeTextChannelId = PatchParseUtil.parseDiscordSnowflake("activeTextChannelId", partialUpdate);
            update = update.andThen(guildPlayer -> guildPlayer.setActiveTextChannelId(activeTextChannelId.longValue()));
        }

        //is paused
        if (partialUpdate.containsKey("paused")) {
            boolean isPaused = PatchParseUtil.parseBoolean("paused", partialUpdate);
            update = update.andThen(guildPlayer -> guildPlayer.setPaused(isPaused));
        }

        //volume
        if (partialUpdate.containsKey("volume")) {
            int volume = PatchParseUtil.parseInt("volume", partialUpdate);
            update = update.andThen(guildPlayer -> guildPlayer.setVolume(volume));
        }

        //repeat mode
        if (partialUpdate.containsKey("repeatMode")) {
            String repeatModeRaw = PatchParseUtil.cast("repeatMode", partialUpdate, String.class);
            Optional<RepeatMode> parse = RepeatMode.parse(repeatModeRaw);
            if (parse.isPresent()) {
                update = update.andThen(guildPlayer -> guildPlayer.setRepeatMode(parse.get()));
            } else {
                throw new RepeatModeParseException(repeatModeRaw);
            }
        }

        //is shuffled
        if (partialUpdate.containsKey("shuffled")) {
            boolean isShuffled = PatchParseUtil.parseBoolean("isShuffled", partialUpdate);
            update = update.andThen(guildPlayer -> guildPlayer.setShuffled(isShuffled));
        }

        return this.dbWrapper.findApplyAndMerge(GuildPlayer.key(id), update);
    }
}
