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

package com.fredboat.backend.quarterdeck.db.repositories.api;

import com.fredboat.backend.quarterdeck.db.entities.cache.SearchResult;
import com.fredboat.backend.quarterdeck.db.entities.cache.SearchResultId;
import com.fredboat.backend.quarterdeck.db.entities.cache.TrackSearchResult;
import com.fredboat.backend.shared.SearchProvider;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Created by napster on 05.02.18.
 */
public interface SearchResultRepo extends Repo<SearchResultId, SearchResult> {

    /**
     * @param id
     *         id of the search result to be returned
     * @param maxAgeMillis
     *         do not return search results which are older than the max age
     *
     * @return the search result of the provided id  younge than the requested maximum age, null if there is either no
     * search result for the provided is, or only an older than the requested maximum age one.
     *
     * @deprecated bugfixes only. New features should be added to the modern table, which saves the tracks as base64
     * Strings instead of a single serialized byte array
     */
    @Nullable
    @Deprecated
    TrackSearchResult getMaxAged(SearchResultId id, long maxAgeMillis);

    /**
     * @deprecated remove as soon as the the legacy table is phased out for good (when the v1 quarterdeck api is adopted
     * and no rollback is bound to go back to v0).
     */
    @Deprecated
    TrackSearchResult mergeLegacy(TrackSearchResult entity);

    /**
     * @param id
     *         id of the search result to be returned
     * @param maxAgeMillis
     *         do not return search results which are older than the max age
     *
     * @return the search result of the provided id, younger than the requested maximum age, null if there is either no
     * search result for the provided is, or only an older than the requested maximum age one.
     */
    Optional<SearchResult> find(SearchResultId id, long maxAgeMillis);

    /**
     * Save a search result as described by the provided parameters.
     *
     * @return the merged entity.
     */
    SearchResult put(SearchProvider provider, String searchTerm, long lookedUp, String track);
}
