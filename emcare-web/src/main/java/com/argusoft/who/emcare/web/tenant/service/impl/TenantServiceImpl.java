package com.argusoft.who.emcare.web.tenant.service.impl;

import com.argusoft.who.emcare.web.common.response.Response;
import com.argusoft.who.emcare.web.config.tenant.MultitenantDataSourceConfiguration;
import com.argusoft.who.emcare.web.config.tenant.TenantContext;
import com.argusoft.who.emcare.web.fhir.model.LocationResource;
import com.argusoft.who.emcare.web.fhir.resourceprovider.OrganizationResourceProvider;
import com.argusoft.who.emcare.web.fhir.service.LocationResourceService;
import com.argusoft.who.emcare.web.language.dto.LanguageAddDto;
import com.argusoft.who.emcare.web.language.service.LanguageService;
import com.argusoft.who.emcare.web.location.dto.HierarchyMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationMasterDto;
import com.argusoft.who.emcare.web.location.model.HierarchyMaster;
import com.argusoft.who.emcare.web.location.model.LocationMaster;
import com.argusoft.who.emcare.web.location.service.LocationService;
import com.argusoft.who.emcare.web.tenant.dto.TenantDto;
import com.argusoft.who.emcare.web.tenant.entity.TenantConfig;
import com.argusoft.who.emcare.web.tenant.mapper.TenantMapper;
import com.argusoft.who.emcare.web.tenant.repository.TenantConfigRepository;
import com.argusoft.who.emcare.web.tenant.service.TenantService;
import com.argusoft.who.emcare.web.user.dto.UserDto;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.sql.DataSource;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    @Autowired
    DataSource dataSource;

    @Autowired
    MultitenantDataSourceConfiguration multitenantDataSourceConfiguration;

    @Autowired
    LocationService locationService;

    @Autowired
    LocationResourceService locationResourceService;

    @Autowired
    OrganizationResourceProvider organizationResourceProvider;

    @Autowired
    UserService userService;

    @Autowired
    LanguageService languageService;


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
        tenantConfig = tenantConfigRepository.save(tenantConfig);

        multitenantDataSourceConfiguration.addDataSourceDynamic();

        TenantContext.setCurrentTenant(tenantConfig.getTenantId());

        try {
            Resource resource = new ClassPathResource("New_Database.sql");
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.execute(FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            tenantConfigRepository.delete(tenantConfig);
            TenantContext.clearTenant();
            return ResponseEntity.badRequest().body(
                    new Response(
                            "Database not setup properly! Please Contact Administrative Department.",
                            HttpStatus.BAD_REQUEST.value()
                    )
            );
        }

        HierarchyMaster hierarchyMaster;
        LocationMaster locationMaster;
        Organization organization;
        String orgId;
        Location facility;
        LocationResource locationResource;
        UserDto userDto;
        LanguageAddDto languageAddDto;


        try {
            HierarchyMasterDto hierarchyMasterDto = tenantDto.getHierarchy();
            if (Objects.isNull(hierarchyMasterDto)) {
                throw new RuntimeException("Hierarchy not saved!");
            }
            hierarchyMaster = (HierarchyMaster) locationService.createHierarchyMaster(hierarchyMasterDto).getBody();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    new Response(
                            "Hierarchy not saved properly! Please Contact Administrative Department.",
                            HttpStatus.BAD_REQUEST.value()
                    )
            );
        }

        try {
            LocationMasterDto locationMasterDto = tenantDto.getLocation();
            if (Objects.isNull(locationMasterDto)) {
                throw new RuntimeException("Hierarchy not saved!");
            }
            locationMaster = (LocationMaster) locationService.createOrUpdate(locationMasterDto).getBody();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    new Response(
                            "Location not saved properly! Please Contact Administrative Department.",
                            HttpStatus.BAD_REQUEST.value()
                    )
            );
        }

        try {
            organization = tenantDto.getOrganization();
            if (Objects.isNull(organization)) {
                throw new RuntimeException("Please Enter Organization Details");
            }
            orgId = organizationResourceProvider.createOrganization(organization).getId().getValue();
            organization.setId(orgId);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    new Response(
                            "Organization not saved properly! Please Contact Administrative Department.",
                            HttpStatus.BAD_REQUEST.value()
                    )
            );
        }

        try {
            facility = tenantDto.getFacility();
            if (Objects.isNull(facility)) {
                throw new RuntimeException("Please Enter Facility Details");
            }
            Reference reference = new Reference();
            reference.setResource(organization);
            facility.setManagingOrganization(reference);
            locationResource = locationResourceService.saveResource(facility);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    new Response(
                            "Facility not saved properly! Please Contact Administrative Department.",
                            HttpStatus.BAD_REQUEST.value()
                    )
            );
        }

        try {
            userDto = tenantDto.getUserDto();
            if (Objects.isNull(userDto)) {
                throw new RuntimeException("Please Enter User Details");
            }
            List<String> facilityIds = new ArrayList<String>();
            facilityIds.add(locationResource.getResourceId());
            userDto.setFacilityIds(facilityIds);
            userService.addUser(userDto);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    new Response(
                            "Facility not saved properly! Please Contact Administrative Department.",
                            HttpStatus.BAD_REQUEST.value()
                    )
            );
        }

        try {
            languageAddDto = tenantDto.getLanguage();
            if (Objects.isNull(languageAddDto)) {
                throw new RuntimeException("Please Enter Language Details");
            }
            languageService.createNewLanguageTranslation(languageAddDto);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    new Response(
                            "Facility not saved properly! Please Contact Administrative Department.",
                            HttpStatus.BAD_REQUEST.value()
                    )
            );
        }


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
