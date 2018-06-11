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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import fredboat.definitions.SearchProvider;

/**
 * Created by napster on 20.05.18.
 *
 * @see com.fredboat.backend.quarterdeck.rest.v1.transfer (package-info.java)
 */
public class SearchResult {

    @SuppressWarnings("NullableProblems") //populated by constructor / jackson
    private SearchProvider provider;
    private String searchTerm = "";
    private long lookedUp;
    private String track = "";

    public static SearchResult of(com.fredboat.backend.quarterdeck.db.entities.cache.SearchResult entity) {
        return new SearchResult(entity.getId().getProvider(),
                entity.getId().getSearchTerm(),
                entity.getLookedUp(),
                entity.getTrack());
    }

    public SearchResult(SearchProvider provider, String searchTerm, long lookedUp, String track) {
        this.provider = provider;
        this.searchTerm = searchTerm;
        this.lookedUp = lookedUp;
        this.track = track;
    }

    private SearchResult() {
    }

    public SearchProvider getProvider() {
        return this.provider;
    }

    public void setProvider(SearchProvider provider) {
        this.provider = provider;
    }

    public String getSearchTerm() {
        return this.searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    @JsonSerialize(using = ToStringSerializer.class)
    public long getLookedUp() {
        return this.lookedUp;
    }

    public void setLookedUp(long lookedUp) {
        this.lookedUp = lookedUp;
    }

    public String getTrack() {
        return this.track;
    }

    public void setTrack(String track) {
        this.track = track;
    }
}
