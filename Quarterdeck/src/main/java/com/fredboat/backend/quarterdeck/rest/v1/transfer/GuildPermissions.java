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

import io.swagger.annotations.ApiModelProperty;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class GuildPermissions {

    private final DiscordSnowflake guildId;
    private final List<DiscordSnowflake> adminIds;
    private final List<DiscordSnowflake> djIds;
    private final List<DiscordSnowflake> userIds;

    public static GuildPermissions of(com.fredboat.backend.quarterdeck.db.entities.main.GuildPermissions guildPermissions) {
        return new GuildPermissions(guildPermissions);
    }

    private GuildPermissions(com.fredboat.backend.quarterdeck.db.entities.main.GuildPermissions guildPermissions) {
        this.guildId = new DiscordSnowflake(guildPermissions.getId());
        this.adminIds = this.parseIds(guildPermissions.splitAdminList());
        this.djIds = this.parseIds(guildPermissions.splitDjList());
        this.userIds = this.parseIds(guildPermissions.splitDjList());
    }

    // the getters are picked up by springfox for the documentation
    @ApiModelProperty(position = -1)
    public DiscordSnowflake getGuildId() {
        return guildId;
    }

    public List<DiscordSnowflake> getAdminIds() {
        return this.adminIds;
    }

    public List<DiscordSnowflake> getDjIds() {
        return this.djIds;
    }

    public List<DiscordSnowflake> getUserIds() {
        return this.userIds;
    }

    /**
     * Parse list of string into discord snowflake list.
     *
     * @param ids List of ids to be parsed.
     * @return List of discord snowflake.
     */
    private List<DiscordSnowflake> parseIds(List<String> ids) {
        List<DiscordSnowflake> idList = new LinkedList<>();
        Stream<String> stream = ids.stream();
        stream.forEach(id -> {
            if (id == null || id.length() == 0) {
                return;
            }

            try {
                DiscordSnowflake discordSnowflake = new DiscordSnowflake(id);
                idList.add(discordSnowflake);
            } catch (NumberFormatException e) {
                // ignored
            }
        });

        return idList;
    }
}
