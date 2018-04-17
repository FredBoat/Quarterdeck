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
import org.hibernate.annotations.Type;
import space.npstr.sqlsauce.entities.SaucedEntity;
import space.npstr.sqlsauce.fp.types.EntityKey;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by napster on 27.08.17.
 * <p>
 * Caches a search result
 */
@Entity
@Table(name = "search_results")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "track_search_results")
public class TrackSearchResult extends SaucedEntity<TrackSearchResult.SearchResultId, TrackSearchResult> {

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

    public TrackSearchResult setProvider(SearchProvider provider) {
        this.searchResultId.provider = provider;
        return this;
    }

    public String getSearchTerm() {
        return this.searchResultId.searchTerm;
    }

    public TrackSearchResult setSearchTerm(String searchTerm) {
        this.searchResultId.searchTerm = searchTerm;
        return this;
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

    /**
     * Composite primary key for SearchResults
     */
    @Embeddable
    public static class SearchResultId implements Serializable {

        private static final long serialVersionUID = 8969973651938173208L;

        @SuppressWarnings("NullableProblems") //populated by the orm
        @Enumerated(EnumType.STRING)
        @Type(type = "pgsql_enum")
        @Column(name = "provider", nullable = false)
        private SearchProvider provider;

        @SuppressWarnings("NullableProblems") //populated by the orm
        @Column(name = "search_term", nullable = false, columnDefinition = "text")
        private String searchTerm;

        //for jpa / db wrapper
        SearchResultId() {
        }

        public SearchResultId(SearchProvider provider, String searchTerm) {
            this.provider = provider;
            this.searchTerm = searchTerm;
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

        @Override
        public int hashCode() {
            return Objects.hash(this.provider, this.searchTerm);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SearchResultId)) return false;
            SearchResultId other = (SearchResultId) o;
            return this.provider.equals(other.provider) && this.searchTerm.equals(other.searchTerm);
        }

        @Override
        public String toString() {
            return "Search: Provider " + this.provider + " Search term " + this.searchTerm;
        }
    }
}
