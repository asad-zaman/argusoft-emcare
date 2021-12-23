package com.argusoft.who.emcare.web.user.dto;

import com.argusoft.who.emcare.web.location.model.LocationMaster;

import java.util.List;
import java.util.Map;

public class UserMasterDto {

    private String userId;
    private String userName;
    private String email;
    private LocationMaster location;
    private String[] roles;
    private List<Map<String, Object>> feature;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocationMaster getLocation() {
        return location;
    }

    public void setLocation(LocationMaster location) {
        this.location = location;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public List<Map<String, Object>> getFeature() {
        return feature;
    }

    public void setFeature(List<Map<String, Object>> feature) {
        this.feature = feature;
    }
}
