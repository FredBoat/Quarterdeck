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

import com.fredboat.backend.quarterdeck.rest.v1.transfer.DiscordSnowflake;
import com.fredboat.backend.shared.GuildPermissionLevel;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "guild_permissions")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "guild_permissions")
public class GuildPermissions extends SaucedEntity<String, GuildPermissions> {
    
    private static final String UNEXPECTED_ENUM_ERROR = "Unexpected enum ";

    // Guild ID
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "list_admin", nullable = false, columnDefinition = "text")
    private String adminList = "";

    @Column(name = "list_dj", nullable = false, columnDefinition = "text")
    private String djList = "";

    @Column(name = "list_user", nullable = false, columnDefinition = "text")
    private String userList = "";

    //for jpa / db wrapper
    GuildPermissions() {
    }

    public GuildPermissions(String id) {
        String sanitizedId = setId(id).getId();
        //init other default values
        this.djList = sanitizedId;
        this.userList = sanitizedId;
    }

    @Override
    public GuildPermissions setId(String id) {
        //soft check
        DiscordSnowflake snowflake = new DiscordSnowflake(id.replaceAll("\"", "")); //jackson plz
        this.id = snowflake.getSnowflakeId();
        return this;
    }

    public static EntityKey<String, GuildPermissions> key(String id) {
        DiscordSnowflake snowflake = new DiscordSnowflake(id.replaceAll("\"", "")); //jackson plz
        return EntityKey.of(snowflake.getSnowflakeId(), GuildPermissions.class);
    }

    @Override
    public String getId() {
        return this.id;
    }

    public List<String> splitAdminList() {
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

    public List<String> splitDjList() {
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

    public List<String> splitUserList() {
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

    public List<String> getFromEnum(GuildPermissionLevel level) {
        switch (level) {
            case ADMIN:
                return splitAdminList();
            case DJ:
                return splitDjList();
            case USER:
                return splitUserList();
            default:
                throw new IllegalArgumentException(UNEXPECTED_ENUM_ERROR + level);
        }
    }

    public GuildPermissions setFromEnum(GuildPermissionLevel level, List<String> list) {
        switch (level) {
            case ADMIN:
                return setAdminList(list);
            case DJ:
                return setDjList(list);
            case USER:
                return setUserList(list);
            default:
                throw new IllegalArgumentException(UNEXPECTED_ENUM_ERROR + level);
        }
    }

    public GuildPermissions addIdToLevel(String id, GuildPermissionLevel level) {
        List<String> list = getPermissionListFromEnum(level);

        if (!list.contains(id)) {
            list.add(id);
        }
        this.setFromEnum(level, list);
        return this;
    }

    public GuildPermissions removeIdFromLevel(String id, GuildPermissionLevel level) {
        List<String> permissions = getPermissionListFromEnum(level);
        List<String> ids = Stream.of(permissions)
                .flatMap(List::stream)
                .filter(listId -> !listId.equals(id))
                .collect(Collectors.toList());
        this.setFromEnum(level, ids);

        return this;
    }
    //the boilerplate below is for v0 jackson


    public String getAdminList() {
        return this.adminList;
    }

    public String getDjList() {
        return this.djList;
    }

    public String getUserList() {
        return this.userList;
    }

    public void setAdminList(String adminList) {
        this.adminList = adminList;
    }

    public void setDjList(String djList) {
        this.djList = djList;
    }

    public void setUserList(String userList) {
        this.userList = userList;
    }

    private List<String> getPermissionListFromEnum(GuildPermissionLevel level) {
        switch (level) {
            case ADMIN:
                return Arrays.stream(adminList.split(" ")).collect(Collectors.toList());

            case DJ:
                return Arrays.stream(djList.split(" ")).collect(Collectors.toList());

            case USER:
                return Arrays.stream(userList.split(" ")).collect(Collectors.toList());

            default:
                throw new IllegalArgumentException(UNEXPECTED_ENUM_ERROR + level);
        }
    }
}
