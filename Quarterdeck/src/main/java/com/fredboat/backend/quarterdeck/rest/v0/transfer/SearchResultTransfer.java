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

package com.fredboat.backend.quarterdeck.rest.v0.transfer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fredboat.backend.quarterdeck.db.entities.cache.SearchResultId;
import com.fredboat.backend.quarterdeck.db.entities.cache.TrackSearchResult;
import fredboat.definitions.SearchProvider;

import java.io.IOException;

/**
 * Created by napster on 05.04.18.
 *
 * @deprecated only bugfixes allowed. add new stuff to the v1 implementation.
 */
@Deprecated
public class SearchResultTransfer {

    private Id searchResultId;
    private long timestamp;
    private byte[] serializedSearchResult;

    public static SearchResultTransfer of(TrackSearchResult trackSearchResult) {
        return new SearchResultTransfer(trackSearchResult);
    }

    public TrackSearchResult toEntity() {
        return new TrackSearchResult()
                .setId(new SearchResultId(SearchProvider.valueOf(this.searchResultId.provider), this.searchResultId.searchTerm))
                .setLookedUp(this.timestamp)
                .setSerializedResult(this.serializedSearchResult);
    }

    private SearchResultTransfer(TrackSearchResult trackSearchResult) {
        this.searchResultId = new Id(trackSearchResult.getProvider().name(), trackSearchResult.getSearchTerm());
        this.timestamp = trackSearchResult.getLookedUp();
        this.serializedSearchResult = trackSearchResult.getSerializedResult();
    }

    private SearchResultTransfer() {
    }

    public Id getSearchResultId() {
        return this.searchResultId;
    }

    public void setSearchResultId(Id searchResultId) {
        this.searchResultId = searchResultId;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @JsonSerialize(using = ByteArraySerializer.class)
    public byte[] getSerializedSearchResult() {
        return this.serializedSearchResult;
    }

    public void setSerializedSearchResult(byte[] serializedSearchResult) {
        this.serializedSearchResult = serializedSearchResult;
    }

    private static class Id {
        private String provider;
        private String searchTerm;

        public Id(String provider, String searchTerm) {
            this.provider = provider;
            this.searchTerm = searchTerm;
        }

        private Id() {
        }

        public String getProvider() {
            return this.provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getSearchTerm() {
            return this.searchTerm;
        }

        public void setSearchTerm(String searchTerm) {
            this.searchTerm = searchTerm;
        }
    }

    private static class ByteArraySerializer extends JsonSerializer<byte[]> {

        @Override
        public void serialize(byte[] bytes, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartArray();
            for (byte b : bytes) {
                gen.writeNumber(b);
            }
            gen.writeEndArray();
        }
    }
}
