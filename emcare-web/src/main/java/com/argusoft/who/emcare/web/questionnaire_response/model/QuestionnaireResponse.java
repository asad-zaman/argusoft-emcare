package com.argusoft.who.emcare.web.questionnaire_response.model;

import com.argusoft.who.emcare.web.common.model.EntityAuditInfo;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "questionnaire_response")
public class QuestionnaireResponse extends EntityAuditInfo implements Serializable {


    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "questionnaire_response", columnDefinition = "TEXT")
    private String questionnaireResponseText;

    @Column(name = "patient_id")
    private String patientId;


    @Column(name = "consultation_stage")
    private String consultationStage;


    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "encounter_id")
    private String encounterId;

    @Column(name = "structure_map_id")
    private String structureMapId;

    @Column(name = "questionnaire_id")
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
