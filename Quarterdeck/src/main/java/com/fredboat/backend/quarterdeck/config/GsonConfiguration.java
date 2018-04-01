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

package com.fredboat.backend.quarterdeck.config;

import com.fredboat.backend.quarterdeck.rest.v1.transfer.DiscordSnowflake;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.LongSerializationPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spring.web.json.Json;

import java.lang.reflect.Type;

/**
 * Created by napster on 30.03.18.
 */
@Configuration
public class GsonConfiguration {

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .registerTypeAdapter(Json.class, new SpringfoxJsonToGsonAdapter())
                .registerTypeAdapter(DiscordSnowflake.class, new DiscordSnowflakeoGsonAdapter())
                .create();
    }

    /**
     * Make springfox play nice with gson
     * <p>
     * source: https://stackoverflow.com/questions/30219946
     */
    private static class SpringfoxJsonToGsonAdapter implements JsonSerializer<Json> {

        private final JsonParser parser = new JsonParser();

        @Override
        public JsonElement serialize(Json json, Type type, JsonSerializationContext context) {
            return this.parser.parse(json.value());
        }
    }


    private static class DiscordSnowflakeoGsonAdapter implements JsonSerializer<DiscordSnowflake>, JsonDeserializer<DiscordSnowflake> {

        @Override
        public JsonElement serialize(DiscordSnowflake snowflake, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(snowflake.getSnowflakeId());
        }

        @Override
        public DiscordSnowflake deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            return new DiscordSnowflake(json.getAsJsonPrimitive().getAsString());
        }
    }
}
