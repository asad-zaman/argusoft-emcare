package com.argusoft.who.emcare.web.fhir.dto;

import java.util.List;

public class MedicationDto {

    private String id;
    private String status;
    private List<MedicationCodeDto> code;
    private List<MedicationCodeDto> form;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<MedicationCodeDto> getCode() {
        return code;
    }

    public void setCode(List<MedicationCodeDto> code) {
        this.code = code;
    }

    public List<MedicationCodeDto> getForm() {
        return form;
    }

    public void setForm(List<MedicationCodeDto> form) {
        this.form = form;
    }
}
