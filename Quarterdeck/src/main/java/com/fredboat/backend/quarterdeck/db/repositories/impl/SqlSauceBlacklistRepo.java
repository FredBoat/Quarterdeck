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

package com.fredboat.backend.quarterdeck.db.repositories.impl;

import com.fredboat.backend.quarterdeck.db.entities.main.BlacklistEntry;
import com.fredboat.backend.quarterdeck.db.repositories.api.BlacklistRepo;
import org.springframework.stereotype.Component;
import space.npstr.sqlsauce.DatabaseWrapper;
import space.npstr.sqlsauce.fp.types.EntityKey;

import java.util.List;
import java.util.function.Function;

/**
 * Created by napster on 05.02.18.
 */
@Component
public class SqlSauceBlacklistRepo extends SqlSauceRepo<Long, BlacklistEntry> implements BlacklistRepo {

    public SqlSauceBlacklistRepo(DatabaseWrapper dbWrapper) {
        super(dbWrapper, BlacklistEntry.class);
    }

    @Override
    public List<BlacklistEntry> loadBlacklist() {
        return this.dbWrapper.loadAll(BlacklistEntry.class);
    }

    @Override
    public BlacklistEntry transform(long id, Function<BlacklistEntry, BlacklistEntry> transformation) {
        EntityKey<Long, BlacklistEntry> key = EntityKey.of(id, BlacklistEntry.class);
        return this.dbWrapper.findApplyAndMerge(key, transformation);
    }
}
