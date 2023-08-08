package com.argusoft.who.emcare.web.adminsetting.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;


import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = {SettingDto.class})
public class SettingDtoTest {

    private SettingDto settingDto;

    @BeforeEach
    public void setUp() {
        settingDto = new SettingDto();
    }

    @Test
    public void TestSetId(){

        Long id = 1L;
        settingDto.setId(id);

        Long resultId = settingDto.getId();

        assertEquals(id, resultId);

    }

    @Test
    public void testGetId(){

        Long id = 1L;
        settingDto.setId(id);

        assertEquals(id, settingDto.getId());
    }

    @Test
    public void testSetName(){

        String name = "Name1";
        settingDto.setName(name);

        String resultName = settingDto.getName();

        assertEquals(name, resultName);

    }

    @Test
    public void testGetName(){

        String name = "Name1";
        settingDto.setName(name);

        assertEquals(name, settingDto.getName());
    }

    @Test
    public void testSetKey(){
        String key = "Key1";
        settingDto.setKey(key);

        String resultKey = settingDto.getKey();

        assertEquals(key, resultKey);
    }

    @Test
    public void testGetKey(){
        String key = "Key1";
        settingDto.setKey(key);

        assertEquals(key, settingDto.getKey());
    }

    @Test
    public void testSetValue(){
        String value = "Value1";
        settingDto.setValue(value);

        String resultValue = settingDto.getValue();

        assertEquals(value, resultValue);
    }

    @Test
    public void testGetValue(){
        String value = "Value1";
        settingDto.setValue(value);

        assertEquals(value, settingDto.getValue());
    }
}
