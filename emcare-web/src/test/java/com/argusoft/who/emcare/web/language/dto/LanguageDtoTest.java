package com.argusoft.who.emcare.web.language.dto;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LanguageDtoTest {
    @Test
    public void testGettersAndSetters() {
        LanguageDto languageDto = new LanguageDto();
        languageDto.setId(1);
        languageDto.setLanguageName("English");
        languageDto.setLanguageCode("en");
        languageDto.setLanguageTranslation("Abc");

        assertEquals("1", languageDto.getId().toString());
        assertEquals("English", languageDto.getLanguageName());
        assertEquals("en", languageDto.getLanguageCode());
        assertEquals("Abc", languageDto.getLanguageTranslation());
    }

    @Test
    void testDefaultValues() {
        LanguageDto languageDto = new LanguageDto();
        assertNull(languageDto.getId());
        assertNull(languageDto.getLanguageName());
        assertNull(languageDto.getLanguageCode());
        assertNull(languageDto.getLanguageTranslation());
    }

}
