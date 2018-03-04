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

package com.fredboat.quarterdeck.backend.config;

import fredboat.db.repositories.api.BlacklistRepo;
import fredboat.db.repositories.api.GuildConfigRepo;
import fredboat.db.repositories.api.GuildDataRepo;
import fredboat.db.repositories.api.GuildModulesRepo;
import fredboat.db.repositories.api.GuildPermsRepo;
import fredboat.db.repositories.api.PrefixRepo;
import fredboat.db.repositories.api.SearchResultRepo;
import fredboat.db.repositories.impl.SqlSauceBlacklistRepo;
import fredboat.db.repositories.impl.SqlSauceGuildConfigRepo;
import fredboat.db.repositories.impl.SqlSauceGuildDataRepo;
import fredboat.db.repositories.impl.SqlSauceGuildModulesRepo;
import fredboat.db.repositories.impl.SqlSauceGuildPermsRepo;
import fredboat.db.repositories.impl.SqlSaucePrefixRepo;
import fredboat.db.repositories.impl.SqlSauceSearchResultRepo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.npstr.sqlsauce.DatabaseWrapper;

/**
 * Created by napster on 04.03.18.
 */
@Configuration
public class RepoConfiguration {

    //main db repos
    @Bean
    public GuildConfigRepo guildConfigRepo(DatabaseWrapper wrapper) {
        return new SqlSauceGuildConfigRepo(wrapper);
    }

    @Bean
    public BlacklistRepo blacklistRepo(DatabaseWrapper wrapper) {
        return new SqlSauceBlacklistRepo(wrapper);
    }

    @Bean
    public GuildDataRepo guildDataRepo(DatabaseWrapper wrapper) {
        return new SqlSauceGuildDataRepo(wrapper);
    }

    @Bean
    public GuildModulesRepo guildModulesRepo(DatabaseWrapper wrapper) {
        return new SqlSauceGuildModulesRepo(wrapper);
    }

    @Bean
    public GuildPermsRepo guildPermsRepo(DatabaseWrapper wrapper) {
        return new SqlSauceGuildPermsRepo(wrapper);
    }

    @Bean
    public PrefixRepo prefixRepo(DatabaseWrapper wrapper) {
        return new SqlSaucePrefixRepo(wrapper);
    }


    //cache db repos
    @Bean
    public SearchResultRepo searchResultRepo(@Qualifier("cacheDbWrapper") DatabaseWrapper wrapper) {
        return new SqlSauceSearchResultRepo(wrapper);
    }
}
