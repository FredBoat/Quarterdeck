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

package com.fredboat.backend.quarterdeck.db.entities.main;

import fredboat.definitions.PermissionLevel;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import space.npstr.sqlsauce.entities.SaucedEntity;
import space.npstr.sqlsauce.fp.types.EntityKey;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "guild_permissions")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "guild_permissions")
public class GuildPermissions extends SaucedEntity<String, GuildPermissions> {

    public static final String ADMIN_LIST_COLUMN = "list_admin";
    public static final String DJ_LIST_COLUMN = "list_dj";
    public static final String USER_LIST_COLUMN = "list_user";

    // Guild ID
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = ADMIN_LIST_COLUMN, nullable = false, columnDefinition = "text")
    private String adminList = "";

    @Column(name = DJ_LIST_COLUMN, nullable = false, columnDefinition = "text")
    private String djList = "";

    @Column(name = USER_LIST_COLUMN, nullable = false, columnDefinition = "text")
    private String userList = "";

    //for jpa / db wrapper
    GuildPermissions() {
    }

    public static EntityKey<String, GuildPermissions> key(String guildId) {
        return EntityKey.of(guildId, GuildPermissions.class);
    }

    @Override
    public GuildPermissions setId(String id) {
        this.id = id;

        //Set up default permissions. Note that the @everyone role of a guild is of the same snowflake as the guild
        // This code works because setId() is only ever called when creating a new instance of this entity after failing
        // not finding it in the database, and never else. There is no need to ever set the id on this object outside of
        // that case.
        this.djList = id;
        this.userList = id;

        return this;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public List<String> getAdminList() {
        return Arrays.asList(this.adminList.split(" "));
    }

    public GuildPermissions setAdminList(List<String> list) {
        StringBuilder str = new StringBuilder();
        for (String item : list) {
            str.append(item).append(" ");
        }

        this.adminList = str.toString().trim();
        return this;
    }

    public List<String> getDjList() {
        return Arrays.asList(this.djList.split(" "));
    }

    public GuildPermissions setDjList(List<String> list) {
        StringBuilder str = new StringBuilder();
        for (String item : list) {
            str.append(item).append(" ");
        }

        this.djList = str.toString().trim();
        return this;
    }

    public List<String> getUserList() {
        return Arrays.asList(this.userList.split(" "));
    }

    public GuildPermissions setUserList(List<String> list) {
        StringBuilder str = new StringBuilder();
        for (String item : list) {
            str.append(item).append(" ");
        }

        this.userList = str.toString().trim();
        return this;
    }

    public List<String> getFromEnum(PermissionLevel level) {
        switch (level) {
            case ADMIN:
                return getAdminList();
            case DJ:
                return getDjList();
            case USER:
                return getUserList();
            default:
                throw new IllegalArgumentException("Unexpected enum " + level);
        }
    }

    public GuildPermissions setFromEnum(PermissionLevel level, List<String> list) {
        switch (level) {
            case ADMIN:
                return setAdminList(list);
            case DJ:
                return setDjList(list);
            case USER:
                return setUserList(list);
            default:
                throw new IllegalArgumentException("Unexpected enum " + level);
        }
    }

}
