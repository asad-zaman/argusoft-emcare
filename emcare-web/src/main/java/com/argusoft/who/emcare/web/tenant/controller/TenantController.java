package com.argusoft.who.emcare.web.tenant.controller;

import com.argusoft.who.emcare.web.tenant.dto.TenantDto;
import com.argusoft.who.emcare.web.tenant.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api/tenant")
public class TenantController {

    @Autowired
    TenantService tenantService;

    @PostMapping("/add")
    public ResponseEntity<Object> addNewTenant(@RequestBody TenantDto tenantDto) {
        return tenantService.addNewTenant(tenantDto);
    }

    @GetMapping("/all")
    public List<TenantDto> getAllTenant() {
        return tenantService.getAllTenantDetails();
    }

    @GetMapping("/check")
    public ResponseEntity getAllTenant(@RequestParam(value = "key") String key, @RequestParam(value = "value") String value) {
        return tenantService.checkDataAlreadyExistOrNot(key, value);
    }
}
