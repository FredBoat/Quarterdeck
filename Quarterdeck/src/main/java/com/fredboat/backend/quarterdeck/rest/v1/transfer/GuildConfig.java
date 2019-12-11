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

import com.fredboat.backend.shared.Language;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by napster on 31.03.18.
 *
 * @see com.fredboat.backend.quarterdeck.rest.v1.transfer (package-info.java)
 */
public class GuildConfig {

    private final DiscordSnowflake guildId;
    private final boolean trackAnnounce;
    private final boolean autoResume;
    private final boolean clearOnEmpty;
    private final Language language;
    private final boolean enableUnknownCommand;

    public static GuildConfig of(com.fredboat.backend.quarterdeck.db.entities.main.GuildConfig guildConfig) {
        return new GuildConfig(guildConfig);
    }

    private GuildConfig(com.fredboat.backend.quarterdeck.db.entities.main.GuildConfig guildConfig) {
        this.guildId = new DiscordSnowflake(guildConfig.getId());
        this.trackAnnounce = guildConfig.isTrackAnnounce();
        this.autoResume = guildConfig.isAutoResume();
        this.language = guildConfig.getLanguage();
        this.clearOnEmpty = guildConfig.isClearOnEmpty();
        this.enableUnknownCommand = guildConfig.isEnableUnknownCommand();
    }


    // the getters are picked up by springfox for the documentation
    @ApiModelProperty(position = -1)
    public DiscordSnowflake getGuildId() {
        return this.guildId;
    }

    public boolean isTrackAnnounce() {
        return this.trackAnnounce;
    }

    public boolean isAutoResume() {
        return this.autoResume;
    }

    public boolean isClearOnEmpty() {
        return this.clearOnEmpty;
    }

    public Language getLanguage() {
        return this.language;
    }

    public boolean isEnableUnknownCommand() { return this.enableUnknownCommand; }

}
