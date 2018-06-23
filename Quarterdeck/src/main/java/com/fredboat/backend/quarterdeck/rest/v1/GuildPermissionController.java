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

package com.fredboat.backend.quarterdeck.rest.v1;

import com.fredboat.backend.quarterdeck.db.repositories.api.GuildPermsRepo;
import com.fredboat.backend.quarterdeck.parsing.PatchParseUtil;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/" + EntityController.VERSION_PATH + "guilds/{guild_id}/permissions")
public class GuildPermissionController {

    private final GuildPermsRepo guildPermsRepo;

    public GuildPermissionController(GuildPermsRepo guildPermsRepo) {
        this.guildPermsRepo = guildPermsRepo;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "guild_id", dataTypeClass = DiscordSnowflake.class, example = "174820236481134592",
                    required = true, paramType = "path", type = "string", format = "Discord snowflake",
                    value = "Discord snowflake")
    })
    @GetMapping
    public GuildPermissions getGuildPermissions(@PathVariable("guild_id") DiscordSnowflake guildId) {

        var guildPermissions = this.guildPermsRepo.get(guildId.getSnowflakeId());
        return GuildPermissions.of(guildPermissions);
    }

    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "guild_id", dataTypeClass = DiscordSnowflake.class, example = "174820236481134592",
                    required = true, paramType = "path", type = "string", format = "Discord snowflake",
                    value = "Discord snowflake"),
            @ApiImplicitParam(name = "permission_level", dataTypeClass = GuildPermissionLevel.class, example = "dj",
                    required = true, paramType = "path", type = "string", format = "Discord snowflake"),
            @ApiImplicitParam(name = "id", dataTypeClass = DiscordSnowflake.class, example = "174820236481134599",
                    required = true, paramType = "path", type = "string", format = "Discord snowflake",
                    value = "Discord snowflake"),
    })
    @PutMapping("{permission_level}/{id}")
    public GuildPermissions putIdIntoGuildPermission(@PathVariable("guild_id") DiscordSnowflake guildId,
                                                     @PathVariable("permission_level") String guildPermissionLevel,
                                                     @PathVariable("id") DiscordSnowflake id) {
        GuildPermissionLevel permissionLevel = PatchParseUtil.parsePermissionLevel(guildPermissionLevel);
        return GuildPermissions.of(this.guildPermsRepo.put(guildId.getSnowflakeId(), permissionLevel,
                id.getSnowflakeId()));

    }

    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "guild_id", dataTypeClass = DiscordSnowflake.class, example = "174820236481134592",
                    required = true, paramType = "path", type = "string", format = "Discord snowflake",
                    value = "Discord snowflake"),
            @ApiImplicitParam(name = "permission_level", dataTypeClass = GuildPermissionLevel.class, example = "dj",
                    required = true, paramType = "path", type = "string", format = "Discord snowflake"),
            @ApiImplicitParam(name = "id", dataTypeClass = DiscordSnowflake.class, example = "174820236481134599",
                    required = true, paramType = "path", type = "string", format = "Discord snowflake")
    })
    @DeleteMapping("{permission_level}/{id}")
    public GuildPermissions deleteIdFromGuildPermission(@PathVariable("guild_id") DiscordSnowflake guildId,
                                                        @PathVariable("permission_level") String guildPermissionLevel,
                                                        @PathVariable("id") DiscordSnowflake id) {

        GuildPermissionLevel permissionLevel = PatchParseUtil.parsePermissionLevel(guildPermissionLevel);
        return GuildPermissions.of(this.guildPermsRepo.delete(guildId.getSnowflakeId(), permissionLevel,
                id.getSnowflakeId()));
    }
}
