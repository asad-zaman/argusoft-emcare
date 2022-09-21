package com.argusoft.who.emcare.web.user.dto;

import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;

import java.util.List;

public class UserListDto {
    private String id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String phone;
    private String countryCode;
    private Boolean enabled;
    private List<String> realmRoles;
    private FacilityDto facilityDto;

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

    public FacilityDto getFacilityDto() {
        return facilityDto;
    }

    public void setFacilityDto(FacilityDto facilityDto) {
        this.facilityDto = facilityDto;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
