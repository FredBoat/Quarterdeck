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

package com.fredboat.backend.quarterdeck.db.entities.cache;

import fredboat.definitions.SearchProvider;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import space.npstr.sqlsauce.entities.SaucedEntity;
import space.npstr.sqlsauce.fp.types.EntityKey;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by napster on 27.08.17.
 * <p>
 * Caches a search result
 *
 * @deprecated Bug fixes only please. Move over to using {@link SearchResult} asap.
 */
@Entity
@Table(name = "track_search_results")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "track_search_results")
@Deprecated
public class TrackSearchResult extends SaucedEntity<SearchResultId, TrackSearchResult> {

    @SuppressWarnings("NullableProblems") //populated by the orm
    @EmbeddedId
    private SearchResultId searchResultId;

    @Column(name = "looked_up")
    private long lookedUp;

    @SuppressWarnings("NullableProblems") //populated by the orm
    @Column(name = "serialized_result")
    //serialization format: lavaplayer
    private byte[] serializedResult;

    //for jpa / db wrapper
    public TrackSearchResult() {
    }

    public static EntityKey<SearchResultId, TrackSearchResult> key(SearchResultId id) {
        return EntityKey.of(id, TrackSearchResult.class);
    }

    @Override
    public TrackSearchResult setId(SearchResultId id) {
        this.searchResultId = id;
        return this;
    }

    @Override
    public SearchResultId getId() {
        return this.searchResultId;
    }

    public SearchProvider getProvider() {
        return this.searchResultId.getProvider();
    }

    public String getSearchTerm() {
        return this.searchResultId.getSearchTerm();
    }

    public byte[] getSerializedResult() {
        return this.serializedResult;
    }

    public TrackSearchResult setSerializedResult(byte[] serializedResult) {
        this.serializedResult = serializedResult;
        return this;
    }

    public long getLookedUp() {
        return this.lookedUp;
    }

    public TrackSearchResult setLookedUp(long lookedUp) {
        this.lookedUp = lookedUp;
        return this;
    }

}
