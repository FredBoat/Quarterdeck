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

package com.fredboat.backend.quarterdeck.db.repositories.api;

import com.fredboat.backend.quarterdeck.db.entities.main.BlacklistEntry;

import java.util.List;
import java.util.function.Function;

/**
 * Created by napster on 05.02.18.
 */
public interface BlacklistRepo extends Repo<Long, BlacklistEntry> {

    /**
     * @return the whole blacklist aka all entries. Not a lightweight operation, and shouldn't be called outside
     * of initial population of the blacklist (and probably not even then, reworking the ratelimiter is planned).
     *
     * @deprecated This loads a ton of data all at once. A better endpoint will be provided in the future, if a
     * legitimate use case can be found. Meanwhile, {@link BlacklistRepo#fetch(Object)}} + a cache are good enough.
     */
    @Deprecated
    List<BlacklistEntry> loadBlacklist();


    /**
     * General purpose change to the blacklist entry in a single transaction.
     *
     * @param id
     *         the id of the blacklist entry to be changed
     * @param transformation
     *         this function will have the blacklist entry that is loaded from the database / newly constructed applied,
     *         the result will be merged back into the database.
     *
     * @return the merged blacklist entry
     */
    BlacklistEntry transform(long id, Function<BlacklistEntry, BlacklistEntry> transformation);
}
