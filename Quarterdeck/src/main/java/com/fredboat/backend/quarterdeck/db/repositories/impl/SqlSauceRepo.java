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

import com.fredboat.backend.quarterdeck.db.repositories.api.RepoAdapter;
import space.npstr.sqlsauce.DatabaseWrapper;
import space.npstr.sqlsauce.entities.SaucedEntity;
import space.npstr.sqlsauce.fp.types.EntityKey;
import space.npstr.sqlsauce.fp.types.Transfiguration;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Created by napster on 05.02.18.
 */
public abstract class SqlSauceRepo<I extends Serializable, E extends SaucedEntity<I, E>> extends RepoAdapter<I, E> {

    protected final DatabaseWrapper dbWrapper;
    protected final Class<E> entityClass;

    public SqlSauceRepo(DatabaseWrapper dbWrapper, Class<E> entityClass) {
        this.dbWrapper = dbWrapper;
        this.entityClass = entityClass;
    }

    public DatabaseWrapper getDatabaseWrapper() {
        return this.dbWrapper;
    }

    public Class<E> getEntityClass() {
        return this.entityClass;
    }

    @Override
    public void delete(I id) {
        this.dbWrapper.deleteEntity(EntityKey.of(id, this.entityClass));
    }

    @Override
    public E fetch(I id) {
        return this.dbWrapper.getOrCreate(EntityKey.of(id, this.entityClass));
    }

    @Override
    public E merge(E entity) {
        return this.dbWrapper.merge(entity);
    }

    @Override
    public E transform(I id, Function<E, E> transformation) {
        return this.dbWrapper.findApplyAndMerge(Transfiguration.of(EntityKey.of(id, this.entityClass), transformation));
    }
}
