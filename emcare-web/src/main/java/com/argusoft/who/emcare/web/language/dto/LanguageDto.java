package com.argusoft.who.emcare.web.language.dto;

public class LanguageDto {

    public Integer id;
    public String languageName;
    public String languageCode;
    public String languageTranslation;

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

    public String getLanguageTranslation() {
        return languageTranslation;
    }

    public void setLanguageTranslation(String languageTranslation) {
        this.languageTranslation = languageTranslation;
    }
}
