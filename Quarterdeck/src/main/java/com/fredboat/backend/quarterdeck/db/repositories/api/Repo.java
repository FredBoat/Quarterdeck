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

import java.util.Map;
import java.util.function.Function;

/**
 * Created by napster on 05.02.18.
 */
public interface Repo<I, E> {

    /**
     * @param id
     *         of the entity that shall be deleted
     */
    void delete(I id);

    /**
     * @param id
     *         id of the entity that shall be returned
     *
     * @return the entity of the provided id, if such an entity exists in the database, a default entity otherwise.
     * Expect this to return a entity constructed through its default constructor, with setId(id) called on it.
     */
    E fetch(I id);

    /**
     * @param entity
     *         entity to be merged into the database
     *
     * @return the merged entity
     */
    E merge(E entity);


    /**
     * Patch an entity. A patch allows for partial updates of an entity, the caller is not required to send the full
     * entity if they want to change a subset of its values.
     *
     * @param id
     *         id of the entitiy that shall be patched
     * @param partialUpdate
     *         a map of the values that shall be patched. caution when processing them is required
     *
     * @return the full entity with the patch applied
     */
    E patch(I id, Map<String, Object> partialUpdate);

    /**
     * A generic way to change an entity. This method will fetch an existing entity or create a default one, apply it
     * on the provided transformation, and merge the result of that into the database.
     * <p>
     * Prefer to use one of the other specialized methods of this class over this one. Using this method makes it hard
     * to reason about the exact type of transformation happening and pollutes the caller with Entity details.
     *
     * @return the full entity with the transformation applied
     */
    E transform(I id, Function<E, E> transformation);
}
