package com.argusoft.who.emcare.web.menu.dto;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.meanbean.test.BeanTester;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class CurrentUserFeatureJsonTest {

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
    void dtoGetterSetterTest() {
        BeanTester beanTester = new BeanTester();
        beanTester.testBean(CurrentUserFeatureJson.class);
    }
}