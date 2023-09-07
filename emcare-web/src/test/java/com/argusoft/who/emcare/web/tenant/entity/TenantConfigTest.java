package com.argusoft.who.emcare.web.tenant.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meanbean.test.BeanTester;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TenantConfigTest {

    @Test
    public void testTenantEntity(){
        BeanTester beanTester = new BeanTester();
        beanTester.testBean(TenantConfig.class);
    }
}
