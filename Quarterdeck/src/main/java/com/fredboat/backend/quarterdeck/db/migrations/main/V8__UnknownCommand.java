package com.fredboat.backend.quarterdeck.db.migrations.main;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

public class V8__UnknownCommand extends BaseJavaMigration {

    //current:
    // table: public.guild_config
    // column: guildid CHARACTER, track_announce BOOLEAN, auto_resume
    // can be null, empty string, or a string

    //target:
    // table: public.prefixes
    // column: pvalues TEXT[]
    // NOT NULL, entries should not be null, the array can be empty

    //language=PostgreSQL
    private static final String ADD_COLUMN
            = "ALTER TABLE public.guild_config ADD COLUMN IF NOT EXISTS enable_unknown_command BOOLEAN NOT NULL DEFAULT(FALSE);";

    @Override
    public void migrate(Context context) throws Exception {
        try (Statement statement = context.getConnection().createStatement()) {
            statement.execute(ADD_COLUMN);
        }
    }
}
