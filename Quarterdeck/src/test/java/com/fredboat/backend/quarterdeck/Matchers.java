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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Created by napster on 18.05.18.
 * <p>
 * Collection of custom Matcher implementations
 */
public class Matchers {

    private Matchers() {}

    /**
     * Parse a String to a long and make sure it is greater than the provided value.
     */
    public static class IsGreaterThan extends BaseMatcher<String> {

        private final long value;

        public IsGreaterThan(long value) {
            this.value = value;
        }

        public static IsGreaterThan isGreaterThan(long value) {
            return new IsGreaterThan(value);
        }

        @Override
        public boolean matches(Object item) {
            if (!(item instanceof String)) {
                return false;
            }

            String raw = (String) item;
            long itemValue;
            try {
                itemValue = Long.parseLong(raw);
            } catch (Exception ignored) {
                return false;
            }

            return itemValue > this.value;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(" is a long greater than " + this.value);
        }
    }


    /**
     * Parse a String to a long and make sure it is lower than the provided value.
     */
    public static class IsLowerThan extends BaseMatcher<String> {

        private final long value;

        public IsLowerThan(long value) {
            this.value = value;
        }

        public static IsLowerThan isLowerThan(long value) {
            return new IsLowerThan(value);
        }

        @Override
        public boolean matches(Object item) {
            if (!(item instanceof String)) {
                return false;
            }

            String raw = (String) item;
            long itemValue;
            try {
                itemValue = Long.parseLong(raw);
            } catch (Exception ignored) {
                return false;
            }

            return itemValue < this.value;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(" is a long lower than " + this.value);
        }
    }
}
