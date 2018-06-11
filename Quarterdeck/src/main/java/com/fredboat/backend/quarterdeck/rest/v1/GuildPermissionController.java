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

package com.fredboat.backend.quarterdeck.rest.v1;

import com.fredboat.backend.quarterdeck.db.repositories.api.GuildPermsRepo;
import com.fredboat.backend.quarterdeck.exceptions.PermissionNotSupportedException;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/" + EntityController.VERSION_PATH
        + "guilds/{" + GuildPermissionController.GUILD_ID_PATH_VARIABLE + "}/permissions")
public class GuildPermissionController {
    /**
     * Guild id path variable.
     */
    static final String GUILD_ID_PATH_VARIABLE = "guild_id";
    static final String PERMISSION_PATH_VARIABLE = "permsLevel";
    static final String USER_ID_PATH_VARIABLE = "user_id";

    private final GuildPermsRepo guildPermsRepo;

    public GuildPermissionController(GuildPermsRepo guildPermsRepo) {
        this.guildPermsRepo = guildPermsRepo;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = GUILD_ID_PATH_VARIABLE, dataTypeClass = DiscordSnowflake.class, example = "174820236481134592",
                    required = true, paramType = "path", type = "string", format = "Discord snowflake",
                    value = "Discord snowflake")
    })
    @GetMapping
    public GuildPermissions getPermissions(@PathVariable(GUILD_ID_PATH_VARIABLE) DiscordSnowflake guildId) {
        return GuildPermissions.of(this.guildPermsRepo.fetch(guildId.getSnowflakeId()));
    }

    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = GUILD_ID_PATH_VARIABLE, dataTypeClass = DiscordSnowflake.class, example = "174820236481134592",
                    required = true, paramType = "path", type = "string", format = "Discord snowflake",
                    value = "Discord snowflake"),
            @ApiImplicitParam(name = PERMISSION_PATH_VARIABLE, dataTypeClass = GuildPermissionLevels.class, example = "dj",
                    required = true, paramType = "path", type = "enum", format = "Discord snowflake"),
            @ApiImplicitParam(name = USER_ID_PATH_VARIABLE, dataTypeClass = DiscordSnowflake.class, example = "174820236481134599",
                    required = true, paramType = "path", type = "string", format = "Discord snowflake",
                    value = "Discord snowflake"),
    })
    @PutMapping("{" + PERMISSION_PATH_VARIABLE + "}/{" + USER_ID_PATH_VARIABLE + "}")
    public ResponseEntity<GuildPermissions> putPermission(@PathVariable(GUILD_ID_PATH_VARIABLE) DiscordSnowflake guildId,
                                                          @PathVariable(PERMISSION_PATH_VARIABLE) GuildPermissionLevels level,
                                                          @PathVariable(USER_ID_PATH_VARIABLE) DiscordSnowflake userId) {
        try {
            ResponseEntity<GuildPermissions> result = new ResponseEntity<>(
                    GuildPermissions.of(this.guildPermsRepo.put(guildId.getSnowflakeId(),
                            GuildPermissionsUtil.transferPermissionResolve(level), userId.getSnowflakeId())), HttpStatus.OK);

            return result;
        } catch (PermissionNotSupportedException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = GUILD_ID_PATH_VARIABLE, dataTypeClass = DiscordSnowflake.class, example = "174820236481134592",
                    required = true, paramType = "path", type = "string", format = "Discord snowflake",
                    value = "Discord snowflake"),
            @ApiImplicitParam(name = PERMISSION_PATH_VARIABLE, dataTypeClass = GuildPermissionLevels.class, example = "dj",
                    required = true, paramType = "path", type = "enum", format = "Discord snowflake"),
            @ApiImplicitParam(name = USER_ID_PATH_VARIABLE, dataTypeClass = DiscordSnowflake.class, example = "174820236481134599",
                    required = true, paramType = "path", type = "string", format = "Discord snowflake")
    })
    @DeleteMapping("{" + PERMISSION_PATH_VARIABLE + "}/{" + USER_ID_PATH_VARIABLE + "}")
    public ResponseEntity<GuildPermissions> deleteGuildPermission(@PathVariable(GUILD_ID_PATH_VARIABLE) DiscordSnowflake guildId,
                                                                  @PathVariable(PERMISSION_PATH_VARIABLE) GuildPermissionLevels level,
                                                                  @PathVariable(USER_ID_PATH_VARIABLE) DiscordSnowflake userId) {
        try {
            ResponseEntity<GuildPermissions> result = new ResponseEntity<>(
                    GuildPermissions.of(this.guildPermsRepo.delete(guildId.getSnowflakeId(),
                            GuildPermissionsUtil.transferPermissionResolve(level), userId.getSnowflakeId())), HttpStatus.OK);
            return result;
        } catch (PermissionNotSupportedException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(GuildPermissionLevels.class, new GuildPermissionLevelsEnumConverter());
    }
}
