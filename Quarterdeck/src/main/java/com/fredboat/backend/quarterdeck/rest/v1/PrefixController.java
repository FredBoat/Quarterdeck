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

import com.fredboat.backend.quarterdeck.db.entities.main.GuildBotId;
import com.fredboat.backend.quarterdeck.db.repositories.api.PrefixRepo;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.DiscordSnowflake;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.Prefix;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Created by napster on 03.04.18.
 */
@RestController
@RequestMapping("/" + EntityController.VERSION_PATH + "guilds/{guild_id}/prefix")
public class PrefixController {

    private final PrefixRepo prefixRepo;

    public PrefixController(PrefixRepo prefixRepo) {
        this.prefixRepo = prefixRepo;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "guild_id", dataTypeClass = DiscordSnowflake.class, example = "174820236481134592",
                    required = true, paramType = "path", type = "string", format = "Discord snowflake",
                    value = "Discord snowflake"),
            @ApiImplicitParam(name = "bot_id", dataTypeClass = DiscordSnowflake.class, example = "223809720098488320",
                    required = true, paramType = "query", type = "string", format = "Discord snowflake",
                    value = "Discord snowflake"),
    })
    @GetMapping
    public Prefix getPrefixes(@PathVariable("guild_id") DiscordSnowflake guildId,
                              @RequestParam("bot_id") DiscordSnowflake botId) {
        return Prefix.of(this.prefixRepo.fetch(new GuildBotId(guildId.longValue(), botId.longValue())));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "guild_id", dataTypeClass = DiscordSnowflake.class, example = "174820236481134592",
                    required = true, paramType = "path", type = "string", format = "Discord snowflake",
                    value = "Discord snowflake"),
            @ApiImplicitParam(name = "bot_id", dataTypeClass = DiscordSnowflake.class, example = "223809720098488320",
                    required = true, paramType = "query", type = "string", format = "Discord snowflake",
                    value = "Discord snowflake"),
            @ApiImplicitParam(name = "prefixes", example = "[\";;\", \"!\"]",
                    required = true, paramType = "body", type = "string", format = "string",
                    allowMultiple = true,
                    value = "Array of prefixes. A prefix can be any string."),
    })
    @PostMapping
    public Prefix addPrefixes(@PathVariable("guild_id") DiscordSnowflake guildId,
                              @RequestParam("bot_id") DiscordSnowflake botId,
                              @RequestBody String[] prefixes) {
        GuildBotId id = new GuildBotId(guildId.longValue(), botId.longValue());
        return Prefix.of(this.prefixRepo.addPrefixes(id, Arrays.asList(prefixes)));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "guild_id", dataTypeClass = DiscordSnowflake.class, example = "174820236481134592",
                    required = true, paramType = "path", type = "string", format = "Discord snowflake",
                    value = "Discord snowflake"),
            @ApiImplicitParam(name = "bot_id", dataTypeClass = DiscordSnowflake.class, example = "223809720098488320",
                    required = true, paramType = "query", type = "string", format = "Discord snowflake",
                    value = "Discord snowflake"),
            @ApiImplicitParam(name = "prefixes", example = "[\";;\", \"!\"]",
                    required = true, paramType = "body", type = "string", format = "string",
                    allowMultiple = true, allowEmptyValue = true,
                    value = "Array of (existing) prefixes. None or empty array to reset the whole entity"),
    })
    @DeleteMapping
    public Prefix removeOrResetPrefix(@PathVariable("guild_id") DiscordSnowflake guildId,
                                      @RequestParam("bot_id") DiscordSnowflake botId,
                                      @RequestBody(required = false) @Nullable String[] prefixes) { //if no specific prefix or an empty array is sent, reset the whole entity
        GuildBotId id = new GuildBotId(guildId.longValue(), botId.longValue());

        if (prefixes == null || prefixes.length == 0) {
            this.prefixRepo.delete(id);
            return Prefix.of(this.prefixRepo.fetch(id));
        } else {
            return Prefix.of(this.prefixRepo.removePrefixes(id, Arrays.asList(prefixes)));
        }
    }
}
