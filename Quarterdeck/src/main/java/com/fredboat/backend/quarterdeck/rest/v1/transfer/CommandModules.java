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

import com.fredboat.backend.quarterdeck.db.entities.main.GuildModules;
import com.fredboat.backend.shared.Module;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by napster on 04.04.18.
 *
 * @see com.fredboat.backend.quarterdeck.rest.v1.transfer (package-info.java)
 */
public class CommandModules {

    private DiscordSnowflake guildId;
    private List<CommandModule> modules;


    public static CommandModules of(GuildModules guildModules) {
        return new CommandModules(guildModules);
    }

    private CommandModules(GuildModules guildModules) {
        this.guildId = new DiscordSnowflake(guildModules.getId());
        this.modules = List.of(
                CommandModule.of(guildModules, Module.ADMIN),
                CommandModule.of(guildModules, Module.INFO),
                CommandModule.of(guildModules, Module.CONFIG),
                CommandModule.of(guildModules, Module.MUSIC),
                CommandModule.of(guildModules, Module.MOD),
                CommandModule.of(guildModules, Module.UTIL),
                CommandModule.of(guildModules, Module.FUN)
        );
    }


    // the getters are picked up by springfox for the documentation
    @ApiModelProperty(position = -1)
    public DiscordSnowflake getGuildId() {
        return this.guildId;
    }

    public List<CommandModule> getModules() {
        return this.modules;
    }
}
