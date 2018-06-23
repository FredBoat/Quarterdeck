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

package com.fredboat.backend.quarterdeck.rest.v0;

import com.fredboat.backend.quarterdeck.db.entities.main.GuildPermissions;
import com.fredboat.backend.quarterdeck.db.repositories.api.GuildPermsRepo;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.DiscordSnowflake;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by napster on 17.02.18.
 */
@RestController
@RequestMapping("/" + EntityController.VERSION_PATH + "guildperms/")
public class GuildPermsController extends EntityController<String, GuildPermissions> {

    private final GuildPermsRepo guildPermsRepo;

    public GuildPermsController(GuildPermsRepo repo) {
        super(repo);
        this.guildPermsRepo = repo;
    }

    @Override
    @PostMapping("/fetch")
    public GuildPermissions fetch(@RequestBody String id) {
        String sanitizedId = id.replaceAll("\"", ""); //jackson plz
        return this.guildPermsRepo.get(sanitizedId);
    }

    @Override
    @PostMapping("/delete")
    public void delete(@RequestBody String id) {
        String sanitizedId = id.replaceAll("\"", ""); //jackson plz
        super.delete(new DiscordSnowflake(sanitizedId).getSnowflakeId());
    }
}
