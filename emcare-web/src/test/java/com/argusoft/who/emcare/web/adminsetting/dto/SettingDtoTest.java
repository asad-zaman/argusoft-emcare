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
    public void testSetAndGetId(){
        Long id = 1L;
        settingDto.setId(id);

        assertEquals(id, settingDto.getId());
    }

    @Test
    public void testSetAndGetName(){
        String name = "Name1";
        settingDto.setName(name);

        assertEquals(name, settingDto.getName());
    }

    @Test
    public void testSetAndGetKey(){
        String key = "Key1";
        settingDto.setKey(key);

        assertEquals(key, settingDto.getKey());
    }

    @Test
    public void testSetAndGetValue(){
        String value = "Value1";
        settingDto.setValue(value);

        assertEquals(value, settingDto.getValue());
    }
}
