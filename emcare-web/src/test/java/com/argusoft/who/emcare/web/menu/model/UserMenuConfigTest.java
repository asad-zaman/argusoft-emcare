package com.argusoft.who.emcare.web.menu.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.meanbean.test.BeanTester;
import org.mockito.MockitoAnnotations;

class UserMenuConfigTest {
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
    void testGetterSetters() {
        BeanTester beanTester = new BeanTester();
        beanTester.testBean(UserMenuConfig.class);
    }
}