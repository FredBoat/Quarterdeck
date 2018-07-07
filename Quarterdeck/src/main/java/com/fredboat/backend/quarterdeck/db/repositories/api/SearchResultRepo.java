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
import com.fredboat.backend.shared.SearchProvider;

import java.util.Optional;

/**
 * Created by napster on 05.02.18.
 */
public interface SearchResultRepo extends Repo<SearchResultId, SearchResult> {


    /**
     * @return size of the table containing the search results
     */
    long getSize();

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
