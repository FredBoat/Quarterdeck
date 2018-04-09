package com.fredboat.backend.quarterdeck.rest.v1;

import com.fredboat.backend.quarterdeck.db.repositories.api.GuildPermsRepo;
import com.fredboat.backend.quarterdeck.exceptions.PermissionNotSupportedException;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.DiscordSnowflake;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.GuildPermissionLevels;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.GuildPermissions;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.GuildPermissionsUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @ApiImplicitParam(name = PERMISSION_PATH_VARIABLE, dataTypeClass = GuildPermissionLevels.class, example = "DJ",
                    required = true, paramType = "path", type = "enum", format = "Discord snowflake")
    })
    @DeleteMapping("{" + PERMISSION_PATH_VARIABLE + "}")
    public ResponseEntity<GuildPermissions> deleteGuildPermission(@PathVariable(GUILD_ID_PATH_VARIABLE) DiscordSnowflake guildId,
                                                                  @PathVariable(PERMISSION_PATH_VARIABLE) GuildPermissionLevels level) {
        try {
            GuildPermissions test2 = GuildPermissions.of(this.guildPermsRepo.delete(guildId.getSnowflakeId(), GuildPermissionsUtil.transferPermissionResolve(level)));
            ResponseEntity<GuildPermissions> result = new ResponseEntity<GuildPermissions>(test2, HttpStatus.OK);

            GuildPermissions test = GuildPermissions.of(this.guildPermsRepo.fetch(guildId.getSnowflakeId()));
            return result;
        } catch (PermissionNotSupportedException e) {
            return new ResponseEntity<GuildPermissions>(HttpStatus.BAD_REQUEST);
        }
    }
}
