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
import com.fredboat.backend.shared.GuildPermissionLevel;
import org.springframework.stereotype.Component;
import space.npstr.sqlsauce.DatabaseWrapper;
import space.npstr.sqlsauce.entities.SaucedEntity;
import space.npstr.sqlsauce.fp.types.EntityKey;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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
        EntityKey<String, GuildPermissions> key = EntityKey.of(guildId, GuildPermissions.class);
        synchronized (SaucedEntity.getEntityLock(key)) {
            EntityManager entityManager = this.getDatabaseWrapper().getEntityManagerFactory().createEntityManager();
            try {
                EntityTransaction transaction = entityManager.getTransaction();
                try {
                    transaction.begin();
                    GuildPermissions guildPermissions = entityManager.find(GuildPermissions.class, guildId);
                    if (guildPermissions == null) {
                        guildPermissions = new GuildPermissions(guildId);
                    }
                    guildPermissions = guildPermissions.removeIdFromLevel(id, guildPermissionLevel);

                    guildPermissions = entityManager.merge(guildPermissions);
                    transaction.commit();
                    return guildPermissions;
                } finally {
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }
                }
            } finally {
                entityManager.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuildPermissions put(String guildId, GuildPermissionLevel guildPermissionLevel, String id) {
        EntityKey<String, GuildPermissions> key = EntityKey.of(guildId, GuildPermissions.class);
        synchronized (SaucedEntity.getEntityLock(key)) {
            EntityManager entityManager = this.getDatabaseWrapper().getEntityManagerFactory().createEntityManager();
            try {
                EntityTransaction transaction = entityManager.getTransaction();
                try {
                    transaction.begin();
                    GuildPermissions guildPermissions = entityManager.find(GuildPermissions.class, guildId);
                    if (guildPermissions == null) {
                        guildPermissions = new GuildPermissions(guildId);
                    }

                    guildPermissions = guildPermissions.addIdToLevel(id, guildPermissionLevel);
                    guildPermissions = entityManager.merge(guildPermissions);
                    transaction.commit();
                    return guildPermissions;
                } finally {
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }
                }
            } finally {
                entityManager.close();
            }
        }
    }

    @Override
    public GuildPermissions fetch(String id) {
        return Optional.ofNullable(this.dbWrapper.getEntity(GuildPermissions.key(id)))
                .orElse(new GuildPermissions(id));
    }
}
