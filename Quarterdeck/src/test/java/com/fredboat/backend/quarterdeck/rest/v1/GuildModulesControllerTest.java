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
import com.fredboat.backend.quarterdeck.rest.v1.transfer.DiscordSnowflake;
import fredboat.definitions.Module;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by napster on 06.04.18.
 */
public class GuildModulesControllerTest extends BaseTest {

    private static final String urlTemplate = "/v1/guilds/{guild_id}/modules";
    private static final String urlTemplateModule = urlTemplate + "/{module_id}";

    @WithMockUser(roles = "ADMIN")
    @Test
    public void testGet() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        this.mockMvc.perform(get(urlTemplate, guildId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.modules.length()", is(7)))
                .andExpect(jsonPath("$.modules[0].moduleId", both(isA(String.class))
                        .and(is(equalToIgnoringCase(Module.ADMIN.name())))))
                .andExpect(jsonPath("$.modules[0].enabled", isA(Boolean.class)))
                .andExpect(jsonPath("$.modules[1].moduleId", both(isA(String.class))
                        .and(is(equalToIgnoringCase(Module.INFO.name())))))
                .andExpect(jsonPath("$.modules[1].enabled", isA(Boolean.class)))
                .andExpect(jsonPath("$.modules[2].moduleId", both(isA(String.class))
                        .and(is(equalToIgnoringCase(Module.CONFIG.name())))))
                .andExpect(jsonPath("$.modules[2].enabled", isA(Boolean.class)))
                .andExpect(jsonPath("$.modules[3].moduleId", both(isA(String.class))
                        .and(is(equalToIgnoringCase(Module.MUSIC.name())))))
                .andExpect(jsonPath("$.modules[3].enabled", isA(Boolean.class)))
                .andExpect(jsonPath("$.modules[4].moduleId", both(isA(String.class))
                        .and(is(equalToIgnoringCase(Module.MOD.name())))))
                .andExpect(jsonPath("$.modules[4].enabled", isA(Boolean.class)))
                .andExpect(jsonPath("$.modules[5].moduleId", both(isA(String.class))
                        .and(is(equalToIgnoringCase(Module.UTIL.name())))))
                .andExpect(jsonPath("$.modules[5].enabled", isA(Boolean.class)))
                .andExpect(jsonPath("$.modules[6].moduleId", both(isA(String.class))
                        .and(is(equalToIgnoringCase(Module.FUN.name())))))
                .andExpect(jsonPath("$.modules[6].enabled", isA(Boolean.class)));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void testReset() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();

        Module module = Module.FUN;
        this.mockMvc.perform(get(urlTemplate, guildId))
                .andExpect(jsonPath("$.modules[6].moduleId", is(equalToIgnoringCase(module.name()))))
                .andExpect(jsonPath("$.modules[6].enabled", is(module.isEnabledByDefault())));

        Map<String, Object> patchGuildModule = new HashMap<>();
        patchGuildModule.put("enabled", !module.isEnabledByDefault()); //no fun allowed :[
        MockHttpServletRequestBuilder patch = patch(urlTemplateModule, guildId, module.name())
                .content(this.mapper.writeValueAsString(patchGuildModule))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        this.mockMvc.perform(patch)
                .andExpect(jsonPath("$.modules[6].moduleId", is(equalToIgnoringCase(module.name()))))
                .andExpect(jsonPath("$.modules[6].enabled", is(!module.isEnabledByDefault())));

        this.mockMvc.perform(get(urlTemplate, guildId))
                .andExpect(jsonPath("$.modules[6].moduleId", is(equalToIgnoringCase(module.name()))))
                .andExpect(jsonPath("$.modules[6].enabled", is(!module.isEnabledByDefault())));

        this.mockMvc.perform(delete(urlTemplateModule, guildId, module.name()))
                .andExpect(status().isOk());

        this.mockMvc.perform(get(urlTemplate, guildId))
                .andExpect(jsonPath("$.modules[6].moduleId", is(equalToIgnoringCase(module.name()))))
                .andExpect(jsonPath("$.modules[6].enabled", is(module.isEnabledByDefault())));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void testPatching() throws Exception {
        boolean enabled = true;
        for (Module module : Module.values()) {
            enabled = !enabled;
            patchAndCheckSingleModule(module, enabled);
        }
    }

    private void patchAndCheckSingleModule(Module module, boolean enabled) throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();

        Map<String, Object> patchGuildModule = new HashMap<>();
        patchGuildModule.put("enabled", enabled);
        this.mockMvc.perform(
                patch(urlTemplateModule, guildId, module.name())
                        .content(this.mapper.writeValueAsString(patchGuildModule))
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.modules.length()", is(7)))
                .andExpect(jsonPath("$.modules[?(@.moduleId =~ /^" + module.name() + "$/i)].enabled",
                        iterableWithSize(1)))
                .andExpect(jsonPath("$.modules[?(@.moduleId =~ /^" + module.name() + "$/i)].enabled",
                        containsInAnyOrder(enabled)));
    }


    @WithMockUser(roles = "ADMIN")
    @Test
    public void testResetSingleValueDirectly() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();

        Module module = Module.UTIL;
        this.mockMvc.perform(get(urlTemplate, guildId))
                .andExpect(jsonPath("$.modules[?(@.moduleId =~ /^" + module.name() + "$/i)].enabled",
                        iterableWithSize(1)))
                .andExpect(jsonPath("$.modules[?(@.moduleId =~ /^" + module.name() + "$/i)].enabled",
                        containsInAnyOrder(module.isEnabledByDefault())));

        Map<String, Object> patchGuildModule = new HashMap<>();
        patchGuildModule.put("enabled", !module.isEnabledByDefault());
        MockHttpServletRequestBuilder patch = patch(urlTemplateModule, guildId, module.name())
                .content(this.mapper.writeValueAsString(patchGuildModule))
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        this.mockMvc.perform(patch)
                .andExpect(jsonPath("$.modules[?(@.moduleId =~ /^" + module.name() + "$/i)].enabled",
                        iterableWithSize(1)))
                .andExpect(jsonPath("$.modules[?(@.moduleId =~ /^" + module.name() + "$/i)].enabled",
                        containsInAnyOrder(!module.isEnabledByDefault())));

        this.mockMvc.perform(get(urlTemplate, guildId))
                .andExpect(jsonPath("$.modules[?(@.moduleId =~ /^" + module.name() + "$/i)].enabled",
                        iterableWithSize(1)))
                .andExpect(jsonPath("$.modules[?(@.moduleId =~ /^" + module.name() + "$/i)].enabled",
                        containsInAnyOrder(!module.isEnabledByDefault())));

        this.mockMvc.perform(delete(urlTemplateModule, guildId, module.name()))
                .andExpect(status().isOk());

        this.mockMvc.perform(get(urlTemplate, guildId))
                .andExpect(jsonPath("$.modules[?(@.moduleId =~ /^" + module.name() + "$/i)].enabled",
                        iterableWithSize(1)))
                .andExpect(jsonPath("$.modules[?(@.moduleId =~ /^" + module.name() + "$/i)].enabled",
                        containsInAnyOrder(module.isEnabledByDefault())));
    }
}
