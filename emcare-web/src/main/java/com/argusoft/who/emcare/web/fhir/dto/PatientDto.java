package com.argusoft.who.emcare.web.fhir.dto;

import java.util.Date;

/**
 *
 * @author parth
 */
public class PatientDto {
    
    private String id;
    private String identifier;
    private String givenName;
    private String familyName;
    private String gender;
    private Date dob;
    private String caregiver;
    private String location;

    public PatientDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getCaregiver() {
        return caregiver;
    }

    public void setCaregiver(String caregiver) {
        this.caregiver = caregiver;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    
}
