package com.argusoft.who.emcare.web.language.dto;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LanguageAddDtoTest {
    @Test
    void testGettersAndSetters() {
        LanguageAddDto languageAddDto = new LanguageAddDto();
        languageAddDto.setLanguageCode("1");
        languageAddDto.setLanguageName("English");

        assertEquals("1", languageAddDto.getLanguageCode());
        assertEquals("English", languageAddDto.getLanguageName());
    }

    @Test
    void testDefaultValues() {
        LanguageAddDto languageAddDto = new LanguageAddDto();

        assertNull(languageAddDto.getLanguageCode());
        assertNull(languageAddDto.getLanguageName());
    }
}