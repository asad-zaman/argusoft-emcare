package com.argusoft.who.emcare.web.tenant.controller;

import com.argusoft.who.emcare.web.tenant.dto.TenantDto;
import com.argusoft.who.emcare.web.tenant.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 27/02/23  12:22 pm
 */
@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api/tenant")
public class TenantController {

    @Autowired
    TenantService tenantService;

    @GetMapping("/add")
    public ResponseEntity<Object> addNewTenant(@RequestBody TenantDto tenantDto) {
        return tenantService.addNewTenant(tenantDto);
    }
}
