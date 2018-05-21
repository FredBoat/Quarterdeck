package com.fredboat.backend.quarterdeck.rest.v1.transfer;

import java.beans.PropertyEditorSupport;

/***
 * Class to handle lower case input from rest parameter to upper case convert to enums.
 */
public class GuildPermissionLevelsEnumConverter extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {

        String upperCase = text.toUpperCase();
        GuildPermissionLevels levels = GuildPermissionLevels.valueOf(upperCase);
        setValue(levels);
    }
}
