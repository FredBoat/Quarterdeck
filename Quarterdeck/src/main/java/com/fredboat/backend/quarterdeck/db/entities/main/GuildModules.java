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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fredboat.backend.shared.Module;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import space.npstr.sqlsauce.entities.SaucedEntity;
import space.npstr.sqlsauce.fp.types.EntityKey;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by napster on 29.12.17.
 * <p>
 * There are many more concise ways to persist the module configuration of a guild, here is are reasons for the chosen design:
 * <p>
 * - An entity of it's own:
 * To keep the guild config small, and be more flexible. Maybe we want one of these things to be bot specific in the future?
 * Having different ehcache regions for both is a bonus.
 * <p>
 * - Each module in a column:
 * Alternatives like discord permission style bits in a single long value, or collections/lists of enabled permissions
 * lack option to switch between opt-in and opt-out patterns for modules, and having to do any changes or migrations to
 * those is more complicated. The possible performance/ressource usage improvement is probably not worth it.
 * <p>
 * <p>
 * - Nullables Columns
 * A null value signals us that a guild has not expressed any preference for a module in any way, or better the other way
 * round: Explicitly enabling/disabling a module tells us something about the preferences of our users. If we were to preset
 * these values, we would lose this information, and could not switch enabled/disabled modules by default reliably for existing guilds.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "guild_modules")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "guild_modules")
public class GuildModules extends SaucedEntity<Long, GuildModules> {

    @Id
    @Column(name = "guild_id")
    private long guildId;

    /**
     * Column corresponding to {@link Module#ADMIN}.
     */
    @Nullable
    @Column(name = "admin", nullable = true)
    private Boolean adminModule;

    /**
     * Column corresponding to {@link Module#INFO}.
     */
    @Nullable
    @Column(name = "info", nullable = true)
    private Boolean infoModule;

    /**
     * Column corresponding to {@link Module#CONFIG}.
     */
    @Nullable
    @Column(name = "config", nullable = true)
    private Boolean configModule;

    /**
     * Column corresponding to {@link Module#MUSIC}.
     */
    @Nullable
    @Column(name = "music", nullable = true)
    private Boolean musicModule;

    /**
     * Column corresponding to {@link Module#MOD}.
     */
    @Nullable
    @Column(name = "mod", nullable = true)
    private Boolean modModule;

    /**
     * Column corresponding to {@link Module#UTIL}.
     */
    @Nullable
    @Column(name = "util", nullable = true)
    private Boolean utilModule;

    /**
     * Column corresponding to {@link Module#FUN}.
     */
    @Nullable
    @Column(name = "fun", nullable = true)
    private Boolean funModule;


    //for jpa / database wrapper
    GuildModules() {
    }

    @Override
    public GuildModules setId(Long guildId) {
        this.guildId = guildId;
        return this;
    }

    public static EntityKey<Long, GuildModules> key(long guildId) {
        return EntityKey.of(guildId, GuildModules.class);
    }

    @Override
    public Long getId() {
        return this.guildId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.guildId);
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof GuildModules) && ((GuildModules) obj).guildId == this.guildId;
    }

    @CheckReturnValue
    public GuildModules enableModule(Module module) {
        return setModule(module, true);
    }

    @CheckReturnValue
    public GuildModules disableModule(Module module) {
        return setModule(module, false);
    }

    @CheckReturnValue
    public GuildModules resetModule(Module module) {
        return setModule(module, null);
    }

    @CheckReturnValue
    public GuildModules setModule(Module module, @Nullable Boolean enabled) {
        switch (module) {
            case ADMIN:
                this.adminModule = enabled;
                break;
            case INFO:
                this.infoModule = enabled;
                break;
            case CONFIG:
                this.configModule = enabled;
                break;
            case MUSIC:
                this.musicModule = enabled;
                break;
            case MOD:
                this.modModule = enabled;
                break;
            case UTIL:
                this.utilModule = enabled;
                break;
            case FUN:
                this.funModule = enabled;
                break;
            default:
                throw new IllegalArgumentException("Unknown Module " + module.name());
        }
        return this;
    }

    /**
     * @return true if the provided module is enabled, false if not, or null if no preference has been set.
     */
    public Optional<Boolean> isModuleEnabled(Module module) {
        Boolean result;
        switch (module) {
            case ADMIN:
                result = this.adminModule;
                break;
            case INFO:
                result = this.infoModule;
                break;
            case CONFIG:
                result = this.configModule;
                break;
            case MUSIC:
                result = this.musicModule;
                break;
            case MOD:
                result = this.modModule;
                break;
            case UTIL:
                result = this.utilModule;
                break;
            case FUN:
                result = this.funModule;
                break;
            default:
                throw new IllegalArgumentException("Unknown Module " + module.name());
        }

        return Optional.ofNullable(result);
    }

    /**
     * @return true if the provided module is enabled, false if not. If no value has been specified, return the provided
     * default value.
     */
    public boolean isModuleEnabled(Module module, boolean def) {
        return isModuleEnabled(module).orElse(def);
    }

    /**
     * @return true if the provided module is enabled, false if not. If no value has been specified, return the default
     * value of the module.
     */
    public boolean isModuleEnabledOrDefault(Module module) {
        return isModuleEnabled(module).orElse(module.isEnabledByDefault());
    }
}
