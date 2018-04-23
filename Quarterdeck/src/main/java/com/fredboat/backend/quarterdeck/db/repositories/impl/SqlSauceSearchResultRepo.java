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
import com.fredboat.backend.quarterdeck.db.DatabaseManager;
import com.fredboat.backend.quarterdeck.db.entities.cache.TrackSearchResult;
import com.fredboat.backend.quarterdeck.db.repositories.api.SearchResultRepo;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by napster on 05.02.18.
 */
@Component
public class SqlSauceSearchResultRepo extends SqlSauceRepo<TrackSearchResult.SearchResultId, TrackSearchResult>
        implements SearchResultRepo {

    public SqlSauceSearchResultRepo(DatabaseConfiguration dbConfiguration, DatabaseManager databaseManager) {
        super(dbConfiguration.cacheDbWrapper(databaseManager), TrackSearchResult.class); //todo noop / reloading
    }

    @Nullable
    @Override
    public TrackSearchResult getMaxAged(TrackSearchResult.SearchResultId id, long maxAgeMillis) {
        //language=JPAQL
        String query = "SELECT sr FROM TrackSearchResult sr WHERE sr.searchResultId = :id AND sr.lookedUp > :oldest";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("oldest", maxAgeMillis < 0 ? 0 : System.currentTimeMillis() - maxAgeMillis);

        List<TrackSearchResult> queryResult = this.dbWrapper.selectJpqlQuery(query, params, TrackSearchResult.class, 1);

        if (queryResult.isEmpty()) {
            return null;
        } else {
            return queryResult.get(0);
        }
    }

    @Override
    public long getSize() {
        //language=JPAQL
        String query = "SELECT COUNT(sr) FROM TrackSearchResult sr";
        return this.dbWrapper.selectJpqlQuerySingleResult(query, null, Long.class);
    }
}
