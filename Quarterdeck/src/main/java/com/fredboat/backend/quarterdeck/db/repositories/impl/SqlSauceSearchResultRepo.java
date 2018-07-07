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

import com.fredboat.backend.quarterdeck.config.DatabaseConfiguration;
import com.fredboat.backend.quarterdeck.db.entities.cache.SearchResult;
import com.fredboat.backend.quarterdeck.db.entities.cache.SearchResultId;
import com.fredboat.backend.quarterdeck.db.repositories.api.SearchResultRepo;
import com.fredboat.backend.shared.SearchProvider;
import org.springframework.stereotype.Component;
import space.npstr.sqlsauce.DbUtils;

import java.util.Optional;

/**
 * Created by napster on 05.02.18.
 */
@Component
public class SqlSauceSearchResultRepo extends SqlSauceRepo<SearchResultId, SearchResult>
        implements SearchResultRepo {

    public SqlSauceSearchResultRepo(DatabaseConfiguration dbConfiguration) {
        super(dbConfiguration.getCacheDbWrapper(), SearchResult.class); //todo noop / reloading
    }

    @Override
    public long getSize() {
        //language=JPAQL
        String legacyTableQuery = "SELECT COUNT(tsr) FROM TrackSearchResult tsr";
        //language=JPAQL
        String modernTableQuery = "SELECT COUNT(sr) FROM SearchResult sr";
        return this.dbWrapper.selectJpqlQuerySingleResult(legacyTableQuery, null, Long.class)
                + this.dbWrapper.selectJpqlQuerySingleResult(modernTableQuery, null, Long.class);
    }

    @Override
    public Optional<SearchResult> find(SearchResultId id, long maxAgeMillis) {
        //language=JPAQL
        String query = "SELECT sr FROM SearchResult sr WHERE sr.id = :id AND sr.lookedUp > :oldest";
        var params = DbUtils.paramsOf(
                "id", id,
                "oldest", maxAgeMillis < 0 ? 0 : System.currentTimeMillis() - maxAgeMillis
        );

        return this.dbWrapper
                .selectJpqlQuery(query, params, SearchResult.class, 1)
                .stream()
                .findAny();
    }

    @Override
    public SearchResult put(SearchProvider provider, String searchTerm, long lookedUp, String track) {
        return this.dbWrapper.merge(new SearchResult(provider, searchTerm, lookedUp, track));
    }
}
