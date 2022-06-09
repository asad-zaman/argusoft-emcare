package com.argusoft.who.emcare.web.menu.mapper;

import com.argusoft.who.emcare.web.menu.dto.CurrentUserFeatureJson;
import com.argusoft.who.emcare.web.menu.dto.MenuConfigDto;
import com.argusoft.who.emcare.web.menu.dto.UserFeatureJson;
import com.argusoft.who.emcare.web.menu.model.UserMenuConfig;

public class MenuConfigMapper {

    private MenuConfigMapper() {
    }

    public static MenuConfigDto getMenuConfigDto(UserMenuConfig menuConfig, String userName, String roleName) {
        MenuConfigDto configDto = new MenuConfigDto();
        configDto.setMenuId(menuConfig.getMenuId());
        configDto.setFeatureJson(menuConfig.getFeatureJson());
        configDto.setId(menuConfig.getId());
        configDto.setRoleId(menuConfig.getRoleId());
        configDto.setUserId(menuConfig.getUserId());
        configDto.setUserName(userName);
        configDto.setRoleName(roleName);
        return configDto;
    }

    public static UserMenuConfig getUserMenuConfigEntity(MenuConfigDto menuConfigDto) {
        UserMenuConfig menuConfig = new UserMenuConfig();
        menuConfig.setFeatureJson(menuConfigDto.getFeatureJson());
        menuConfig.setRoleId(menuConfigDto.getRoleId());
        menuConfig.setMenuId(menuConfigDto.getMenuId());
        menuConfig.setUserId(menuConfigDto.getUserId());
        menuConfig.setId(menuConfigDto.getId());
        return menuConfig;
    }

    public static CurrentUserFeatureJson getCurrentUserFeatureJson(UserFeatureJson ufj, String customeFeatureJSON) {
        CurrentUserFeatureJson fJSON = new CurrentUserFeatureJson();
        if (customeFeatureJSON == null) {
            fJSON.setFeatureJson(ufj.getFeatureJson());
        } else {
            fJSON.setFeatureJson(customeFeatureJSON);
        }
        fJSON.setMenuName(ufj.getMenuName());
        fJSON.setId(Integer.parseInt(ufj.getId()));
        fJSON.setParent(ufj.getParent());
        return fJSON;
    }
}
