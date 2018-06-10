package com.fredboat.backend.quarterdeck.rest.v1.transfer;


import io.swagger.annotations.ApiModelProperty;

public class GuildPermissions {

    private final DiscordSnowflake guildId;
    private final String adminIds;
    private final String djIds;
    private final String userIds;

    public static GuildPermissions of(com.fredboat.backend.quarterdeck.db.entities.main.GuildPermissions guildPermissions) {
        return new GuildPermissions(guildPermissions);
    }

    private GuildPermissions(com.fredboat.backend.quarterdeck.db.entities.main.GuildPermissions guildPermissions) {
        this.guildId = new DiscordSnowflake(guildPermissions.getId());
        this.adminIds = guildPermissions.getAdminList();
        this.djIds = guildPermissions.getDjList();
        this.userIds = guildPermissions.getUserList();
    }

    // the getters are picked up by springfox for the documentation
    @ApiModelProperty(position = -1)
    public DiscordSnowflake getGuildId() {
        return guildId;
    }

    public String getAdminIds() {
        return this.adminIds;
    }

    public String getDjIds() {
        return this.djIds;
    }

    public String getUserIds() {
        return this.userIds;
    }
}
