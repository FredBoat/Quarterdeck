package com.fredboat.backend.quarterdeck.rest.v1.transfer;

import java.util.Arrays;

public enum GuildPermissionLevels {

    Admin("admin"),
    DJ("dj"),
    USER("user");

    private final String name;

    private GuildPermissionLevels(String name) {
        this.name = name.toLowerCase();
    }

    public String toString() {
        return this.name;
    }

    public static GuildPermissionLevels fromValue(String value) {
        for (GuildPermissionLevels level : values()) {
            if (level.name.equalsIgnoreCase(value)) {
                return level;
            }
        }
        throw new IllegalArgumentException(
                "Unknown enum type " + value + ", Allowed values are " + Arrays.toString(values()));
    }

}
