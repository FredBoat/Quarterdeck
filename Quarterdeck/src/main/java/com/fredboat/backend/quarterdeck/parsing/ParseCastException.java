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

/**
 * Created by napster on 30.03.18.
 */
public class ParseCastException extends ParseException {

    private final String key;
    private final Object value;
    private final Class expected;

    public ParseCastException(String key, Object value, Class expected, Throwable cause) {
        super(cause);
        this.key = key;
        this.value = value;
        this.expected = expected;
    }

    public ParseCastException(String key, Object value, Class expected) {
        this(key, value, expected, null);
    }

    private static final String messageTemplate = "Failed to cast the value %s for key %s: class is %s but should be %s";

    @Override
    public String getMessage() {
        return String.format(messageTemplate,
                this.value, this.key, this.value.getClass().getSimpleName(), this.expected.getSimpleName());
    }
}
