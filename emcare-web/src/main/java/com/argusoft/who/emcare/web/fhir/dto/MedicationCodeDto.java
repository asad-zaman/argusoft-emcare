package com.argusoft.who.emcare.web.fhir.dto;

public class MedicationCodeDto {

    private String code;
    private String display;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }
}
