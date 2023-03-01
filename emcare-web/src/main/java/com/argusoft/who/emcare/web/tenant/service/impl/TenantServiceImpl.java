package com.argusoft.who.emcare.web.tenant.service.impl;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.response.Response;
import com.argusoft.who.emcare.web.tenant.dto.TenantDto;
import com.argusoft.who.emcare.web.tenant.entity.TenantConfig;
import com.argusoft.who.emcare.web.tenant.mapper.TenantMapper;
import com.argusoft.who.emcare.web.tenant.repository.TenantConfigRepository;
import com.argusoft.who.emcare.web.tenant.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Optional<TenantConfig> tConfig = tenantConfigRepository.findByTenantId(tenantDto.getTenantId());
        if (tConfig.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Tenant Id Already Exist", HttpStatus.BAD_REQUEST.value()));
        }
        tConfig = tenantConfigRepository.findByUrl(tenantDto.getUrl());
        if (tConfig.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("URL Already Exist", HttpStatus.BAD_REQUEST.value()));
        }
        tConfig = tenantConfigRepository.findByDomain(tenantDto.getDomain());
        if (tConfig.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Domain Already Exist", HttpStatus.BAD_REQUEST.value()));
        }

        TenantConfig tenantConfig = TenantMapper.getTenantConfig(tenantDto);
        tenantConfigRepository.save(tenantConfig);
        return ResponseEntity.ok().body(tenantConfig);
    }

    @Override
    public List<TenantDto> getAllTenantDetails() {
        List<TenantConfig> tenantConfigs = tenantConfigRepository.findAll();
        List<TenantDto> tenantDtoList = new ArrayList<>();
        for (TenantConfig tenantConfig : tenantConfigs) {
            tenantDtoList.add(TenantMapper.getTenantDto(tenantConfig));
        }
        return tenantDtoList;
    }

    @Override
    public ResponseEntity checkDataAlreadyExistOrNot(String key, String value) {
        if (key.equalsIgnoreCase(TenantConfig.Fields.TENANT_ID)) {
            Optional<TenantConfig> tConfig = tenantConfigRepository.findByTenantId(value);
            if (tConfig.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Tenant Id is Already Exist", HttpStatus.BAD_REQUEST.value()));
            }
        } else if (key.equalsIgnoreCase(TenantConfig.Fields.DOMAIN)) {
            Optional<TenantConfig> tConfig = tenantConfigRepository.findByDomain(value);
            if (tConfig.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Domain is Already Exist", HttpStatus.BAD_REQUEST.value()));
            }
        } else if (key.equalsIgnoreCase(TenantConfig.Fields.URL)) {
            Optional<TenantConfig> tConfig = tenantConfigRepository.findByUrl(value);
            if (tConfig.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("URL is Already Exist", HttpStatus.BAD_REQUEST.value()));
            }
        } else {
            return ResponseEntity.ok().body(new Response("This key doesn't exist", HttpStatus.OK.value()));
        }
        return ResponseEntity.ok().body(new Response("This key doesn't exist", HttpStatus.OK.value()));
    }

}
