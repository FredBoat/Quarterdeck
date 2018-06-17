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

import com.fredboat.backend.quarterdeck.rest.v0.transfer.PrefixTransfer;
import org.hibernate.annotations.Type;
import space.npstr.sqlsauce.entities.SaucedEntity;
import space.npstr.sqlsauce.fp.types.EntityKey;
import space.npstr.sqlsauce.hibernate.types.BasicType;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by napster on 22.12.17.
 * <p>
 * The caching of this entity is not managed by ehcache, instead a guava cache is used on the client side.
 */
@Entity
@Table(name = "prefixes")
public class Prefix extends SaucedEntity<GuildBotId, Prefix> {

    public static final Set<String> DEFAULT_PREFIXES = Set.of(";;", "!");

    @SuppressWarnings("NullableProblems")
    @EmbeddedId
    private GuildBotId id;


    @Type(type = "hash-set-basic")
    @BasicType(String.class)
    @Column(name = "pvalues") //values is a semi-reserved keyword in postgres
    //internally always access this field through the getValues() to ensure it is properly populated
    private HashSet<String> values = new HashSet<>();

    //for jpa & the database wrapper
    Prefix() {
    }

    public static EntityKey<GuildBotId, Prefix> key(GuildBotId id) {
        return EntityKey.of(id, Prefix.class);
    }

    @Override
    public Prefix setId(GuildBotId id) {
        this.id = id;
        return this;
    }

    @Override
    public GuildBotId getId() {
        return this.id;
    }

    @Override
    public Class<Prefix> getClazz() {
        return Prefix.class;
    }

    //reason: no prefixes are a bad idea. it is also not a great idea to hard save the defaults for every guild
    private Set<String> getValues() {
        if (this.values.isEmpty()) {
            return DEFAULT_PREFIXES;
        }
        return this.values;
    }

    /**
     * @deprecated switch to v1 asap pl0x, this method
     */
    @Deprecated
    @Nullable
    public String getPrefix() {
        if (this.values.isEmpty()
                || (this.values.size() == 2 && this.values.contains(";;") && this.values.contains("!"))) {
            return null; //the client is expected to pick their default one
        } else {
            //after the migration, if there was a custom prefix in the old format, it will be the only one in the set
            return this.values.iterator().next();
        }
    }

    @Deprecated
    public static Prefix fromTransfer(PrefixTransfer transfer) {
        Prefix result = new Prefix().setId(transfer.getId());
        result.values = new HashSet<>();
        String newPrefix = transfer.getPrefix();
        if (newPrefix != null) {
            result.values.add(newPrefix);
        }

        return result;
    }

    public Set<String> getPrefixes() {
        return Collections.unmodifiableSet(getValues());
    }

    @CheckReturnValue
    public Prefix addPrefixes(Collection<String> prefixes) {
        HashSet<String> valuesCopy = new HashSet<>(getValues());
        valuesCopy.addAll(prefixes);
        this.values = valuesCopy;
        return this;
    }

    @CheckReturnValue
    public Prefix removePrefixes(Collection<String> prefixes) {
        HashSet<String> valuesCopy = new HashSet<>(this.getValues());
        valuesCopy.removeAll(prefixes);
        this.values = valuesCopy;
        return this;
    }
}
