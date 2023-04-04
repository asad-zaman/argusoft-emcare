package com.argusoft.who.emcare.web.tenant.mapper;

import com.argusoft.who.emcare.web.tenant.dto.TenantDto;
import com.argusoft.who.emcare.web.tenant.entity.TenantConfig;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 01/03/23  12:27 pm
 */
public class TenantMapper {

    private TenantMapper() {
    }

    public static TenantConfig getTenantConfig(TenantDto tenantDto, String orgName) {
        TenantConfig tenantConfig = new TenantConfig();
        tenantConfig.setId(tenantDto.getId());
        tenantConfig.setDomain(tenantDto.getDomain());
        tenantConfig.setUsername(tenantDto.getUsername());
        tenantConfig.setPassword(tenantDto.getPassword());
        tenantConfig.setTenantId(tenantDto.getTenantId());
        tenantConfig.setUrl(tenantDto.getUrl());
        tenantConfig.setDatabaseName(tenantDto.getDatabaseName());
        tenantConfig.setDatabasePort(tenantDto.getDatabasePort());
        tenantConfig.setAdminUser(tenantDto.getUser().getEmail());
        tenantConfig.setOrganization(orgName);
        return tenantConfig;
    }

    public static TenantDto getTenantDto(TenantConfig tenantConfig) {
        TenantDto tenantDto = new TenantDto();
        tenantDto.setId(tenantConfig.getId());
        tenantDto.setDomain(tenantConfig.getDomain());
        tenantDto.setTenantId(tenantConfig.getTenantId());
        tenantDto.setOrganization(tenantConfig.getOrganization());
        tenantDto.setUsername(tenantConfig.getAdminUser());
        return tenantDto;
    }
}
