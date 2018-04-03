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

package com.fredboat.backend.quarterdeck;

import fredboat.definitions.Language;
import fredboat.definitions.RepeatMode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Created by napster on 02.04.18.
 * <p>
 * Test enum types of the public API for being accepted by the API.
 */
public class EnumTypesTest extends BaseTest {


    @Test
    public void testRepeatModeEnumAllAccepted() throws Exception {
        Map<String, Object> patch = new HashMap<>();
        MockHttpServletRequestBuilder request = patch("/v1/guilds/{guild_id}/player", generateUniqueSnowflakeId())
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        for (RepeatMode repeatMode : RepeatMode.values()) {
            patch.put("repeatMode", repeatMode);
            this.mockMvc.perform(request.content(this.gson.toJson(patch)))
                    .andExpect(jsonPath("$.repeatMode", is(equalToIgnoringCase(repeatMode.name()))));
        }
    }

    @Test
    public void testLanguageEnumAllAccepted() throws Exception {
        Map<String, Object> patch = new HashMap<>();
        MockHttpServletRequestBuilder request = patch("/v1/guilds/{guild_id}/config", generateUniqueSnowflakeId())
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        for (Language language : Language.values()) {
            patch.put("language", language);
            this.mockMvc.perform(request.content(this.gson.toJson(patch)))
                    .andExpect(jsonPath("$.language", is(equalToIgnoringCase(language.name()))));
        }
    }

}
