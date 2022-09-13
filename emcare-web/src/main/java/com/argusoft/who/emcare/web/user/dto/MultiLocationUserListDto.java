package com.argusoft.who.emcare.web.user.dto;

import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.location.dto.LocationMasterWithHierarchy;

import java.util.List;

public class MultiLocationUserListDto {

    private String id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String phone;
    private Boolean enabled;
    private List<String> realmRoles;
    private List<LocationMasterWithHierarchy> locations;
    private List<FacilityDto> facilities;

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

    public List<LocationMasterWithHierarchy> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationMasterWithHierarchy> locations) {
        this.locations = locations;
    }

    public List<FacilityDto> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<FacilityDto> facilities) {
        this.facilities = facilities;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
