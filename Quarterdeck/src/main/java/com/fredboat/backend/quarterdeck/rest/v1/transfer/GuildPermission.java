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

public class GuildPermission {

    private final DiscordSnowflake guildId;
    private final String adminIds;
    private final String djIds;
    private final String userIds;

    public static GuildPermission of(com.fredboat.backend.quarterdeck.db.entities.main.GuildPermissions guildPermissions) {
        return new GuildPermission(guildPermissions);
    }

    private GuildPermission(com.fredboat.backend.quarterdeck.db.entities.main.GuildPermissions guildPermissions) {
        this.guildId = new DiscordSnowflake(guildPermissions.getId());
        this.adminIds = guildPermissions.getAdminList();
        this.djIds = guildPermissions.getDjList();
        this.userIds = guildPermissions.getUserList();
    }

    // the getters are picked up by springfox for the documentation
    @ApiModelProperty(position = -1)
    public DiscordSnowflake getGuildId() {
        return guildId;
    }

    public String getAdminIds() {
        return this.adminIds;
    }

    public String getDjIds() {
        return this.djIds;
    }

    public String getUserIds() {
        return this.userIds;
    }
}
