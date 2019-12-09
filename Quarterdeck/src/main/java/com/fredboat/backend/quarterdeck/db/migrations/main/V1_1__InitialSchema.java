package com.fredboat.backend.quarterdeck.db.migrations.main;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.Statement;

public class V1_1__InitialSchema extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();

        //UConfig
        //drop UConfig if exists; it was never used anyways, and can be readded later if we actually use it
        //language=PostgreSQL
        String dropUConfigSql = "DROP TABLE IF EXISTS public.user_config;";
        try (Statement dropUConfig = connection.createStatement()) {
            dropUConfig.execute(dropUConfigSql);
        }

        //BlacklistEntry
        //language=PostgreSQL
        String createBlacklistSql
                = "CREATE TABLE IF NOT EXISTS public.blacklist "
                + "( "
                + "    id                    BIGINT NOT NULL, "
                + "    level                 INTEGER NOT NULL, "
                + "    rate_limit_reached    INTEGER NOT NULL, "
                + "    rate_limit_timestamp  BIGINT NOT NULL, "
                + "    blacklisted_timestamp BIGINT NOT NULL, "
                + "    CONSTRAINT blacklist_pkey PRIMARY KEY (id) "
                + ");";
        try (Statement createBlacklist = connection.createStatement()) {
            createBlacklist.execute(createBlacklistSql);
        }

        //GuildConfig
        //language=PostgreSQL
        String createGuildConfigSql
                = "CREATE TABLE IF NOT EXISTS public.guild_config "
                + "( "
                + "    guildid        CHARACTER VARYING(255) COLLATE pg_catalog.\"default\" NOT NULL, "
                + "    track_announce BOOLEAN NOT NULL, "
                + "    auto_resume    BOOLEAN NOT NULL, "
                + "    lang           CHARACTER VARYING(255) COLLATE pg_catalog.\"default\" NOT NULL, "
                + "    enable_unknown_command BOOLEAN NOT NULL, "
                + "    prefix         TEXT COLLATE pg_catalog.\"default\", "
                + "    CONSTRAINT guild_config_pkey PRIMARY KEY(guildid) "
                + ");";
        try (Statement createGuildConfig = connection.createStatement()) {
            createGuildConfig.execute(createGuildConfigSql);
        }

        //GuildPermissions
        //language=PostgreSQL
        String createGuildPermissionsSql
                = "CREATE TABLE IF NOT EXISTS public.guild_permissions "
                + "( "
                + "    id         CHARACTER VARYING(255) COLLATE pg_catalog.\"default\" NOT NULL, "
                + "    list_admin TEXT COLLATE pg_catalog.\"default\" NOT NULL, "
                + "    list_dj    TEXT COLLATE pg_catalog.\"default\" NOT NULL, "
                + "    list_user  TEXT COLLATE pg_catalog.\"default\" NOT NULL, "
                + "    CONSTRAINT guild_permissions_pkey PRIMARY KEY (id) "
                + ");";
        try (Statement createGuildPermissions = connection.createStatement()) {
            createGuildPermissions.execute(createGuildPermissionsSql);
        }

        //HStorex (from sqlsauce, requires hstore extension to be enabled)
        //language=PostgreSQL
        String createHstorexSql
                = "CREATE TABLE IF NOT EXISTS public.hstorex "
                + "( "
                + "    name    TEXT COLLATE pg_catalog.\"default\" NOT NULL, "
                + "    hstorex HSTORE, "
                + "    CONSTRAINT hstorex_pkey PRIMARY KEY (name) "
                + ")";
        try (Statement createHstorex = connection.createStatement()) {
            createHstorex.execute(createHstorexSql);
        }
    }
}
