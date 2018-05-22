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

package com.fredboat.backend.quarterdeck.parsing;

import com.fredboat.backend.quarterdeck.rest.v1.transfer.DiscordSnowflake;

import javax.annotation.CheckReturnValue;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created by napster on 28.03.18.
 */
public class PatchParseUtil {

    //source: https://stackoverflow.com/a/8571649
    private static final Pattern BASE64
            = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$");

    /**
     * @return the base 64 string parsed from the provided map of attributes and provided key
     *
     * @throws ParseException
     *         If the input did not fit our high quality stack overflow sourced base64 regex.
     */
    @CheckReturnValue
    public static String parseBase64String(String key, Map<String, Object> attributes) {
        String value = parseString(key, attributes);
        if (!BASE64.matcher(value).matches()) {
            throw new NotBase64StringException(key, value);
        }
        return value;
    }

    /**
     * @return the DiscordSnowflake parsed from the provided map of attributes and provided key
     *
     * @throws ParseException
     *         If we were not able to parse the input into a {@link DiscordSnowflake}.
     */
    @CheckReturnValue
    public static DiscordSnowflake parseDiscordSnowflake(String key, Map<String, Object> attributes) {
        Object value = attributes.get(key);
        if (value instanceof Long) { // be lenient
            return new DiscordSnowflake((long) value);
        } else if (value instanceof String) {
            try {
                return new DiscordSnowflake((String) (value));
            } catch (Exception e) {
                throw new DiscordSnowflakeParseException(key, value);
            }
        } else {
            throw new DiscordSnowflakeParseException(key, value);
        }
    }

    /**
     * @return the integer parsed from the provided map of attributes and provided key
     *
     * @throws ParseException
     *         If we were not able to parse the input into an integer.
     */
    @CheckReturnValue
    public static int parseInt(String key, Map<String, Object> attributes) {
        Object value = attributes.get(key);
        if (value instanceof Integer) {
            return (int) value;
        } else if (value instanceof Number) {
            //this can lead to precision loss, but only for values that would be anyways outside of the expected integer range
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) (value));
            } catch (Exception e) {
                throw new NumberParseException(key, value, Integer.class);
            }
        } else {
            throw new NumberParseException(key, value, Integer.class);
        }
    }

    /**
     * @return the long parsed from the provided map of attributes and provided key
     *
     * @throws ParseException
     *         If we were not able to parse the input into a long.
     */
    @CheckReturnValue
    public static long parseLong(String key, Map<String, Object> attributes) {
        Object value = attributes.get(key);
        if (value instanceof Long) {
            return (long) value;
        } else if (value instanceof Number) {
            //this can lead to precision loss, but only for values that would be anyways outside of the expected long range
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String) (value));
            } catch (Exception e) {
                throw new NumberParseException(key, value, Long.class);
            }
        } else {
            throw new NumberParseException(key, value, Long.class);
        }
    }

    /**
     * @return the boolean parsed from the provided map of attributes and provided key
     *
     * @throws ParseException
     *         If anything went wrong.
     */
    @CheckReturnValue
    public static boolean parseBoolean(String key, Map<String, Object> attributes) throws ParseCastException {
        return cast(key, attributes, Boolean.class);
    }

    /**
     * @return the String parsed from the provided map of attributes and provided key
     *
     * @throws ParseException
     *         If anything went wrong.
     */
    @CheckReturnValue
    public static String parseString(String key, Map<String, Object> attributes) throws ParseCastException {
        return cast(key, attributes, String.class);
    }

    /**
     * @return The nonnull value behind the provided key casted to the provided class.
     *
     * @throws ParseException
     *         If the cast went wrong, or the value is null. Use {@link PatchParseUtil#castNullable(String, Map, Class)}
     *         if you want to allow nullable values.
     */
    @CheckReturnValue
    public static <T> T cast(String key, Map<String, Object> attributes, Class<T> clazz) {
        return castNullable(key, attributes, clazz).orElseThrow(
                () -> new ParseCastNullException(key, clazz)
        );
    }

    /**
     * @return The value behind the provided key casted to the provided class. May return an empty optional if the input
     * value found behind hte provided key in the provided map of attributes was null.
     *
     * @throws ParseException
     *         If the cast went wrong.
     */
    @SuppressWarnings("unchecked")
    @CheckReturnValue
    public static <T> Optional<T> castNullable(String key, Map<String, Object> attributes, Class<T> clazz) {
        Object value = attributes.get(key);
        try {
            return Optional.ofNullable((T) value);
        } catch (Exception e) {
            throw new ParseCastException(key, value, clazz, e);
        }
    }
}
