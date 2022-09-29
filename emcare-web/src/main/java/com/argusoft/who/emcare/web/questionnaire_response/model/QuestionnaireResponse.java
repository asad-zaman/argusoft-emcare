package com.argusoft.who.emcare.web.questionnaire_response.model;

import com.argusoft.who.emcare.web.common.model.EntityAuditInfo;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "questionnaire_response")
public class QuestionnaireResponse extends EntityAuditInfo implements Serializable {


    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "questionnaire_response", columnDefinition = "TEXT")
    private String questionnaireResponseText;

    @Column(name = "patient_id", nullable = false)
    private String patientId;


    @Column(name = "cnslt_stage", nullable = false)
    private String cnsltStage;


    @Column(name = "status", nullable = false)
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
