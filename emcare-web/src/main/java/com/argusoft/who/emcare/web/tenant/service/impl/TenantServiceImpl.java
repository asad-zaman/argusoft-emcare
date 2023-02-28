package com.argusoft.who.emcare.web.tenant.service.impl;

import com.argusoft.who.emcare.web.tenant.dto.TenantDto;
import com.argusoft.who.emcare.web.tenant.repository.TenantConfigRepository;
import com.argusoft.who.emcare.web.tenant.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 27/02/23  12:23 pm
 */
@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    TenantConfigRepository tenantConfigRepository;

    @Override
    public ResponseEntity addNewTenant(TenantDto tenantDto) {
        return null;
    }
}
