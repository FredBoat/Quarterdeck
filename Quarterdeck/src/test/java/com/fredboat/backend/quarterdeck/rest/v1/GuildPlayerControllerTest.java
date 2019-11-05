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
import com.fredboat.backend.quarterdeck.db.entities.main.GuildPlayer;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.DiscordSnowflake;
import com.fredboat.backend.shared.RepeatMode;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by napster on 28.03.18.
 */
public class GuildPlayerControllerTest extends BaseTest {

    private static final String urlTemplate = "/v1/guilds/{guild_id}/player";

    @WithMockUser(roles = "ADMIN")
    @Test
    public void testGet() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        this.mockMvc.perform(get(urlTemplate, guildId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.voiceChannelId", isA(String.class)))
                .andExpect(jsonPath("$.activeTextChannelId", isA(String.class)))
                .andExpect(jsonPath("$.paused", isA(Boolean.class)))
                .andExpect(jsonPath("$.volume", isA(Integer.class)))
                .andExpect(jsonPath("$.repeatMode", isA(String.class)))
                .andExpect(jsonPath("$.shuffled", isA(Boolean.class)));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void testPatch() throws Exception {
        Map<String, Object> patchGuildPlayer = new HashMap<>();
        patchGuildPlayer.put("voiceChannelId", "42");
        patchGuildPlayer.put("activeTextChannelId", Long.MAX_VALUE);
        patchGuildPlayer.put("paused", false);
        patchGuildPlayer.put("volume", 3);
        patchGuildPlayer.put("repeatMode", RepeatMode.ALL);

        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        MockHttpServletRequestBuilder request = patch(urlTemplate, guildId)
                .content(this.mapper.writeValueAsString(patchGuildPlayer))
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.voiceChannelId", both(isA(String.class)).and(is("42"))))
                .andExpect(jsonPath("$.activeTextChannelId", both(isA(String.class))
                        .and(is(Long.toString(Long.MAX_VALUE)))))
                .andExpect(jsonPath("$.paused", both(isA(Boolean.class)).and(is(false))))
                .andExpect(jsonPath("$.volume", both(isA(Integer.class)).and(is(3))))
                .andExpect(jsonPath("$.repeatMode", both(Is.<String>isA(String.class))
                        .and(is(equalToIgnoringCase(RepeatMode.ALL.name())))))
                .andExpect(jsonPath("$.shuffled", both(isA(Boolean.class)).and(is(false))));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void testDelete() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();

        this.mockMvc.perform(get(urlTemplate, guildId))
                .andExpect(jsonPath("$.volume", is(GuildPlayer.DEFAULT_VOLUME)));

        Map<String, Object> patchGuildData = new HashMap<>();
        patchGuildData.put("volume", 69);
        MockHttpServletRequestBuilder patch = patch(urlTemplate, guildId)
                .content(this.mapper.writeValueAsString(patchGuildData))
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        this.mockMvc.perform(patch)
                .andExpect(jsonPath("$.volume", is(69)));

        this.mockMvc.perform(get(urlTemplate, guildId))
                .andExpect(jsonPath("$.volume", is(69)));

        this.mockMvc.perform(delete(urlTemplate, guildId))
                .andExpect(status().isOk());

        this.mockMvc.perform(get(urlTemplate, guildId))
                .andExpect(jsonPath("$.volume", is(GuildPlayer.DEFAULT_VOLUME)));
    }
}
