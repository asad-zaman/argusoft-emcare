package com.argusoft.who.emcare.web.indicators.indicator.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IndicatorNumeratorEquationDtoTest {

    @Test
    void testGettersAndSetters() {
        IndicatorNumeratorEquationDto dto = new IndicatorNumeratorEquationDto();

        dto.setNumeratorId(1L);
        dto.setCodeId(2L);
        dto.setCode("NUM_CODE");
        dto.setCondition("NUM_CONDITION");
        dto.setValue("NUM_VALUE");
        dto.setValueType("NUM_VALUE_TYPE");
        dto.setEqIdentifier("NUM_EQ_IDENTIFIER");

        assertEquals(1L, dto.getNumeratorId());
        assertEquals(2L, dto.getCodeId());
        assertEquals("NUM_CODE", dto.getCode());
        assertEquals("NUM_CONDITION", dto.getCondition());
        assertEquals("NUM_VALUE", dto.getValue());
        assertEquals("NUM_VALUE_TYPE", dto.getValueType());
        assertEquals("NUM_EQ_IDENTIFIER", dto.getEqIdentifier());
    }


    @Test
    void testDefaultValues() {
        IndicatorNumeratorEquationDto dto = new IndicatorNumeratorEquationDto();

        // Check default values
        assertNull(dto.getNumeratorId());
        assertNull(dto.getCodeId());
        assertNull(dto.getCode());
        assertNull(dto.getCondition());
        assertNull(dto.getValue());
        assertNull(dto.getValueType());
        assertNull(dto.getEqIdentifier());
    }

}
