package com.argusoft.who.emcare.web.location.dto;

public class LocationMasterWithHierarchy {

    private Integer id;

    private String name;
    private String type;
    private boolean isActive;
    private Long parent;
    private String hierarch;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public String getHierarch() {
        return hierarch;
    }

    public void setHierarch(String hierarch) {
        this.hierarch = hierarch;
    }
}
