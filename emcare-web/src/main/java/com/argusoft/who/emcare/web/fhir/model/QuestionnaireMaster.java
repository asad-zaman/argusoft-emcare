package com.argusoft.who.emcare.web.fhir.model;

import com.argusoft.who.emcare.web.common.model.EntityAuditInfo;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "questionnaire_master")
public class QuestionnaireMaster extends EntityAuditInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "text", columnDefinition = "TEXT")
    private String text;
    
    @Column(name = "resource_id", columnDefinition = "TEXT")
    private String resourceId;

    @Column(name = "version", columnDefinition = "TEXT")
    private String version;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionnaireMaster)) return false;
        if (!super.equals(o)) return false;
        QuestionnaireMaster that = (QuestionnaireMaster) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getText(), that.getText()) && Objects.equals(getResourceId(), that.getResourceId()) && Objects.equals(getVersion(), that.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getText(), getResourceId(), getVersion());
    }
}
