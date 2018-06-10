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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fredboat.backend.quarterdeck.db.entities.main.GuildBotId;
import com.fredboat.backend.quarterdeck.db.entities.main.Prefix;
import com.fredboat.backend.quarterdeck.db.repositories.api.PrefixRepo;
import com.fredboat.backend.quarterdeck.rest.v0.transfer.PrefixTransfer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;

/**
 * Created by napster on 17.02.18.
 *
 * @deprecated move to v1 asap pl0x
 */
@Deprecated
@RestController
@RequestMapping("/" + EntityController.VERSION_PATH + "prefix/")
public class V0PrefixController {

    protected final PrefixRepo repo;
    private final ObjectMapper mapper;

    public V0PrefixController(PrefixRepo repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Nullable
    @PostMapping("/getraw")
    public String getPrefix(@RequestBody GuildBotId id) throws JsonProcessingException {
        return this.mapper.writeValueAsString(this.repo.fetch(id).getPrefix());
    }

    @PostMapping("/merge")
    public PrefixTransfer merge(@RequestBody PrefixTransfer transfer) {
        return PrefixTransfer.of(this.repo.merge(Prefix.fromTransfer(transfer)));
    }

    @PostMapping("/delete")
    public void delete(@RequestBody GuildBotId id) {
        this.repo.delete(id);
    }

    @PostMapping("/fetch")
    public PrefixTransfer fetch(@RequestBody GuildBotId id) {
        return PrefixTransfer.of(this.repo.fetch(id));
    }
}
