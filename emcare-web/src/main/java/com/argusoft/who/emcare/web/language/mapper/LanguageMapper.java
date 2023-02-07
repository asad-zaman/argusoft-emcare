package com.argusoft.who.emcare.web.language.mapper;

import com.argusoft.who.emcare.web.language.dto.LanguageAddDto;
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

    public static LanguageDto getLanguageDto(LanguageAddDto dto, String languageTranslation) {
        LanguageDto languageDto = new LanguageDto();

        languageDto.setLanguageCode(dto.getLanguageCode());
        languageDto.setLanguageName(dto.getLanguageName());
        languageDto.setLanguageTranslation(languageTranslation);

        return languageDto;
    }
}
