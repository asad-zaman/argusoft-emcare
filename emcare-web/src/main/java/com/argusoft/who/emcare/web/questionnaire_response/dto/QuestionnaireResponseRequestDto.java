package com.argusoft.who.emcare.web.questionnaire_response.dto;

public class QuestionnaireResponseRequestDto {

    private Long id;
    private String questionnaireResponseText;
    private String patientId;
    private String cnsltStage;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionnaireResponseText() {
        return questionnaireResponseText;
    }

    public void setQuestionnaireResponseText(String questionnaireResponseText) {
        this.questionnaireResponseText = questionnaireResponseText;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getCnsltStage() {
        return cnsltStage;
    }

    public void setCnsltStage(String cnsltStage) {
        this.cnsltStage = cnsltStage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
