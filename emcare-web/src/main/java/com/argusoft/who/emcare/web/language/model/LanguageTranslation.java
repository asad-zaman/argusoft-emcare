package com.argusoft.who.emcare.web.language.model;

import com.argusoft.who.emcare.web.common.model.EntityAuditInfo;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "language_translation")
public class LanguageTranslation extends EntityAuditInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Basic(optional = false)
    @Column(name = "language_name", nullable = false)
    private String languageName;

    @Basic(optional = false)
    @Column(name = "language_code", nullable = false)
    private String languageCode;

    @Column(name = "language_data", columnDefinition = "TEXT")
    private String languageData;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageData() {
        return languageData;
    }

    public void setLanguageData(String languageData) {
        this.languageData = languageData;
    }
}
