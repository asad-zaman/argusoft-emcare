package com.argusoft.who.emcare.web.tenant.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meanbean.test.BeanTester;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TenantDtoTest {

    @Test
   public void testTenantDto(){
       BeanTester beanTester = new BeanTester();
       beanTester.testBean(TenantDto.class);
   }

}
