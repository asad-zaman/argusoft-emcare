package com.argusoft.who.emcare.web.indicators.codes.dto;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomCodeRequestDtoTest {
    @Test
    public void testGetterAndSetterMethods() {
        CustomCodeRequestDto customCodeRequestDto = new CustomCodeRequestDto();
        Long codeId = 1L;
        String code = "TEST_CODE";
        String codeDescription = "Test Code Description";
        String valueType = "String";
        String[] condition = {"=Equal To", ">Greater Than"};
        String[] value = {"TestValue1", "TestValue2"};

        customCodeRequestDto.setCodeId(codeId);
        customCodeRequestDto.setCode(code);
        customCodeRequestDto.setCodeDescription(codeDescription);
        customCodeRequestDto.setValueType(valueType);
        customCodeRequestDto.setCondition(condition);
        customCodeRequestDto.setValue(value);

        assertEquals(codeId, customCodeRequestDto.getCodeId());
        assertEquals(code, customCodeRequestDto.getCode());
        assertEquals(codeDescription, customCodeRequestDto.getCodeDescription());
        assertEquals(valueType, customCodeRequestDto.getValueType());
        assertArrayEquals(condition, customCodeRequestDto.getCondition());
        assertArrayEquals(value, customCodeRequestDto.getValue());
    }

    @Test
    public void testDefaultConstructor() {
        CustomCodeRequestDto customCodeRequestDto = new CustomCodeRequestDto();

        assertNull(customCodeRequestDto.getCodeId());
        assertNull(customCodeRequestDto.getCode());
        assertNull(customCodeRequestDto.getCodeDescription());
        assertNull(customCodeRequestDto.getValueType());
        assertNull(customCodeRequestDto.getCondition());
        assertNull(customCodeRequestDto.getValue());
    }

}