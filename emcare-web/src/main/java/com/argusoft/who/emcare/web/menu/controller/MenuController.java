package com.argusoft.who.emcare.web.menu.controller;

import com.argusoft.who.emcare.web.common.response.Response;
import com.argusoft.who.emcare.web.menu.dto.MenuConfigDto;
import com.argusoft.who.emcare.web.menu.model.MenuConfig;
import com.argusoft.who.emcare.web.menu.model.UserMenuConfig;
import com.argusoft.who.emcare.web.menu.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    MenuService menuService;

    @GetMapping("/all")
    public List<MenuConfig> getAllMenu() {
        return menuService.getAllMenus();
    }

    @GetMapping("/menuconfig/{menuId}")
    public List<MenuConfigDto> getMenuConfigById(@PathVariable("menuId") Integer menuId) {
        return menuService.getMenuConfigByMenuId(menuId);
    }

    @PostMapping("/config/add")
    public UserMenuConfig addMenuConfig(@RequestBody MenuConfigDto menuConfig) {
        return menuService.addMenuConfiguration(menuConfig);
    }

    @DeleteMapping("/config/delete/{menuConfigId}")
    public Response deleteMenuConfig(@PathVariable("menuConfigId") Integer menuConfigId) {
        return menuService.deleteMenuConfig(menuConfigId);
    }

    @PutMapping("/config/update")
    public UserMenuConfig updateMenuConfig(@RequestBody MenuConfigDto menuConfig) {
        return menuService.addMenuConfiguration(menuConfig);
    }
}
