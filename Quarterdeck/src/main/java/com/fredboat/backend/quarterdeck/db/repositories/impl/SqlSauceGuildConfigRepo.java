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

import com.fredboat.backend.quarterdeck.db.entities.main.GuildConfig;
import com.fredboat.backend.quarterdeck.db.repositories.api.GuildConfigRepo;
import com.fredboat.backend.quarterdeck.parsing.LanguageParseException;
import com.fredboat.backend.quarterdeck.parsing.PatchParseUtil;
import fredboat.definitions.Language;
import org.springframework.stereotype.Component;
import space.npstr.sqlsauce.DatabaseWrapper;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by napster on 05.02.18.
 */
@Component
public class SqlSauceGuildConfigRepo extends SqlSauceRepo<String, GuildConfig> implements GuildConfigRepo {

    public SqlSauceGuildConfigRepo(DatabaseWrapper dbWrapper) {
        super(dbWrapper, GuildConfig.class);
    }

    @Override
    public GuildConfig patch(String id, Map<String, Object> partialUpdate) {

        Function<GuildConfig, GuildConfig> update = guildConfig -> guildConfig;
        //track announce
        if (partialUpdate.containsKey("trackAnnounce")) {
            boolean trackAnnounce = PatchParseUtil.parseBoolean("trackAnnounce", partialUpdate);
            update = update.andThen(guildConfig -> guildConfig.setTrackAnnounce(trackAnnounce));
        }

        //auto resume
        if (partialUpdate.containsKey("autoResume")) {
            boolean autoResume = PatchParseUtil.parseBoolean("autoResume", partialUpdate);
            update = update.andThen(guildConfig -> guildConfig.setAutoResume(autoResume));
        }

        //lang
        if (partialUpdate.containsKey("language")) {
            String langRaw = PatchParseUtil.cast("language", partialUpdate, String.class);
            Optional<Language> parse = Language.parse(langRaw);
            if (parse.isPresent()) {
                String language = parse.get().getCode();
                update = update.andThen(guildConfig -> guildConfig.setLang(language));
            } else {
                throw new LanguageParseException(langRaw);
            }
        }

        return this.dbWrapper.findApplyAndMerge(GuildConfig.key(id), update);
    }
}
