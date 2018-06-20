package com.fredboat.backend.quarterdeck.rest.v1;

import com.fredboat.backend.quarterdeck.BaseTest;
import com.fredboat.backend.quarterdeck.rest.v1.transfer.DiscordSnowflake;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GuildPermissionsControllerTest extends BaseTest {

    private static final String GET_URL_TEMPLATE = "/v1/guilds/{guild_id}/permissions";
    private static final String PUT_URL_TEMPLATE = "/v1/guilds/{guild_id}/permissions/{permission_level}/{id}";

    private static final String ADMIN_PERMISSION_LEVEL = "admin";
    private static final String DJ_PERMISSION_LEVEL = "dj";
    private static final String USER_PERMISSION_LEVEL = "user";

    @WithMockUser(roles = "ADMIN")
    @Test
    public void ifwRequestNewGuildIdThenReturnEmptyGuildPermission() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        this.mockMvc.perform(get(GET_URL_TEMPLATE, guildId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.adminIds").isEmpty())
                .andExpect(jsonPath("$.djIds").isEmpty())
                .andExpect(jsonPath("$.userIds").isEmpty());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void ifAddUserToAdminPermissionThenReturnGuildPermissionWithUserOnAdminList() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        DiscordSnowflake userId = generateUniqueSnowflakeId();
        this.mockMvc.perform(put(PUT_URL_TEMPLATE, guildId, ADMIN_PERMISSION_LEVEL, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.adminIds", hasItems(userId.toString())))
                .andExpect(jsonPath("$.djIds").isEmpty())
                .andExpect(jsonPath("$.userIds").isEmpty());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void ifAddUserToDjPermissionThenReturnGuildPermissionWithUserOnDjList() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        DiscordSnowflake userId = generateUniqueSnowflakeId();
        this.mockMvc.perform(put(PUT_URL_TEMPLATE, guildId, DJ_PERMISSION_LEVEL, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.adminIds").isEmpty())
                .andExpect(jsonPath("$.djIds", hasItems(userId.toString())))
                .andExpect(jsonPath("$.userIds").isEmpty());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void ifAddUserToUserPermissionThenReturnGuildPermissionWithUserOnUserList() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        DiscordSnowflake userId = generateUniqueSnowflakeId();
        this.mockMvc.perform(put(PUT_URL_TEMPLATE, guildId, USER_PERMISSION_LEVEL, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.adminIds").isEmpty())
                .andExpect(jsonPath("$.djIds").isEmpty())
                .andExpect(jsonPath("$.userIds", hasItems(userId.toString())));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void ifDeleteUserFromUserRoleTheUserIsNotOnThenReturnGuildPermissionWithoutUserOnUserList() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        DiscordSnowflake userId = generateUniqueSnowflakeId();
        this.mockMvc.perform(delete(PUT_URL_TEMPLATE, guildId, USER_PERMISSION_LEVEL, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.adminIds").isEmpty())
                .andExpect(jsonPath("$.djIds").isEmpty())
                .andExpect(jsonPath("$.userIds").isEmpty());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void ifAddUserToAdminPermissionAndRemoveThenReturnGuildPermissionWithoutUserOnAdminList() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        DiscordSnowflake userId = generateUniqueSnowflakeId();
        this.mockMvc.perform(put(PUT_URL_TEMPLATE, guildId, ADMIN_PERMISSION_LEVEL, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.adminIds", hasItems(userId.toString())))
                .andExpect(jsonPath("$.djIds").isEmpty())
                .andExpect(jsonPath("$.userIds").isEmpty());

        this.mockMvc.perform(delete(PUT_URL_TEMPLATE, guildId, ADMIN_PERMISSION_LEVEL, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.adminIds").isEmpty())
                .andExpect(jsonPath("$.djIds").isEmpty())
                .andExpect(jsonPath("$.userIds").isEmpty());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void ifAddUserToDjPermissionAndDeleteThenReturnGuildPermissionWithoutUserOnDjList() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        DiscordSnowflake userId = generateUniqueSnowflakeId();
        this.mockMvc.perform(put(PUT_URL_TEMPLATE, guildId, DJ_PERMISSION_LEVEL, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.adminIds").isEmpty())
                .andExpect(jsonPath("$.djIds", hasItems(userId.toString())))
                .andExpect(jsonPath("$.userIds").isEmpty());

        this.mockMvc.perform(delete(PUT_URL_TEMPLATE, guildId, DJ_PERMISSION_LEVEL, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.adminIds").isEmpty())
                .andExpect(jsonPath("$.djIds").isEmpty())
                .andExpect(jsonPath("$.userIds").isEmpty());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void ifAddUserToUserPermissionAndDeleteThenReturnGuildPermissionWithoutUserOnUserList() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        DiscordSnowflake userId = generateUniqueSnowflakeId();
        this.mockMvc.perform(put(PUT_URL_TEMPLATE, guildId, USER_PERMISSION_LEVEL, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.adminIds").isEmpty())
                .andExpect(jsonPath("$.djIds").isEmpty())
                .andExpect(jsonPath("$.userIds", hasItems(userId.toString())));

        this.mockMvc.perform(delete(PUT_URL_TEMPLATE, guildId, USER_PERMISSION_LEVEL, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.adminIds").isEmpty())
                .andExpect(jsonPath("$.djIds").isEmpty())
                .andExpect(jsonPath("$.userIds").isEmpty());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void ifAddTwoUsersToUserPermissionThenReturnGuildPermissionTwoUsersOnUserList() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        DiscordSnowflake userId = generateUniqueSnowflakeId();
        DiscordSnowflake userId2 = generateUniqueSnowflakeId();
        this.mockMvc.perform(put(PUT_URL_TEMPLATE, guildId, USER_PERMISSION_LEVEL, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.adminIds").isEmpty())
                .andExpect(jsonPath("$.djIds").isEmpty())
                .andExpect(jsonPath("$.userIds", hasItems(userId.toString())));

        this.mockMvc.perform(put(PUT_URL_TEMPLATE, guildId, USER_PERMISSION_LEVEL, userId2))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.guildId", both(isA(String.class)).and(is(guildId.getSnowflakeId()))))
                .andExpect(jsonPath("$.adminIds").isEmpty())
                .andExpect(jsonPath("$.djIds").isEmpty())
                .andExpect(jsonPath("$.userIds", hasItems(userId.toString(), userId2.toString())));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void ifAddUserToInvalidRolePermissionThenReturnBadRequest() throws Exception {
        DiscordSnowflake guildId = generateUniqueSnowflakeId();
        DiscordSnowflake userId = generateUniqueSnowflakeId();
        this.mockMvc.perform(put(PUT_URL_TEMPLATE, guildId, "supersuperadmin", userId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

}
