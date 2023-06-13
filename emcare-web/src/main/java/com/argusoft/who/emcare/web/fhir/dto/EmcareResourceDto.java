package com.argusoft.who.emcare.web.fhir.dto;

public interface EmcareResourceDto {

    public String getKey();

    public String getResourceId();

    public String getIdentifier();

    public String getGivenName();

    public String getFamilyName();

    public String getGender();

    public String getBirthDate();

    public String getFacilityName();

    public String getAddressLine();

    public String getOrganizationName();

    public String getLocationName();

    public String getConsultationDate();

    public String getText();
}
