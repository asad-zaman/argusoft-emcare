package com.argusoft.who.emcare.web.menu.dto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.meanbean.test.BeanTester;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class FeatureJSONTest {
    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testParameterisedConstructor() {
        FeatureJSON mockFeatureJSON = new FeatureJSON(true, true, true, true, true);
        String expectedToString = "{\"canEdit\":" + true + ",\"canDelete\":" + true + ",\"canAdd\":" + true + ",\"canView\":" + true + ",\"canExport\":" + true + "}";
        assertEquals(expectedToString, mockFeatureJSON.toString());
    }

    @Test
    void dtoGetterSetterTest() {
        BeanTester beanTester = new BeanTester();
        beanTester.testBean(FeatureJSON.class);
    }
}