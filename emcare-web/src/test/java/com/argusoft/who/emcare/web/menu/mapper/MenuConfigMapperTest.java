package com.argusoft.who.emcare.web.menu.mapper;

import com.argusoft.who.emcare.web.menu.dto.CurrentUserFeatureJson;
import com.argusoft.who.emcare.web.menu.dto.MenuConfigDto;
import com.argusoft.who.emcare.web.menu.dto.UserFeatureJson;
import com.argusoft.who.emcare.web.menu.model.UserMenuConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MenuConfigMapperTest {

    @Test
    void testGetMenuConfigDtoNoNullFields() {
        UserMenuConfig mockUserMenuConfig = new UserMenuConfig();
        mockUserMenuConfig.setId(1);
        mockUserMenuConfig.setMenuId(1);
        mockUserMenuConfig.setUserId("userId");
        mockUserMenuConfig.setRoleId("roleId");
        mockUserMenuConfig.setFeatureJson("featureJSON");

        String mockUserName = "userName";
        String mockRoleName = "roleName";

        MenuConfigDto actualMenuConfigDTO = MenuConfigMapper.getMenuConfigDto(mockUserMenuConfig, mockUserName, mockRoleName);

        assertNotNull(actualMenuConfigDTO);
        assertEquals(mockUserMenuConfig.getId(), actualMenuConfigDTO.getId());
        assertEquals(mockUserMenuConfig.getMenuId(), actualMenuConfigDTO.getMenuId());
        assertEquals(mockUserMenuConfig.getUserId(), actualMenuConfigDTO.getUserId());
        assertEquals(mockUserMenuConfig.getRoleId(), actualMenuConfigDTO.getRoleId());
        assertEquals(mockUserMenuConfig.getFeatureJson(), actualMenuConfigDTO.getFeatureJson());
        assertEquals(mockUserName, actualMenuConfigDTO.getUserName());
        assertEquals(mockRoleName, actualMenuConfigDTO.getRoleName());
    }

    @Test
    void testGetMenuConfigDtoPartialNullFields() {
        UserMenuConfig mockUserMenuConfig = new UserMenuConfig();
        mockUserMenuConfig.setId(1);
        mockUserMenuConfig.setMenuId(1);
        mockUserMenuConfig.setUserId(null);
        mockUserMenuConfig.setRoleId("roleId");
        mockUserMenuConfig.setFeatureJson("featureJSON");

        String mockRoleName = "roleName";

        MenuConfigDto actualMenuConfigDTO = MenuConfigMapper.getMenuConfigDto(mockUserMenuConfig, null, mockRoleName);

        assertNotNull(actualMenuConfigDTO);
        assertEquals(mockUserMenuConfig.getId(), actualMenuConfigDTO.getId());
        assertEquals(mockUserMenuConfig.getMenuId(), actualMenuConfigDTO.getMenuId());
        assertEquals(mockUserMenuConfig.getUserId(), actualMenuConfigDTO.getUserId());
        assertEquals(mockUserMenuConfig.getRoleId(), actualMenuConfigDTO.getRoleId());
        assertEquals(mockUserMenuConfig.getFeatureJson(), actualMenuConfigDTO.getFeatureJson());
        assertNull(actualMenuConfigDTO.getUserName());
        assertEquals(mockRoleName, actualMenuConfigDTO.getRoleName());
    }

    @Test
    void testGetUserMenuConfigEntityNoNullFields() {
        MenuConfigDto mockMenuConfig = new MenuConfigDto();
        mockMenuConfig.setId(1);
        mockMenuConfig.setMenuId(1);
        mockMenuConfig.setUserId("userId");
        mockMenuConfig.setRoleId("roleId");
        mockMenuConfig.setFeatureJson("featureJSON");
        mockMenuConfig.setUserName("userName");
        mockMenuConfig.setRoleName("roleName");

        UserMenuConfig actualUserMenuConfig = MenuConfigMapper.getUserMenuConfigEntity(mockMenuConfig);

        assertNotNull(actualUserMenuConfig);
        assertEquals(mockMenuConfig.getId(), actualUserMenuConfig.getId());
        assertEquals(mockMenuConfig.getMenuId(), actualUserMenuConfig.getMenuId());
        assertEquals(mockMenuConfig.getUserId(), actualUserMenuConfig.getUserId());
        assertEquals(mockMenuConfig.getRoleId(), actualUserMenuConfig.getRoleId());
        assertEquals(mockMenuConfig.getFeatureJson(), actualUserMenuConfig.getFeatureJson());
    }

    @Test
    void testGetUserMenuConfigEntityPartialNullFields() {
        MenuConfigDto mockMenuConfig = new MenuConfigDto();
        mockMenuConfig.setId(1);
        mockMenuConfig.setMenuId(1);
        mockMenuConfig.setUserId(null);
        mockMenuConfig.setRoleId("roleId");
        mockMenuConfig.setFeatureJson("featureJSON");
        mockMenuConfig.setUserName(null);
        mockMenuConfig.setRoleName("roleName");

        UserMenuConfig actualUserMenuConfig = MenuConfigMapper.getUserMenuConfigEntity(mockMenuConfig);

        assertNotNull(actualUserMenuConfig);
        assertEquals(mockMenuConfig.getId(), actualUserMenuConfig.getId());
        assertEquals(mockMenuConfig.getMenuId(), actualUserMenuConfig.getMenuId());
        assertEquals(mockMenuConfig.getUserId(), actualUserMenuConfig.getUserId());
        assertEquals(mockMenuConfig.getRoleId(), actualUserMenuConfig.getRoleId());
        assertEquals(mockMenuConfig.getFeatureJson(), actualUserMenuConfig.getFeatureJson());
    }

    @Test
    void testGetCurrentUserFeatureJsonNoNullFields() {
        UserFeatureJson mockUserFeatureJson = mock(UserFeatureJson.class);
        when(mockUserFeatureJson.getId()).thenReturn("1");
        when(mockUserFeatureJson.getOrderNumber()).thenReturn(1L);
        when(mockUserFeatureJson.getMenuName()).thenReturn("menuName");
        when(mockUserFeatureJson.getParent()).thenReturn(2L);
        when(mockUserFeatureJson.getFeatureJson()).thenReturn("featureJSON");
        String expectedCustomJSONFeature = "customJSONFeature";

        CurrentUserFeatureJson actualCurrentUserFeatureJSON = MenuConfigMapper.getCurrentUserFeatureJson(mockUserFeatureJson, expectedCustomJSONFeature);

        assertNotNull(actualCurrentUserFeatureJSON);
        assertEquals(Integer.parseInt(mockUserFeatureJson.getId()), actualCurrentUserFeatureJSON.getId());
        assertEquals(mockUserFeatureJson.getMenuName(), actualCurrentUserFeatureJSON.getMenuName());
        assertEquals(mockUserFeatureJson.getParent(), actualCurrentUserFeatureJSON.getParent());
        assertEquals(mockUserFeatureJson.getOrderNumber(), actualCurrentUserFeatureJSON.getOrderNumber());
        assertEquals(expectedCustomJSONFeature, actualCurrentUserFeatureJSON.getFeatureJson());
    }

    @Test
    void testGetCurrentUserFeatureJsonPartialNullFields() {
        UserFeatureJson mockUserFeatureJson = mock(UserFeatureJson.class);
        when(mockUserFeatureJson.getId()).thenReturn("1");
        when(mockUserFeatureJson.getOrderNumber()).thenReturn(1L);
        when(mockUserFeatureJson.getMenuName()).thenReturn("menuName");
        when(mockUserFeatureJson.getParent()).thenReturn(null);
        when(mockUserFeatureJson.getFeatureJson()).thenReturn("featureJSON");
        String expectedCustomJSONFeature = "customJSONFeature";

        CurrentUserFeatureJson actualCurrentUserFeatureJSON = MenuConfigMapper.getCurrentUserFeatureJson(mockUserFeatureJson, expectedCustomJSONFeature);

        assertNotNull(actualCurrentUserFeatureJSON);
        assertEquals(Integer.parseInt(mockUserFeatureJson.getId()), actualCurrentUserFeatureJSON.getId());
        assertEquals(mockUserFeatureJson.getMenuName(), actualCurrentUserFeatureJSON.getMenuName());
        assertEquals(mockUserFeatureJson.getParent(), actualCurrentUserFeatureJSON.getParent());
        assertEquals(mockUserFeatureJson.getOrderNumber(), actualCurrentUserFeatureJSON.getOrderNumber());
        assertEquals(expectedCustomJSONFeature, actualCurrentUserFeatureJSON.getFeatureJson());
    }
}