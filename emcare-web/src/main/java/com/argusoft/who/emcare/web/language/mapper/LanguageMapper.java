package com.argusoft.who.emcare.web.language.mapper;

import com.argusoft.who.emcare.web.language.dto.LanguageDto;
import com.argusoft.who.emcare.web.language.model.LanguageTranslation;

public class LanguageMapper {

    private LanguageMapper() {
    }

    public static LanguageTranslation getLanguageTranslation(LanguageDto dto) {
        LanguageTranslation languageTranslation = new LanguageTranslation();
        languageTranslation.setId(dto.getId());
        languageTranslation.setLanguageCode(dto.getLanguageCode());
        languageTranslation.setLanguageData(dto.getLanguageTranslation());
        languageTranslation.setLanguageName(dto.getLanguageName());
        return languageTranslation;
    }
}
