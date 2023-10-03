package com.argusoft.who.emcare.web.userlocationmapping.model;

import com.argusoft.who.emcare.web.menu.model.MenuConfig;
import org.junit.jupiter.api.Test;
import org.meanbean.test.BeanTester;

import static org.junit.jupiter.api.Assertions.*;

class UserLocationMappingTest {
    @Test
    void testGetterSetters() {
        BeanTester beanTester = new BeanTester();
        beanTester.testBean(UserLocationMapping.class);
    }
}