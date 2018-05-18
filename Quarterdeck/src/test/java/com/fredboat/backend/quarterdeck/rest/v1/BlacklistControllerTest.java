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
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.fredboat.backend.quarterdeck.Matchers.IsGreaterThan.isGreaterThan;
import static com.fredboat.backend.quarterdeck.Matchers.IsLowerThan.isLowerThan;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by napster on 18.05.18.
 */
public class BlacklistControllerTest extends BaseTest {

    private static final String urlTemplate = "/v1/blacklist/{user_id}";

    @WithMockUser(roles = "ADMIN")
    @Test
    public void noEntry() throws Exception {
        DiscordSnowflake userId = generateUniqueSnowflakeId();

        requestAndExpectEmptyEntry(userId);
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void existingEntryAndReset() throws Exception {
        DiscordSnowflake userId = generateUniqueSnowflakeId();

        // currently entries can only be created as a result of repeatedly hitting a ratelimit, so we're going to use
        // some help of the RatelimitControllerTest here
        Rate rate = Rate.USER_SHARDS_COMM;
        Rate[] rates = {rate};
        RatelimitRequest payload = new RatelimitRequest(rates, userId, null, 1);

        MockHttpServletRequestBuilder ratelimitRequest = post(RatelimitControllerTest.baseTemplate)
                .content(this.mapper.writeValueAsString(payload))
                .contentType(MediaType.APPLICATION_JSON_UTF8);

        //exhaust the ratelimit
        for (int ii = 0; ii < rate.getRequests(); ii++) {
            RatelimitControllerTest.requestAndExpectGranted(this.mockMvc, ratelimitRequest);
        }

        //exhaust the ratelimit hits
        int hitsWithoutBlacklistIssued = BlacklistService.RATE_LIMIT_HITS_BEFORE_BLACKLIST - 1;
        for (int ii = 0; ii < hitsWithoutBlacklistIssued; ii++) {
            RatelimitControllerTest.requestAndExpectRatelimited(this.mockMvc, ratelimitRequest, rate);
        }

        RatelimitControllerTest.requestAndExpectNewlyBlacklisted(this.mockMvc, ratelimitRequest, rate);
        RatelimitControllerTest.requestAndExpectAlreadyBlacklisted(this.mockMvc, ratelimitRequest);


        //now, check the blacklist entry
        this.mockMvc.perform(get(urlTemplate, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.snowflakeId", both(isA(String.class)).and(is(userId.getSnowflakeId()))))
                .andExpect(jsonPath("$.level", is(0)))
                .andExpect(jsonPath("$.blacklistedUntil",
                        both(isA(String.class)).and(isGreaterThan(System.currentTimeMillis()))));


        //reset the entry
        this.mockMvc.perform(delete(urlTemplate, userId))
                .andExpect(status().isOk());

        requestAndExpectEmptyEntry(userId);
    }

    private void requestAndExpectEmptyEntry(DiscordSnowflake snowflake) throws Exception {
        this.mockMvc.perform(get(urlTemplate, snowflake))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.snowflakeId", both(isA(String.class)).and(is(snowflake.getSnowflakeId()))))
                .andExpect(jsonPath("$.level", is(-1)))
                .andExpect(jsonPath("$.blacklistedUntil",
                        both(isA(String.class)).and(isLowerThan(System.currentTimeMillis()))));
    }
}
