package com.argusoft.who.emcare.web.common.service;

import com.argusoft.who.emcare.web.common.service.impl.CommonServiceImpl;
import com.argusoft.who.emcare.web.tenant.entity.TenantConfig;
import com.argusoft.who.emcare.web.tenant.repository.TenantConfigRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {CommonServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
class CommonServiceTest {

    @Mock
    private TenantConfigRepository tenantConfigRepository;

    @InjectMocks
    CommonServiceImpl commonService;

    AutoCloseable autoCloseable;

    private final String defaultTenant = "Kambezi";

    private final String defaultTenantDomain = "localhost:8080";

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testGetDomainFormUrl() {

        String url = "https://example.com/api/users";
        String uri = "/api/users";
        String expectedDomain = "example.com";

        String actualDomain = commonService.getDomainFormUrl(url,uri);

        assertEquals(expectedDomain, actualDomain);

    }

    @Test
    void testGetTenantIdFromURL_ExistingDomain() {
        String url = "https://example.com/api/users";
        String uri = "/api/users";
        String domain = "example.com";
        String tenantId = "tenant123";


        TenantConfig tenantConfig = new TenantConfig();
        tenantConfig.setTenantId(tenantId);

        when(tenantConfigRepository.findByDomain(domain)).thenReturn(Optional.of(tenantConfig));

        String actualTenantId = commonService.getTenantIdFromURL(url, uri);

        assertEquals(tenantId, actualTenantId);
    }

    @Test
    void testGetTenantIdFromURL_NonExistingDomain() {
        String url = "https://nonexisting.com/api/users";
        String uri = "/api/users";

        when(tenantConfigRepository.findByDomain(anyString())).thenReturn(Optional.empty());

        String actualTenantId = commonService.getTenantIdFromURL(url, uri);

        assertEquals(defaultTenant, actualTenantId);
    }

    @Test
    void testGetTenantIdFromURL_DefaultTenant() {
        String url = "https://example.com/api/users";
        String uri = "/api/users";
        String domain = "example.com";


        when(tenantConfigRepository.findByDomain(domain)).thenReturn(Optional.empty());

        String actualTenantId = commonService.getTenantIdFromURL(url, uri);

        assertEquals(defaultTenant, actualTenantId);
    }

}