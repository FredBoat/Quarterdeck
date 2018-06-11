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

import com.fredboat.backend.quarterdeck.BaseTest;
import fredboat.definitions.SearchProvider;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Base64;
import java.util.HashMap;

import static org.hamcrest.Matchers.both;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by napster on 22.05.18.
 */
public class SearchResultControllerTest extends BaseTest {

    private static final String baseTemplate = "/v1/tracks/search/{provider}/{search_term}";
    private static final String maxAgeTemplate = baseTemplate + "?max_age={max_age}";


    @Test
    public void getNoResult() throws Exception {
        SearchProvider provider = SearchProvider.SOUNDCLOUD;
        String searchTerm = "such search much term";

        this.mockMvc.perform(get(baseTemplate, provider, searchTerm))
                .andExpect(status().isNotFound());
    }

    @Test
    public void putResult() throws Exception {
        SearchProvider provider = SearchProvider.YOUTUBE;
        String searchTerm = "whatever";
        long lookedUp = System.currentTimeMillis();
        String track = new String(Base64.getEncoder().encode("let's imagine that this is a track".getBytes()));

        doPutRequest(provider, searchTerm, lookedUp, track);
    }

    @Test
    public void getWithResult() throws Exception {
        SearchProvider provider = SearchProvider.SOUNDCLOUD;
        String searchTerm = "yet another search term";
        long lookedUp = System.currentTimeMillis();
        String track = new String(Base64.getEncoder().encode("yet another fake track".getBytes()));

        doPutRequest(provider, searchTerm, lookedUp, track);

        this.mockMvc.perform(get(baseTemplate, provider, searchTerm))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.provider", both(isA(String.class)).and(is(provider.name()))))
                .andExpect(jsonPath("$.searchTerm", both(isA(String.class)).and(is(searchTerm))))
                .andExpect(jsonPath("$.lookedUp", both(isA(String.class)).and(is(Long.toString(lookedUp)))))
                .andExpect(jsonPath("$.track", both(isA(String.class)).and(is(track))));
    }


    @Test
    public void testMaxAge() throws Exception {
        SearchProvider provider = SearchProvider.YOUTUBE;
        String searchTerm = "anime sucks";
        String track = new String(Base64.getEncoder().encode("thats just a meme".getBytes()));

        long now = System.currentTimeMillis();

        doPutRequest(provider, searchTerm, now, track);

        //allow old results to be returned
        long superOld = Long.MAX_VALUE;
        this.mockMvc.perform(get(maxAgeTemplate, provider, searchTerm, superOld))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.provider", both(isA(String.class)).and(is(provider.name()))))
                .andExpect(jsonPath("$.searchTerm", both(isA(String.class)).and(is(searchTerm))))
                .andExpect(jsonPath("$.lookedUp", both(isA(String.class)).and(is(Long.toString(now)))))
                .andExpect(jsonPath("$.track", both(isA(String.class)).and(is(track))));

        //request very recent result
        long superYoung = 0;
        this.mockMvc.perform(get(maxAgeTemplate, provider, searchTerm, superYoung))
                .andExpect(status().isNotFound());
    }

    @Test
    public void notBase64() throws Exception {
        SearchProvider provider = SearchProvider.SOUNDCLOUD;
        String searchTerm = "not base 64";
        long lookedUp = System.currentTimeMillis();
        String track = "not base 64";

        var body = new HashMap<String, Object>();
        body.put("lookedUp", lookedUp);
        body.put("track", track);

        this.mockMvc.perform(put(baseTemplate, provider, searchTerm)
                .content(this.mapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is4xxClientError());
    }

    private void doPutRequest(SearchProvider provider, String searchTerm, long lookedUp, String track)
            throws Exception {
        var body = new HashMap<String, Object>();
        body.put("lookedUp", lookedUp);
        body.put("track", track);

        RequestBuilder put = put(baseTemplate, provider, searchTerm)
                .content(this.mapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON_UTF8);


        this.mockMvc.perform(put)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.provider", both(isA(String.class)).and(is(provider.name()))))
                .andExpect(jsonPath("$.searchTerm", both(isA(String.class)).and(is(searchTerm))))
                .andExpect(jsonPath("$.lookedUp", both(isA(String.class)).and(is(Long.toString(lookedUp)))))
                .andExpect(jsonPath("$.track", both(isA(String.class)).and(is(track))));
    }
}
