package com.argusoft.who.emcare.web.tenant.service;

import com.argusoft.who.emcare.web.tenant.dto.TenantDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 27/02/23  12:22 pm
 */
public interface TenantService {

    public ResponseEntity addNewTenant(TenantDto tenantDto) throws Exception;

    public List<TenantDto> getAllTenantDetails();

    public ResponseEntity checkDataAlreadyExistOrNot(String key, String value);
}
