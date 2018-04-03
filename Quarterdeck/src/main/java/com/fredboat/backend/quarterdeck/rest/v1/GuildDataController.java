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

import com.fredboat.backend.quarterdeck.db.repositories.api.GuildDataRepo;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.DiscordSnowflake;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.GuildData;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by napster on 22.03.18.
 */
@RestController
@RequestMapping("/" + EntityController.VERSION_PATH + "guilds/{guild_id}/data")
public class GuildDataController {

    private final GuildDataRepo guildDataRepo;

    public GuildDataController(GuildDataRepo guildDataRepo) {
        this.guildDataRepo = guildDataRepo;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "guild_id", dataTypeClass = DiscordSnowflake.class, example = "174820236481134592",
                    required = true, paramType = "path", type = "string", format = "Discord snowflake",
                    value = "Discord snowflake")
    })
    @GetMapping
    public GuildData getGuildData(@PathVariable("guild_id") DiscordSnowflake guildId) {
        return GuildData.of(this.guildDataRepo.fetch(guildId.longValue()));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "guild_id", dataTypeClass = DiscordSnowflake.class, example = "174820236481134592",
                    required = true, paramType = "path", type = "string", format = "Discord snowflake",
                    value = "Discord snowflake"),
            @ApiImplicitParam(name = "partialGuildData", dataType = "GuildData", required = true)
    })
    @PatchMapping
    public GuildData patchGuildData(@PathVariable("guild_id") DiscordSnowflake guildId,
                                    @RequestBody Map<String, Object> partialGuildData) {
        return GuildData.of(this.guildDataRepo.patch(guildId.longValue(), partialGuildData));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "guild_id", dataTypeClass = DiscordSnowflake.class, example = "174820236481134592",
                    required = true, paramType = "path", type = "string", format = "Discord snowflake",
                    value = "Discord snowflake")
    })
    @DeleteMapping
    public void deleteGuildData(@PathVariable("guild_id") DiscordSnowflake guildId) {
        this.guildDataRepo.delete(guildId.longValue());
    }
}
