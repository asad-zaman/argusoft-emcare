package com.argusoft.who.emcare.web.tenant.mapper;

import com.argusoft.who.emcare.web.device.mapper.DeviceMapper;
import com.argusoft.who.emcare.web.tenant.dto.TenantDto;
import com.argusoft.who.emcare.web.tenant.entity.TenantConfig;
import com.argusoft.who.emcare.web.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class TenantMapperTest {

    @Test
    void testConstructorInitialization() {
        TenantMapper tenantMapper = new TenantMapper();
        assertNotNull(tenantMapper, "TenantMapper instance should not be null");
    }

    @Test
    void testGetTenantConfig(){
        UserDto userDto = new UserDto();
        userDto.setEmail("email");

        TenantDto tenantDto = new TenantDto();
        tenantDto.setId(1);
        tenantDto.setDomain("Domain");
        tenantDto.setUsername("UserName");
        tenantDto.setPassword("Password");
        tenantDto.setTenantId("Id");
        tenantDto.setUrl("Url");
        tenantDto.setDatabaseName("DBName");
        tenantDto.setDatabasePort("DBPort");
        tenantDto.setUser(userDto);

        String orgName = "ORG";

        TenantConfig tenantConfig = TenantMapper.getTenantConfig(tenantDto,orgName);

        assertNotNull(tenantConfig);
        assertEquals(tenantConfig.getPassword(),tenantDto.getPassword());
        assertEquals(tenantConfig.getOrganization(),orgName);
    }

    @Test
    void testGetTenantDto(){
        TenantConfig tenantConfig = new TenantConfig();
        tenantConfig.setId(1);
        tenantConfig.setDomain("Domain");
        tenantConfig.setTenantId("Id");
        tenantConfig.setOrganization("ORG");
        tenantConfig.setUsername("Name");

        TenantDto tenantDto = TenantMapper.getTenantDto(tenantConfig);

        assertNotNull(tenantDto);
        assertEquals(tenantDto.getTenantId(),tenantConfig.getTenantId());
    }
}
