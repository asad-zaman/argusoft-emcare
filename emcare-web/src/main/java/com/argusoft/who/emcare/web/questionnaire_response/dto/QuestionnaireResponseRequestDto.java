package com.argusoft.who.emcare.web.questionnaire_response.dto;

public class QuestionnaireResponseRequestDto {

    private String id;
    private String questionnaireResponseText;
    private String patientId;
    private String consultationStage;
    private boolean isActive;
    private String encounterId;
    private String structureMapId;
    private String questionnaireId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getConsultationStage() {
        return consultationStage;
    }

    public void setConsultationStage(String consultationStage) {
        this.consultationStage = consultationStage;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public String getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
    }

    public String getStructureMapId() {
        return structureMapId;
    }

    public void setStructureMapId(String structureMapId) {
        this.structureMapId = structureMapId;
    }

    public String getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(String questionnaireId) {
        this.questionnaireId = questionnaireId;
    }
}
