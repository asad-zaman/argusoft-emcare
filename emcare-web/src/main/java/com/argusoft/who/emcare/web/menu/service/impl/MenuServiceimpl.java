package com.argusoft.who.emcare.web.menu.service.impl;

import com.argusoft.who.emcare.web.common.response.Response;
import com.argusoft.who.emcare.web.menu.dao.MenuConfigRepository;
import com.argusoft.who.emcare.web.menu.dao.UserMenuConfigRepository;
import com.argusoft.who.emcare.web.menu.dto.FeatureJSON;
import com.argusoft.who.emcare.web.menu.dto.MenuConfigDto;
import com.argusoft.who.emcare.web.menu.mapper.MenuConfigMapper;
import com.argusoft.who.emcare.web.menu.model.MenuConfig;
import com.argusoft.who.emcare.web.menu.model.UserMenuConfig;
import com.argusoft.who.emcare.web.menu.service.MenuService;
import com.argusoft.who.emcare.web.user.service.UserService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class MenuServiceimpl implements MenuService {

    @Autowired
    MenuConfigRepository menuConfigRepository;

    @Autowired
    UserMenuConfigRepository userMenuConfigRepository;

    @Autowired
    UserService userService;

    @Override
    public List<MenuConfig> getAllMenus() {
        List<MenuConfig> menuConfigs = menuConfigRepository.findAll();
        Collections.sort(menuConfigs, (a, b) -> {
            return a.getMenuName().compareTo(b.getMenuName());
        });
        return menuConfigs;
    }

    @Override
    public List<MenuConfigDto> getMenuConfigByMenuId(Integer menuId) {
        List<MenuConfigDto> configs = new ArrayList<>();
        List<UserMenuConfig> userAccesses = userMenuConfigRepository.getMenuConfigByMenuId(menuId);
        for (UserMenuConfig userAccess : userAccesses) {
            String roleName = null;
            String userName = null;
            if (userAccess.getRoleId() != null) {
                roleName = userService.getRoleNameById(userAccess.getRoleId());
            }
            if (userAccess.getUserId() != null) {
                userName = userService.getUserById(userAccess.getUserId()).getUsername();
            }
            if(!userAccess.getFeatureJson().contains("canExport")){
                FeatureJSON featureJSON = new FeatureJSON(true, true, true, true,true);
                Gson g = new Gson();
                FeatureJSON f = g.fromJson(userAccess.getFeatureJson(), FeatureJSON.class);
                if(Objects.isNull(f.getCanExport()) || !f.getCanExport().booleanValue()) {
                    featureJSON.setCanExport(false);
                }
                userAccess.setFeatureJson(featureJSON.toString());
            }
            configs.add(MenuConfigMapper.getMenuConfigDto(userAccess, userName, roleName));
        }
        return configs;
    }

    @Override
    public UserMenuConfig addMenuConfiguration(MenuConfigDto menuConfig) {
        return userMenuConfigRepository.save(MenuConfigMapper.getUserMenuConfigEntity(menuConfig));
    }

    @Override
    public Response deleteMenuConfig(Integer menuConfigId) {
        try {
            userMenuConfigRepository.deleteById(menuConfigId);
            return new Response("Successfully Deleted", HttpStatus.OK.value());
        } catch (Exception ex) {
            return new Response(ex.getMessage(), ex.hashCode());
        }
    }
}
