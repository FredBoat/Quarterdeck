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

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by napster on 18.05.18.
 * <p>
 * This is the class corresponding to the V1 Quarterdeck API, and going forward.
 * <p>
 * Compared to the old style, it does rely on Javas serialization, but instead allows the client to store a track
 * individually as a string/bytea, which comes as close as possible to the output of lavaplayer's own serialization.
 * This solves the issue of Java encoding class information into the serialization output, effectively locking the class
 * name / package in, therefore impacting interoperability as well generally being very error prone due to no compile
 * time checks.
 * <p>
 * The decision was made to only allow saving a single track, because going forward FredBoat will be switching to
 * auto-selecting the top-most search result.
 */

@Entity
@Table(name = "search_results")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "search_results")
public class SearchResult extends SaucedEntity<SearchResultId, SearchResult> {

    @SuppressWarnings("NullableProblems") // populated by the orm
    @EmbeddedId
    private SearchResultId id;

    @Column(name = "looked_up")
    private long lookedUp;

    //the expected format is base64 (as provided by lavaplayer).
    //this is important, because postgres' text type cannot safe all possible byte values (0x00 will error out)
    @Column(name = "track")
    private String track = "";

    //for jpa / database wrapper
    SearchResult() {}

    public SearchResult(SearchProvider provider, String searchTerm, long lookedUp, String track) {
        this.id = new SearchResultId(provider, searchTerm);
        this.lookedUp = lookedUp;
        this.track = track;
    }

    @Override
    public SearchResult setId(SearchResultId id) {
        this.id = id;
        return this;
    }

    @Override
    public SearchResultId getId() {
        return this.id;
    }

    public long getLookedUp() {
        return this.lookedUp;
    }

    public SearchResult setLookedUp(long lookedUp) {
        this.lookedUp = lookedUp;
        return this;
    }

    public String getTrack() {
        return this.track;
    }

    public SearchResult setTrack(String track) {
        this.track = track;
        return this;
    }
}
