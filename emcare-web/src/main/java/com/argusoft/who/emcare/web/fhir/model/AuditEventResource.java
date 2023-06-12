package com.argusoft.who.emcare.web.fhir.model;

import com.argusoft.who.emcare.web.common.model.EntityAuditInfo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "audit_event_resource")
public class AuditEventResource extends EntityAuditInfo implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "text", columnDefinition = "TEXT")
    private String text;

    @Column(name = "resource_id")
    private String resourceId;

    @Column(name = "recorded")
    private Date recorded;

    @Column(name = "status")
    private String status;

    @Column(name = "conslt_stage")
    private String cnsltStage;

    @Column(name = "encounter_id")
    private String encounterId;

    @Column(name = "patient_id")
    private String patientId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getResourceId() {
        return resourceId;
    }

    public Date getRecorded() {
        return recorded;
    }

    public void setRecorded(Date recorded) {
        this.recorded = recorded;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCnsltStage() {
        return cnsltStage;
    }

    public void setCnsltStage(String cnsltStage) {
        this.cnsltStage = cnsltStage;
    }

    public String getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AuditEventResource that = (AuditEventResource) o;
        return Objects.equals(id, that.id) && Objects.equals(text, that.text) && Objects.equals(resourceId, that.resourceId) && Objects.equals(recorded, that.recorded) && Objects.equals(status, that.status) && Objects.equals(cnsltStage, that.cnsltStage) && Objects.equals(encounterId, that.encounterId) && Objects.equals(patientId, that.patientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, text, resourceId, recorded, status, cnsltStage, encounterId, patientId);
    }

}
