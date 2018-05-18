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
import com.fredboat.backend.quarterdeck.ratelimit.BlacklistService;
import com.fredboat.backend.quarterdeck.ratelimit.Rate;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.DiscordSnowflake;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.RatelimitRequest;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.RatelimitResponse;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.fredboat.backend.quarterdeck.Matchers.IsGreaterThan.isGreaterThan;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by napster on 18.05.18.
 */
public class RatelimitControllerTest extends BaseTest {

    public static final String baseTemplate = "/v1/ratelimit";
    private static final String deleteTemplate = baseTemplate + "/{snowflake_id}";


    @WithMockUser(roles = "ADMIN")
    @Test
    public void granted() throws Exception {
        DiscordSnowflake userId = generateUniqueSnowflakeId();
        DiscordSnowflake guildId = generateUniqueSnowflakeId();

        Rate[] rates = {Rate.USER_ALL_COMMS, Rate.GUILD_ALL_COMMS};
        RatelimitRequest payload = new RatelimitRequest(rates, userId, guildId, 1);

        requestAndExpectGranted(this.mockMvc, post(baseTemplate)
                .content(this.mapper.writeValueAsString(payload))
                .contentType(MediaType.APPLICATION_JSON_UTF8));
    }


    @WithMockUser(roles = "ADMIN")
    @Test
    public void ratelimited() throws Exception {
        DiscordSnowflake userId = generateUniqueSnowflakeId();
        DiscordSnowflake guildId = generateUniqueSnowflakeId();

        Rate rate = Rate.USER_EXPORT_COMM;

        Rate[] rates = {rate};
        RatelimitRequest payload = new RatelimitRequest(rates, userId, guildId, 1);

        MockHttpServletRequestBuilder request = post(baseTemplate)
                .content(this.mapper.writeValueAsString(payload))
                .contentType(MediaType.APPLICATION_JSON_UTF8);

        //exhaust the ratelimit
        for (int ii = 0; ii < rate.getRequests(); ii++) {
            requestAndExpectGranted(this.mockMvc, request);
        }

        requestAndExpectRatelimited(this.mockMvc, request, rate);
    }


    @WithMockUser(roles = "ADMIN")
    @Test
    public void blacklisted() throws Exception {
        DiscordSnowflake userId = generateUniqueSnowflakeId();
        DiscordSnowflake guildId = generateUniqueSnowflakeId();

        Rate rate = Rate.USER_SHARDS_COMM;

        Rate[] rates = {rate};
        RatelimitRequest payload = new RatelimitRequest(rates, userId, guildId, 1);

        MockHttpServletRequestBuilder request = post(baseTemplate)
                .content(this.mapper.writeValueAsString(payload))
                .contentType(MediaType.APPLICATION_JSON_UTF8);

        //exhaust the ratelimit
        for (int ii = 0; ii < rate.getRequests(); ii++) {
            requestAndExpectGranted(this.mockMvc, request);
        }

        //exhaust the ratelimit hits
        int hitsWithoutBlacklistIssued = BlacklistService.RATE_LIMIT_HITS_BEFORE_BLACKLIST - 1;
        for (int ii = 0; ii < hitsWithoutBlacklistIssued; ii++) {
            requestAndExpectRatelimited(this.mockMvc, request, rate);
        }

        requestAndExpectNewlyBlacklisted(this.mockMvc, request, rate);

        requestAndExpectAlreadyBlacklisted(this.mockMvc, request);
    }


    @WithMockUser(roles = "ADMIN")
    @Test
    public void testResetRatelimit() throws Exception {
        DiscordSnowflake userId = generateUniqueSnowflakeId();

        Rate rate = Rate.USER_ALL_COMMS;

        Rate[] rates = {rate};
        RatelimitRequest payload = new RatelimitRequest(rates, userId, null, 1);

        MockHttpServletRequestBuilder request = post(baseTemplate)
                .content(this.mapper.writeValueAsString(payload))
                .contentType(MediaType.APPLICATION_JSON_UTF8);

        //exhaust the ratelimit
        for (int ii = 0; ii < rate.getRequests(); ii++) {
            requestAndExpectGranted(this.mockMvc, request);
        }

        requestAndExpectRatelimited(this.mockMvc, request, rate);

        this.mockMvc.perform(delete(deleteTemplate, userId))
                .andExpect(status().isOk());

        requestAndExpectGranted(this.mockMvc, request);
    }


    @WithMockUser(roles = "ADMIN")
    @Test
    public void testGuildNotBlacklisted() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();

        Rate rate = Rate.GUILD_ALL_COMMS;

        Rate[] rates = {rate};
        RatelimitRequest payload = new RatelimitRequest(rates, null, guildId, 1);

        MockHttpServletRequestBuilder request = post(baseTemplate)
                .content(this.mapper.writeValueAsString(payload))
                .contentType(MediaType.APPLICATION_JSON_UTF8);

        //exhaust the ratelimit
        for (int ii = 0; ii < rate.getRequests(); ii++) {
            requestAndExpectGranted(this.mockMvc, request);
        }

        //exhaust the ratelimit hits
        int hitsWithoutBlacklistIssued = BlacklistService.RATE_LIMIT_HITS_BEFORE_BLACKLIST - 1;
        hitsWithoutBlacklistIssued += 5; //add a few more for good measure
        for (int ii = 0; ii < hitsWithoutBlacklistIssued; ii++) {
            requestAndExpectRatelimited(this.mockMvc, request, rate);
        }

        //just ratelimited, not blacklisted
        requestAndExpectRatelimited(this.mockMvc, request, rate);
    }


    //these 4 methods correspond to the 4 basic types of responses as details in the RatelimitResponse class

    static void requestAndExpectGranted(MockMvc mock, MockHttpServletRequestBuilder request) throws Exception {
        doRequest(mock, request,
                nullValue(),
                is(Long.toString(RatelimitResponse.NOT_BLACKLISTED)));
    }

    static void requestAndExpectRatelimited(MockMvc mock, MockHttpServletRequestBuilder request, Rate rate)
            throws Exception {
        doRequest(mock, request,
                is(rate.name()),
                is(Long.toString(RatelimitResponse.NOT_BLACKLISTED)));
    }

    static void requestAndExpectAlreadyBlacklisted(MockMvc mock, MockHttpServletRequestBuilder request)
            throws Exception {
        doRequest(mock, request,
                nullValue(),
                is(Long.toString(RatelimitResponse.IS_BLACKLISTED)));
    }

    static void requestAndExpectNewlyBlacklisted(MockMvc mock, MockHttpServletRequestBuilder request, Rate rate)
            throws Exception {
        doRequest(mock, request,
                is(rate.name()),
                isGreaterThan(RatelimitResponse.IS_BLACKLISTED));
    }

    private static void doRequest(MockMvc mock, MockHttpServletRequestBuilder request,
                                  Matcher<Object> rateMatcher,
                                  Matcher<String> blacklistedLengthMatcher) throws Exception {
        mock.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.rate", rateMatcher))
                .andExpect(jsonPath("$.blacklistedLength",
                        both(isA(String.class)).and(blacklistedLengthMatcher)));
    }
}
