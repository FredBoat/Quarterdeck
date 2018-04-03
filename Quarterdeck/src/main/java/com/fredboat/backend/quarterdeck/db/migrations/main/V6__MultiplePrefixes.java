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

package com.fredboat.backend.quarterdeck.db.migrations.main;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Created by napster on 03.04.18.
 */
public class V6__MultiplePrefixes implements JdbcMigration {

    //current:
    // table: public.prefixes
    // column: prefix TEXT
    // can be null, empty string, or a string

    //target:
    // table: public.prefixes
    // column: pvalues TEXT[]
    // NOT NULL, entries should not be null, the array can be empty


    // nulls are those rows where a prefix was reset via ;;prefix reset (or similar). we can safely drop those
    // so we dont have to worry about accidently adding null values to the array in the MIGRATE step
    private static final String DELETE_NULLS
            = "DELETE FROM public.prefixes WHERE prefixes.prefix IS NULL;";
    private static final String ADD_COLUMN
            = "ALTER TABLE public.prefixes ADD COLUMN pvalues TEXT[] COLLATE pg_catalog.\"default\" NOT NULL DEFAULT '{}';";
    private static final String MIGRATE
            = "UPDATE public.prefixes SET pvalues = pvalues || prefixes.prefix;";

    //todo write migration to delete the old row after this one is successful

    @Override
    public void migrate(Connection connection) throws Exception {

        try (Statement statement = connection.createStatement()) {
            statement.execute(DELETE_NULLS);
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute(ADD_COLUMN);
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute(MIGRATE);
        }

    }
}
