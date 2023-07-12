package com.argusoft.who.emcare.web.questionnaireresponse.model;

import com.argusoft.who.emcare.web.common.model.EntityAuditInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

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

    @Column(name = "consultation_date")
    private Date consultationDate;


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

    public Date getConsultationDate() {
        return consultationDate;
    }

    public void setConsultationDate(Date consultationDate) {
        this.consultationDate = consultationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionnaireResponse)) return false;
        if (!super.equals(o)) return false;
        QuestionnaireResponse that = (QuestionnaireResponse) o;
        return isActive == that.isActive && Objects.equals(getId(), that.getId()) && Objects.equals(getQuestionnaireResponseText(), that.getQuestionnaireResponseText()) && Objects.equals(getPatientId(), that.getPatientId()) && Objects.equals(getConsultationStage(), that.getConsultationStage()) && Objects.equals(getEncounterId(), that.getEncounterId()) && Objects.equals(getStructureMapId(), that.getStructureMapId()) && Objects.equals(getQuestionnaireId(), that.getQuestionnaireId()) && Objects.equals(getConsultationDate(), that.getConsultationDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getQuestionnaireResponseText(), getPatientId(), getConsultationStage(), isActive, getEncounterId(), getStructureMapId(), getQuestionnaireId(), getConsultationDate());
    }
}
