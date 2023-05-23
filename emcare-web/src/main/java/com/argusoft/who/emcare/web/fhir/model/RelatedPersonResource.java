package com.argusoft.who.emcare.web.fhir.model;

import com.argusoft.who.emcare.web.common.model.EntityAuditInfo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "related_person_resource")
public class RelatedPersonResource extends EntityAuditInfo implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "text", columnDefinition = "TEXT")
    private String text;

    private String resourceId;

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

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelatedPersonResource)) return false;
        if (!super.equals(o)) return false;
        RelatedPersonResource that = (RelatedPersonResource) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getText(), that.getText()) && Objects.equals(getResourceId(), that.getResourceId()) && Objects.equals(getPatientId(), that.getPatientId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getText(), getResourceId(), getPatientId());
    }
}
