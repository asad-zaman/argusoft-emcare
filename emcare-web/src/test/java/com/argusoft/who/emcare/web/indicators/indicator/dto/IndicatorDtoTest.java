package com.argusoft.who.emcare.web.indicators.indicator.dto;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IndicatorDtoTest {

    @Test
    void testGettersAndSetters() {
        IndicatorDto dto = new IndicatorDto();

        dto.setIndicatorId(1L);
        dto.setIndicatorCode("Code123");
        dto.setIndicatorName("Indicator Name");
        dto.setDescription("Description");
        dto.setFacilityId("Facility123");
        dto.setNumeratorIndicatorEquation("NumEquation");
        dto.setDenominatorIndicatorEquation("DenomEquation");
        dto.setNumeratorEquations(new ArrayList<>());
        dto.setDenominatorEquations(new ArrayList<>());
        dto.setDisplayType("Display Type");
        dto.setNumeratorEquationString("NumEquationString");
        dto.setDenominatorEquationString("DenomEquationString");
        dto.setColourSchema("Color Schema");
        dto.setAge("Age");
        dto.setGender("Gender");
        dto.setQueryConfigure(true);
        dto.setQuery("Query");

        assertEquals(1L, dto.getIndicatorId());
        assertEquals("Code123", dto.getIndicatorCode());
        assertEquals("Indicator Name", dto.getIndicatorName());
        assertEquals("Description", dto.getDescription());
        assertEquals("Facility123", dto.getFacilityId());
        assertEquals("NumEquation", dto.getNumeratorIndicatorEquation());
        assertEquals("DenomEquation", dto.getDenominatorIndicatorEquation());
        assertEquals(new ArrayList<>(), dto.getNumeratorEquations());
        assertEquals(new ArrayList<>(), dto.getDenominatorEquations());
        assertEquals("Display Type", dto.getDisplayType());
        assertEquals("NumEquationString", dto.getNumeratorEquationString());
        assertEquals("DenomEquationString", dto.getDenominatorEquationString());
        assertEquals("Color Schema", dto.getColourSchema());
        assertEquals("Age", dto.getAge());
        assertEquals("Gender", dto.getGender());
        assertEquals(true, dto.getQueryConfigure());
        assertEquals("Query", dto.getQuery());
    }
    @Test
    void testDefaultValues() {
        IndicatorDto dto = new IndicatorDto();

        // Check default values
        assertNull(dto.getIndicatorId());
        assertNull(dto.getIndicatorCode());
        assertNull(dto.getIndicatorName());
        assertNull(dto.getDescription());
        assertNull(dto.getFacilityId());
        assertNull(dto.getNumeratorIndicatorEquation());
        assertNull(dto.getDenominatorIndicatorEquation());
        assertNull(dto.getNumeratorEquations());
        assertNull(dto.getDenominatorEquations());
        assertNull(dto.getDisplayType());
        assertNull(dto.getNumeratorEquationString());
        assertNull(dto.getDenominatorEquationString());
        assertNull(dto.getColourSchema());
        assertNull(dto.getAge());
        assertNull(dto.getGender());
        assertNull(dto.getQueryConfigure());
        assertNull(dto.getQuery());
    }
}
