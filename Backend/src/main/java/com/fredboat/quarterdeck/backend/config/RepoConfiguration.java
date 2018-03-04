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

import fredboat.db.DatabaseManager;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by napster on 04.03.18.
 */
@Configuration
public class RepoConfiguration {

    //main db repos
    @Bean
    public GuildConfigRepo guildConfigRepo(DatabaseManager databaseManager) {
        return new SqlSauceGuildConfigRepo(databaseManager.getMainDbWrapper());
    }

    @Bean
    public BlacklistRepo blacklistRepo(DatabaseManager databaseManager) {
        return new SqlSauceBlacklistRepo(databaseManager.getMainDbWrapper());
    }

    @Bean
    public GuildDataRepo guildDataRepo(DatabaseManager databaseManager) {
        return new SqlSauceGuildDataRepo(databaseManager.getMainDbWrapper());
    }

    @Bean
    public GuildModulesRepo guildModulesRepo(DatabaseManager databaseManager) {
        return new SqlSauceGuildModulesRepo(databaseManager.getMainDbWrapper());
    }

    @Bean
    public GuildPermsRepo guildPermsRepo(DatabaseManager databaseManager) {
        return new SqlSauceGuildPermsRepo(databaseManager.getMainDbWrapper());
    }

    @Bean
    public PrefixRepo prefixRepo(DatabaseManager databaseManager) {
        return new SqlSaucePrefixRepo(databaseManager.getMainDbWrapper());
    }


    //cache db repos
    @Bean
    public SearchResultRepo searchResultRepo(DatabaseManager databaseManager) {
        return new SqlSauceSearchResultRepo(databaseManager.getCacheDbWrapper()); //todo noop / reloading
    }
}
