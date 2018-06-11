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

import com.fredboat.backend.quarterdeck.db.entities.main.GuildPermissions;
import com.fredboat.backend.quarterdeck.db.repositories.api.GuildPermsRepo;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.GuildPermissionLevel;
import org.springframework.stereotype.Component;
import space.npstr.sqlsauce.DatabaseWrapper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.Optional;

/**
 * Created by napster on 05.02.18.
 */
@Component
public class SqlSauceGuildPermsRepo extends SqlSauceRepo<String, GuildPermissions> implements GuildPermsRepo {

    public SqlSauceGuildPermsRepo(DatabaseWrapper dbWrapper) {
        super(dbWrapper, GuildPermissions.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuildPermissions delete(String guildId, GuildPermissionLevel guildPermissionLevel, String id) {
        GuildPermissions guildPermissions = super.fetch(guildId);

        Function<GuildPermissions, GuildPermissions> update = guildPermission -> guildPermission;
        List<String> permissionList = guildPermissions.removePermission(id, guildPermissionLevel);
        update = update.andThen(guildPerm -> guildPerm.setFromEnum(guildPermissionLevel, permissionList));

        return this.getDatabaseWrapper().findApplyAndMerge(GuildPermissions.key(guildId), update);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuildPermissions put(String guildId, GuildPermissionLevel guildPermissionLevel, String id) {

        GuildPermissions guildPermissions = super.fetch(guildId);

        Function<GuildPermissions, GuildPermissions> update = guildPermission -> guildPermission;
        List<String> permissionList = guildPermissions.addPermission(id, guildPermissionLevel);
        update = update.andThen(guildPerm -> guildPerm.setFromEnum(guildPermissionLevel, permissionList));

        return this.getDatabaseWrapper().findApplyAndMerge(GuildPermissions.key(guildId), update);
    }

    @Override
    public GuildPermissions patch(String guildId, Map<String, Object> partialUpdate) {
        return super.patch(guildId, partialUpdate);
    }

    @Override
    public Optional<GuildPermissions> get(String guildId) {
        return Optional.ofNullable(this.dbWrapper.getEntity(GuildPermissions.key(guildId)));
    }
}
