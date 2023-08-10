package com.argusoft.who.emcare.web.adminsetting.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = {Settings.class})
public class SettingsTest {
    private Settings settings;

    @BeforeEach
    public void setUp() {
        settings = new Settings();
    }

    @Test
    public void testSetAndGetId(){
        Long id = 1L;
        settings.setId(id);

        assertEquals(id, settings.getId());
    }

    @Test
    public void testSetAndGetName(){
        String name = "Name1";
        settings.setName(name);

        assertEquals(name, settings.getName());
    }

    @Test
    public void testSetAndGetKey(){
        String key = "Key1";
        settings.setKey(key);

        assertEquals(key, settings.getKey());
    }

    @Test
    public void testSetAndGetValue(){
        String value = "Value1";
        settings.setValue(value);

        assertEquals(value, settings.getValue());
    }
}
