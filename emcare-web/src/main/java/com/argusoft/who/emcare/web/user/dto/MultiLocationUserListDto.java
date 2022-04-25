package com.argusoft.who.emcare.web.user.dto;

import com.argusoft.who.emcare.web.location.model.LocationMaster;

import java.util.List;

public class MultiLocationUserListDto {

    private String id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private Boolean enabled;
    private List<String> realmRoles;
    private List<LocationMaster> locations;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getRealmRoles() {
        return realmRoles;
    }

    public void setRealmRoles(List<String> realmRoles) {
        this.realmRoles = realmRoles;
    }

    public List<LocationMaster> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationMaster> locations) {
        this.locations = locations;
    }
}