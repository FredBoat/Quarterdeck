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

import fredboat.definitions.RepeatMode;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Created by napster on 28.03.18.
 */
public class V5__CreateGuildPlayer implements JdbcMigration {

    //language=PostgreSQL
    private static final String DROP_TYPE_REPEAT_MODE
            = "DROP TYPE IF EXISTS RepeatMode;";
    /**
     * See {@link RepeatMode} for the Java enum of this
     */
    //language=PostgreSQL
    private static final String CREATE_TYPE_REPEAT_MODE
            = "CREATE TYPE RepeatMode AS ENUM "
            + "( "
            + "    'OFF', "
            + "    'SINGLE', "
            + "    'ALL' "
            + ");";

    //language=PostgreSQL
    private static final String DROP_TABLE_GUILD_PLAYERS
            = "DROP TABLE IF EXISTS public.guild_players;";

    //language=PostgreSQL
    private static final String CREATE_TABLE_GUILD_PLAYERS
            = "CREATE TABLE public.guild_players "
            + "( "
            + "    guild_id                 BIGINT NOT NULL, "
            + "    voice_channel_id         BIGINT NOT NULL, "
            + "    active_text_channel_id   BIGINT NOT NULL, "
            + "    is_paused                BOOLEAN NOT NULL, "
            + "    volume                   INTEGER NOT NULL, "
            + "    repeat_mode              RepeatMode NOT NULL, "
            + "    is_shuffled              BOOLEAN NOT NULL, "
            + "    CONSTRAINT guild_players_pkey PRIMARY KEY (guild_id) "
            + ");";

    @Override
    public void migrate(Connection connection) throws Exception {
        try (Statement drop = connection.createStatement()) {
            drop.execute(DROP_TYPE_REPEAT_MODE);
        }
        try (Statement create = connection.createStatement()) {
            create.execute(CREATE_TYPE_REPEAT_MODE);
        }

        try (Statement drop = connection.createStatement()) {
            drop.execute(DROP_TABLE_GUILD_PLAYERS);
        }
        try (Statement create = connection.createStatement()) {
            create.execute(CREATE_TABLE_GUILD_PLAYERS);
        }
    }
}
