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

import com.fredboat.backend.quarterdeck.BaseTest;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by napster on 22.05.18.
 */
public class Base64Test extends BaseTest {

    @Test
    public void test() {
        var attributes = new HashMap<String, Object>();
        byte[] content = new byte[4096];
        ThreadLocalRandom.current().nextBytes(content);
        attributes.put("base64", new String(Base64.getEncoder().encode(content)));
        attributes.put("notBase64", "this string is not base64 🙃");

        String base64 = PatchParseUtil.parseBase64String("base64", attributes);
        assertArrayEquals(content, Base64.getDecoder().decode(base64), "Parsing base64 modified the content");

        //noinspection ResultOfMethodCallIgnored
        assertThrows(NotBase64StringException.class, () -> PatchParseUtil.parseBase64String("notBase64", attributes));
    }
}
