package com.argusoft.who.emcare.web.user.dto;

/**
 * @author jay
 */
public class UserDto {

    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String regRequestFrom;
    private Integer locationId;
    private String roleName;


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

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
