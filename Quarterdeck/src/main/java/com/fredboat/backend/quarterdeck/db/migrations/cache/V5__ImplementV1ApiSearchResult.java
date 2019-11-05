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

package com.fredboat.backend.quarterdeck.db.migrations.cache;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

/**
 * Created by napster on 18.05.18.
 */
public class V5__ImplementV1ApiSearchResult extends BaseJavaMigration {

    //language=PostgreSQL
    private static final String DROP
            = "DROP TABLE IF EXISTS public.search_results;";

    //language=PostgreSQL
    private static final String CREATE
            = "CREATE TABLE public.search_results "
            + "( "
            + "    provider         SearchProvider NOT NULL, "
            + "    search_term      TEXT COLLATE pg_catalog.\"POSIX\" NOT NULL, "
            + "    looked_up        BIGINT NOT NULL, "
            + "    track            TEXT COLLATE pg_catalog.\"POSIX\" NOT NULL, "
            + "    CONSTRAINT search_results_pkey PRIMARY KEY (provider, search_term) "
            + ");";


    @Override
    public void migrate(Context context) throws Exception {
        try (Statement statement = context.getConnection().createStatement()) {
            statement.execute(DROP);
            statement.execute(CREATE);
        }
    }
}
