package com.argusoft.who.emcare.web.user.entity;

import com.argusoft.who.emcare.web.fhir.dto.ActivityDefinitionDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meanbean.test.BeanTester;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoleEntityTest {
    @Test
    void testGettersAndSetters() {
        BeanTester beanTester = new BeanTester();
        beanTester.testBean(RoleEntity.class);
    }
}
