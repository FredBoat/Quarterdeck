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
import com.fredboat.backend.quarterdeck.db.entities.main.GuildData;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.DiscordSnowflake;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.both;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by napster on 31.03.18.
 */
public class GuildDataControllerTest extends BaseTest {

    private static final String urlTemplate = "/v1/guilds/{guild_id}/data";

    @WithMockUser(roles = "ADMIN")
    @Test
    public void testGet() throws Exception {
        DiscordSnowflake guildId = generateUniqueGuildId();
        this.mockMvc.perform(get(urlTemplate, guildId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.helloSent", isA(String.class)))
                .andDo(document("guild/data/get"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void testPatch() throws Exception {
        Map<String, Object> patchGuildData = new HashMap<>();
        long now = System.currentTimeMillis();
        patchGuildData.put("helloSent", now);

        DiscordSnowflake guildId = generateUniqueGuildId();
        MockHttpServletRequestBuilder request = patch(urlTemplate, guildId)
                .content(this.gson.toJson(patchGuildData))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.helloSent", both(isA(String.class)).and(is(Long.toString(now)))))
                .andDo(document("guild/data/patch"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void testDelete() throws Exception {
        DiscordSnowflake guildId = generateUniqueGuildId();

        this.mockMvc.perform(get(urlTemplate, guildId))
                .andExpect(jsonPath("$.helloSent", is(Long.toString(GuildData.DEFAULT_HELLO_SENT_TIMESTAMP))));

        Map<String, Object> patchGuildData = new HashMap<>();
        long now = System.currentTimeMillis();
        patchGuildData.put("helloSent", now);
        MockHttpServletRequestBuilder patch = patch(urlTemplate, guildId)
                .content(this.gson.toJson(patchGuildData))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        this.mockMvc.perform(patch)
                .andExpect(jsonPath("$.helloSent", is(Long.toString(now))));

        this.mockMvc.perform(get(urlTemplate, guildId))
                .andExpect(jsonPath("$.helloSent", is(Long.toString(now))));

        this.mockMvc.perform(delete(urlTemplate, guildId))
                .andExpect(status().isOk())
                .andDo(document("guild/data/delete"));

        this.mockMvc.perform(get(urlTemplate, guildId))
                .andExpect(jsonPath("$.helloSent", is(Long.toString(GuildData.DEFAULT_HELLO_SENT_TIMESTAMP))));
    }
}
