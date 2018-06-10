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

import com.fredboat.backend.quarterdeck.db.entities.main.GuildPermissions;
import com.fredboat.backend.quarterdeck.exceptions.PermissionNotSupportedException;
import fredboat.definitions.PermissionLevel;

import java.util.Optional;

/**
 * Created by napster on 05.02.18.
 */
public interface GuildPermsRepo extends Repo<String, GuildPermissions> {
    /**
     * Delete permission based on id.
     *
     * @param id              Id to delete the permission.
     * @param permissionLevel Permission level to delete from.
     * @return Updated guild permission.
     * @throws PermissionNotSupportedException If passed a permission level not supported by this function.
     */
    GuildPermissions delete(String id, PermissionLevel permissionLevel) throws PermissionNotSupportedException;

    /***
     * Update permission based on id and permission level
     *
     * @param id              Id to update the permission.
     * @param permissionLevel Permission level to update.
     * @return Updated guild permission.
     * @throws PermissionNotSupportedException If passed a permission level not supported by this function.
     */
    GuildPermissions put(String id, PermissionLevel permissionLevel) throws PermissionNotSupportedException;

    Optional<GuildPermissions> get(String id);
}
