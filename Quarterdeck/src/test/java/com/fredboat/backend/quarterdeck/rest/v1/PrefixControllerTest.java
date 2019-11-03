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
import com.fredboat.backend.quarterdeck.db.entities.main.Prefix;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.DiscordSnowflake;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.core.CombinableMatcher.both;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.hamcrest.core.IsIterableContaining.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by napster on 03.04.18.
 */
public class PrefixControllerTest extends BaseTest {

    private static final String urlTemplate = "/v1/guilds/{guild_id}/prefix?bot_id={bot_id}";

    //get
    @WithMockUser(roles = "ADMIN")
    @Test
    public void testGet() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        DiscordSnowflake botId = generateUniqueSnowflakeId();
        this.mockMvc.perform(get(urlTemplate, guildId, botId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.botId", both(isA(String.class)).and(is(botId.getSnowflakeId()))))
                .andExpect(jsonPath("$.prefixes", hasItems(Prefix.DEFAULT_PREFIXES.toArray(new String[0]))))
                .andExpect(jsonPath("$.prefixes.length()", is(Prefix.DEFAULT_PREFIXES.size())));
    }

    //delete
    @WithMockUser(roles = "ADMIN")
    @Test
    public void testDelete() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        DiscordSnowflake botId = generateUniqueSnowflakeId();
        this.mockMvc.perform(
                delete(urlTemplate, guildId, botId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.botId", both(isA(String.class)).and(is(botId.getSnowflakeId()))))
                .andExpect(jsonPath("$.prefixes", hasItems(Prefix.DEFAULT_PREFIXES.toArray(new String[0]))))
                .andExpect(jsonPath("$.prefixes.length()", is(Prefix.DEFAULT_PREFIXES.size())));
    }

    //test add single prefix
    @WithMockUser(roles = "ADMIN")
    @Test
    public void testAddSingle() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        DiscordSnowflake botId = generateUniqueSnowflakeId();
        String[] addPrefix = {"this_is_a_prefix_in_an_array"};
        this.mockMvc.perform(
                post(urlTemplate, guildId, botId)
                        .content(this.mapper.writeValueAsString(addPrefix))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.botId", both(isA(String.class)).and(is(botId.getSnowflakeId()))))
                .andExpect(jsonPath("$.prefixes", hasItems(Prefix.DEFAULT_PREFIXES.toArray(new String[0]))))
                .andExpect(jsonPath("$.prefixes", hasItems(addPrefix)))
                .andExpect(jsonPath("$.prefixes.length()", is(Prefix.DEFAULT_PREFIXES.size() + 1)));
    }

    //test remove single prefix
    @WithMockUser(roles = "ADMIN")
    @Test
    public void testDeleteSingle() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        DiscordSnowflake botId = generateUniqueSnowflakeId();
        String[] removePrefix = {";;"};
        this.mockMvc.perform(
                delete(urlTemplate, guildId, botId)
                        .content(this.mapper.writeValueAsString(removePrefix))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.botId", both(isA(String.class)).and(is(botId.getSnowflakeId()))))
                .andExpect(jsonPath("$.prefixes", hasItems("!")))
                .andExpect(jsonPath("$.prefixes.length()", is(Prefix.DEFAULT_PREFIXES.size() - 1)));
    }

    //test add multiple prefixes
    @WithMockUser(roles = "ADMIN")
    @Test
    public void testAddMultiple() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        DiscordSnowflake botId = generateUniqueSnowflakeId();
        String[] addPrefixes = {"this_is_a_prefix", "this_is_another_one"};
        this.mockMvc.perform(
                post(urlTemplate, guildId, botId)
                        .content(this.mapper.writeValueAsString(addPrefixes))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.botId", both(isA(String.class)).and(is(botId.getSnowflakeId()))))
                .andExpect(jsonPath("$.prefixes", hasItems(Prefix.DEFAULT_PREFIXES.toArray(new String[0]))))
                .andExpect(jsonPath("$.prefixes", hasItems(addPrefixes)))
                .andExpect(jsonPath("$.prefixes.length()", is(Prefix.DEFAULT_PREFIXES.size() + addPrefixes.length)));
    }

    //test remove multiple prefixes
    @WithMockUser(roles = "ADMIN")
    @Test
    public void testDeleteMultiple() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        DiscordSnowflake botId = generateUniqueSnowflakeId();

        String[] addPrefixes = {"1", "2", "3", "4"};
        this.mockMvc.perform(
                post(urlTemplate, guildId, botId)
                        .content(this.mapper.writeValueAsString(addPrefixes))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.botId", both(isA(String.class)).and(is(botId.getSnowflakeId()))))
                .andExpect(jsonPath("$.prefixes", hasItems(Prefix.DEFAULT_PREFIXES.toArray(new String[0]))))
                .andExpect(jsonPath("$.prefixes", hasItems(addPrefixes)))
                .andExpect(jsonPath("$.prefixes.length()", is(Prefix.DEFAULT_PREFIXES.size() + addPrefixes.length)));

        this.mockMvc.perform(
                delete(urlTemplate, guildId, botId)
                        .content(this.mapper.writeValueAsString(addPrefixes))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.botId", both(isA(String.class)).and(is(botId.getSnowflakeId()))))
                .andExpect(jsonPath("$.prefixes", hasItems(Prefix.DEFAULT_PREFIXES.toArray(new String[0]))))
                .andExpect(jsonPath("$.prefixes.length()", is(Prefix.DEFAULT_PREFIXES.size())));
    }

    //test remove all prefixes
    @WithMockUser(roles = "ADMIN")
    @Test
    public void testDeleteAll() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        DiscordSnowflake botId = generateUniqueSnowflakeId();

        String[] current = this.mapper.readValue(this.mockMvc.perform(get(urlTemplate, guildId, botId))
                .andReturn()
                .getResponse().getContentAsString(), com.fredboat.backend.quarterdeck.rest.v1.transfer.Prefix.class)
                .getPrefixes();

        this.mockMvc.perform(
                delete(urlTemplate, guildId, botId)
                        .content(this.mapper.writeValueAsString(current))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.botId", both(isA(String.class)).and(is(botId.getSnowflakeId()))))
                //ensure the default ones are returned after emptying it completely
                .andExpect(jsonPath("$.prefixes", hasItems(Prefix.DEFAULT_PREFIXES.toArray(new String[0]))))
                .andExpect(jsonPath("$.prefixes.length()", is(Prefix.DEFAULT_PREFIXES.size())));
    }

}
