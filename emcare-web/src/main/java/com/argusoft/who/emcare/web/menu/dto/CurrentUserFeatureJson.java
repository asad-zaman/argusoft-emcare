package com.argusoft.who.emcare.web.menu.dto;

import java.util.List;

public class CurrentUserFeatureJson {

    private Integer id;
    private String menuName;
    private String featureJson;
    private Long parent;
    private Long orderNumber;
    private List<CurrentUserFeatureJson> subMenu;

    public Integer getId() {
        return id;
    }

    public CurrentUserFeatureJson() {
    }

    public CurrentUserFeatureJson(Integer id, String menuName, String featureJson, Long parent, Long orderNumber, List<CurrentUserFeatureJson> subMenu) {
        this.id = id;
        this.menuName = menuName;
        this.featureJson = featureJson;
        this.parent = parent;
        this.orderNumber = orderNumber;
        this.subMenu = subMenu;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getFeatureJson() {
        return featureJson;
    }

    public void setFeatureJson(String featureJson) {
        this.featureJson = featureJson;
    }

    public List<CurrentUserFeatureJson> getSubMenu() {
        return subMenu;
    }

    public void setSubMenu(List<CurrentUserFeatureJson> subMenu) {
        this.subMenu = subMenu;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }
}

