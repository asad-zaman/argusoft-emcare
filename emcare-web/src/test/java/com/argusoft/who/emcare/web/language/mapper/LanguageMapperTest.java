package com.argusoft.who.emcare.web.language.mapper;

import com.argusoft.who.emcare.web.language.dto.LanguageAddDto;
import com.argusoft.who.emcare.web.language.dto.LanguageDto;
import com.argusoft.who.emcare.web.language.model.LanguageTranslation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LanguageMapperTest {

    @Test
    public void testGetLanguageTranslation() {
        LanguageDto languageDto = new LanguageDto();
        languageDto.setId(1);
        languageDto.setLanguageName("English");
        languageDto.setLanguageCode("en");
        languageDto.setLanguageTranslation("Abc");

        LanguageTranslation languageTranslation = LanguageMapper.getLanguageTranslation(languageDto);

        assertEquals(1, languageTranslation.getId());
        assertEquals("English", languageTranslation.getLanguageName());
        assertEquals("en", languageTranslation.getLanguageCode());
        assertEquals("Abc", languageTranslation.getLanguageData());
    }

    @Test
    public void testGetLanguageDto() {
        LanguageAddDto languageAddDto = new LanguageAddDto();
        languageAddDto.setLanguageName("Gujarati");
        languageAddDto.setLanguageCode("Guj");
        String languageTranslation = "kem cho";

        LanguageDto languageDto = LanguageMapper.getLanguageDto(languageAddDto, languageTranslation);

        assertEquals("Gujarati", languageDto.getLanguageName());
        assertEquals("Guj", languageDto.getLanguageCode());
        assertEquals("kem cho", languageDto.getLanguageTranslation());
    }
}
