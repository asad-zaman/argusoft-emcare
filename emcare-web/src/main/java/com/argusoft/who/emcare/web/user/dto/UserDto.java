package com.argusoft.who.emcare.web.user.dto;

import java.util.List;

/**
 * @author jay
 */
public class UserDto {

    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String userName;
    private String regRequestFrom;
    private String roleName;
    private String language;
    private String phone;
    private String countryCode;
    private List<String> facilityIds;


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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegRequestFrom() {
        return regRequestFrom;
    }

    public void setRegRequestFrom(String regRequestFrom) {
        this.regRequestFrom = regRequestFrom;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getFacilityIds() {
        return facilityIds;
    }

    public void setFacilityIds(List<String> facilityIds) {
        this.facilityIds = facilityIds;
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
