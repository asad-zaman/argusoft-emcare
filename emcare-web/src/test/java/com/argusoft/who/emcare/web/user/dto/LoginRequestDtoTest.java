package com.argusoft.who.emcare.web.user.dto;

import com.argusoft.who.emcare.web.tenant.dto.TenantDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meanbean.test.BeanTester;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LoginRequestDtoTest {

    @Test
    public void testTenantDto() {
        BeanTester beanTester = new BeanTester();
        beanTester.testBean(LoginRequestDto.class);
    }
}
