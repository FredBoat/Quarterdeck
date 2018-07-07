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

package com.fredboat.backend.quarterdeck.rest.v1;

import com.fredboat.backend.quarterdeck.db.entities.cache.SearchResultId;
import com.fredboat.backend.quarterdeck.db.repositories.api.SearchResultRepo;
import com.fredboat.backend.quarterdeck.parsing.PatchParseUtil;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.SearchResult;
import com.fredboat.backend.shared.SearchProvider;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by napster on 18.05.18.
 */
@RestController
@RequestMapping("/" + EntityController.VERSION_PATH + "tracks/search/{provider}/{search_term}")
public class SearchResultController {


    protected final SearchResultRepo repo;

    public SearchResultController(SearchResultRepo repo) {
        this.repo = repo;
    }

    @GetMapping
    public ResponseEntity<SearchResult> getSearchResult(@PathVariable("provider") SearchProvider searchProvider,
                                                        @PathVariable("search_term") String searchTerm,
                                                        @RequestParam(value = "max_age", required = false) @Nullable Long maxAgeMillis) {
        long maxAge = maxAgeMillis != null ? maxAgeMillis : Long.MAX_VALUE;
        return this.repo.find(new SearchResultId(searchProvider, searchTerm), maxAge)
                .map(SearchResult::of)
                .map(sr -> new ResponseEntity<>(sr, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "searchResult", dataType = "SearchResult", required = true)
    })
    @PutMapping
    public SearchResult putSearchResult(@PathVariable("provider") SearchProvider provider,
                                        @PathVariable("search_term") String searchTerm,
                                        @RequestBody Map<String, Object> searchResult) {
        long lookedUp = PatchParseUtil.parseLong("lookedUp", searchResult);
        String track = PatchParseUtil.parseBase64String("track", searchResult);

        return SearchResult.of(this.repo.put(provider, searchTerm, lookedUp, track));
    }
}
