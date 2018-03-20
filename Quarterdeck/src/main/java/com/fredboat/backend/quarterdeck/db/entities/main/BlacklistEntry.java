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

import space.npstr.sqlsauce.entities.SaucedEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by napster on 26.04.17.
 * <p>
 * closely related to the stuff in fredboat.util.ratelimit
 */
@Entity
@Table(name = "blacklist")
public class BlacklistEntry extends SaucedEntity<Long, BlacklistEntry> {

    //id of the user or guild that this blacklist entry belongs to
    @Id
    @Column(name = "id", nullable = false)
    private long id;

    //blacklist level that the user or guild is on
    //this should increase every time progressively
    @Column(name = "level", nullable = false)
    private int level = -1;

    //keeps track of how many times a user or guild reached the rate limit on the current blacklist level
    @Column(name = "rate_limit_reached", nullable = false)
    private int rateLimitReached;

    //when was the ratelimit hit the last time?
    @Column(name = "rate_limit_timestamp", nullable = false)
    private long rateLimitReachedTimestamp;

    //time when the id was blacklisted
    @Column(name = "blacklisted_timestamp", nullable = false)
    private long blacklistedTimestamp;

    @Override
    public BlacklistEntry setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.id);
    }

    //Boilerplate code below

    //for jpa / db wrapper
    BlacklistEntry() {
    }

    public int getLevel() {
        return this.level;
    }

    public void incLevel() {
        this.level++;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRateLimitReached() {
        return this.rateLimitReached;
    }

    public void incRateLimitReached() {
        this.rateLimitReached++;
    }

    public void setRateLimitReached(int rateLimitReached) {
        this.rateLimitReached = rateLimitReached;
    }

    public long getRateLimitReachedTimestamp() {
        return this.rateLimitReachedTimestamp;
    }

    public void setRateLimitReachedTimestamp(long rateLimitReachedTimestamp) {
        this.rateLimitReachedTimestamp = rateLimitReachedTimestamp;
    }

    public long getBlacklistedTimestamp() {
        return this.blacklistedTimestamp;
    }

    public void setBlacklistedTimestamp(long blacklistedTimestamp) {
        this.blacklistedTimestamp = blacklistedTimestamp;
    }
}
