package com.argusoft.who.emcare.web.menu.service;

import com.argusoft.who.emcare.web.common.response.Response;
import com.argusoft.who.emcare.web.menu.dto.MenuConfigDto;
import com.argusoft.who.emcare.web.menu.model.MenuConfig;
import com.argusoft.who.emcare.web.menu.model.UserMenuConfig;

import java.util.List;

public interface MenuService {

    public List<MenuConfig> getAllMenus();

    public List<MenuConfigDto> getMenuConfigByMenuId(Integer menuId);

    public UserMenuConfig addMenuConfiguration(MenuConfigDto menuConfigDto);

    public Response deleteMenuConfig(Integer menuConfigId);

}
