package com.fredboat.backend.quarterdeck.rest.v1.transfer;

import fredboat.definitions.PermissionLevel;

public class GuildPermissionsUtil {

    /**
     * Convert guild permission levels into the entity object.
     *
     * @param guildPermissionLevel Guild permission transfer object.
     * @return PermissionLevel object if match or null if unmatched.
     */
    public static PermissionLevel transferPermissionResolve(GuildPermissionLevels guildPermissionLevel) {
        switch (guildPermissionLevel) {
            case DJ:
                return PermissionLevel.DJ;
            case USER:
                return PermissionLevel.USER;
            case Admin:
                return PermissionLevel.ADMIN;

            default:
                return null;
        }
    }
}
