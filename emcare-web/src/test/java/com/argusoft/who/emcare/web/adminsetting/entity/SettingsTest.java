package com.argusoft.who.emcare.web.adminsetting.entity;

import com.argusoft.who.emcare.web.adminsetting.entity.Settings;
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
    public void TestSetId(){

        Long id = 1L;
        settings.setId(id);

        Long resultId = settings.getId();

        assertEquals(id, resultId);

    }

    @Test
    public void testGetId(){

        Long id = 1L;
        settings.setId(id);

        assertEquals(id, settings.getId());
    }

    @Test
    public void testSetName(){

        String name = "Name1";
        settings.setName(name);

        String resultName = settings.getName();

        assertEquals(name, resultName);

    }

    @Test
    public void testGetName(){

        String name = "Name1";
        settings.setName(name);

        assertEquals(name, settings.getName());
    }

    @Test
    public void testSetKey(){
        String key = "Key1";
        settings.setKey(key);

        String resultKey = settings.getKey();

        assertEquals(key, resultKey);
    }

    @Test
    public void testGetKey(){
        String key = "Key1";
        settings.setKey(key);

        assertEquals(key, settings.getKey());
    }

    @Test
    public void testSetValue(){
        String value = "Value1";
        settings.setValue(value);

        String resultValue = settings.getValue();

        assertEquals(value, resultValue);
    }

    @Test
    public void testGetValue(){
        String value = "Value1";
        settings.setValue(value);

        assertEquals(value, settings.getValue());
    }
}
