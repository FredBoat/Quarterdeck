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

package com.fredboat.backend.quarterdeck.rest.v0;

import com.fredboat.backend.quarterdeck.db.entities.cache.TrackSearchResult;
import com.fredboat.backend.quarterdeck.db.repositories.api.SearchResultRepo;
import com.fredboat.backend.quarterdeck.rest.v0.transfer.SearchResultTransfer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;

/**
 * Created by napster on 17.02.18.
 *
 * @deprecated only bugfixes allowed. add new stuff to the v1 implementation.
 */
@Deprecated
@RestController
@RequestMapping("/" + EntityController.VERSION_PATH + "searchresult/")
public class V0SearchResultController {

    protected final SearchResultRepo repo;

    public V0SearchResultController(SearchResultRepo repo) {
        this.repo = repo;
    }

    @Nullable
    @PostMapping("/getmaxaged")
    public SearchResultTransfer getMaxAged(@RequestBody TrackSearchResult.SearchResultId id, @RequestParam("millis") long maxAgeMillis) {
        TrackSearchResult result = this.repo.getMaxAged(id, maxAgeMillis);
        return result == null ? null : SearchResultTransfer.of(result);
    }

    @PostMapping("/merge")
    public SearchResultTransfer merge(@RequestBody SearchResultTransfer transfer) {
        return SearchResultTransfer.of(this.repo.merge(transfer.toEntity()));
    }
}
