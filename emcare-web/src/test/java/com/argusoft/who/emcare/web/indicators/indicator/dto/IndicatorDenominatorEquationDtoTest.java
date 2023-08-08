package com.argusoft.who.emcare.web.indicators.indicator.dto;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IndicatorDenominatorEquationDtoTest {

    @Test
    void testGettersAndSetters() {
        IndicatorDenominatorEquationDto dto = new IndicatorDenominatorEquationDto();

        dto.setDenominatorId(1L);
        dto.setCodeId(2L);
        dto.setCode("Code123");
        dto.setCondition("Condition123");
        dto.setValue("Value123");
        dto.setValueType("ValueType");
        dto.setEqIdentifier("EquatiationIdentifier");

        assertEquals(1L, dto.getDenominatorId());
        assertEquals(2L, dto.getCodeId());
        assertEquals("Code123", dto.getCode());
        assertEquals("Condition123", dto.getCondition());
        assertEquals("Value123", dto.getValue());
        assertEquals("ValueType", dto.getValueType());
        assertEquals("EquatiationIdentifier", dto.getEqIdentifier());
    }

    @Test
    void testDefaultValues() {
        IndicatorDenominatorEquationDto dto = new IndicatorDenominatorEquationDto();

        // Check default values
        assertNull(dto.getDenominatorId());
        assertNull(dto.getCodeId());
        assertNull(dto.getCode());
        assertNull(dto.getCondition());
        assertNull(dto.getValue());
        assertNull(dto.getValueType());
        assertNull(dto.getEqIdentifier());
    }
}
