package com.argusoft.who.emcare.web.tenant.service.impl;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.argusoft.who.emcare.web.common.response.Response;
import com.argusoft.who.emcare.web.config.tenant.MultitenantDataSourceConfiguration;
import com.argusoft.who.emcare.web.config.tenant.TenantContext;
import com.argusoft.who.emcare.web.fhir.model.LocationResource;
import com.argusoft.who.emcare.web.fhir.resourceprovider.OrganizationResourceProvider;
import com.argusoft.who.emcare.web.fhir.service.LocationResourceService;
import com.argusoft.who.emcare.web.fhir.service.OrganizationResourceService;
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
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);
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
    OrganizationResourceService organizationResourceService;

    @Autowired
    UserService userService;

    @Autowired
    LanguageService languageService;

    @Value("${defaultTenant}")
    private String defaultTenant;

    @Override
    public ResponseEntity addNewTenant(TenantDto tenantDto) {
        Optional<TenantConfig> tConfig = tenantConfigRepository.findByTenantId(tenantDto.getTenantId());
        if (tConfig.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Country Already Exist", HttpStatus.BAD_REQUEST.value()));
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

        TenantContext.clearTenant();
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

        HierarchyMaster hierarchyMaster = new HierarchyMaster();
        LocationMaster locationMaster = new LocationMaster();
        Organization organization = new Organization();
        String orgId = null;
        Location facility = new Location();
        LocationResource locationResource = new LocationResource();
        UserDto userDto = new UserDto();
        LanguageAddDto languageAddDto = new LanguageAddDto();

        TenantContext.setCurrentTenant(tenantConfig.getTenantId());
        Response response = new Response("Dose Not Work", HttpStatus.BAD_REQUEST.value());
        try {

            try {
                HierarchyMasterDto hierarchyMasterDto = tenantDto.getHierarchy();
                if (Objects.isNull(hierarchyMasterDto)) {
                    throw new RuntimeException("Hierarchy not saved!");
                }
                hierarchyMaster = (HierarchyMaster) locationService.createHierarchyMaster(hierarchyMasterDto).getBody();
            } catch (Exception ex) {
                afterExceptionProcess(tenantConfig);
                response = new Response(
                        "Hierarchy not saved properly! Please Contact Administrative Department.",
                        HttpStatus.BAD_REQUEST.value()
                );
            }

            try {
                LocationMasterDto locationMasterDto = tenantDto.getLocation();
                if (Objects.isNull(locationMasterDto)) {
                    throw new RuntimeException("Hierarchy not saved!");
                }
                locationMaster = (LocationMaster) locationService.createOrUpdate(locationMasterDto).getBody();
            } catch (Exception ex) {
                locationService.deleteHierarchyMaster(hierarchyMaster.getHierarchyType());
                afterExceptionProcess(tenantConfig);
                response = new Response(
                        "Location not saved properly! Please Contact Administrative Department.",
                        HttpStatus.BAD_REQUEST.value()
                );
            }

            try {
                organization = parser.parseResource(Organization.class, tenantDto.getOrganization());
                if (Objects.isNull(organization)) {
                    throw new RuntimeException("Please Enter Organization Details");
                }
                orgId = organizationResourceProvider.createOrganization(organization).getId().getIdPart();
                organization.setId(orgId);
            } catch (Exception ex) {
                locationService.deleteLocationById(locationMaster.getId());
                locationService.deleteHierarchyMaster(hierarchyMaster.getHierarchyType());
                afterExceptionProcess(tenantConfig);
                response = new Response(
                        "Organization not saved properly! Please Contact Administrative Department.",
                        HttpStatus.BAD_REQUEST.value()
                );
            }

            try {
                facility = parser.parseResource(Location.class, tenantDto.getFacility());
                if (Objects.isNull(facility)) {
                    throw new RuntimeException("Please Enter Facility Details");
                }
                Reference reference = new Reference();
                reference.setResource(organization);
                reference.setId(orgId);
                facility.setManagingOrganization(reference);
                List<Extension> extensions = new ArrayList<>();
                Extension extension = new Extension();
                extension.setValue(new IntegerType(locationMaster.getId()));
                extensions.add(extension);
                facility.setExtension(extensions);
                locationResource = locationResourceService.saveResource(facility);
            } catch (Exception ex) {
                organizationResourceService.deleteOrganizationResource(orgId);
                locationService.deleteLocationById(locationMaster.getId());
                locationService.deleteHierarchyMaster(hierarchyMaster.getHierarchyType());
//            afterExceptionProcess(tenantConfig);
                response = new Response(
                        "Facility not saved properly! Please Contact Administrative Department.",
                        HttpStatus.BAD_REQUEST.value()
                );
            }

            try {
                userDto = tenantDto.getUser();
                if (Objects.isNull(userDto)) {
                    throw new RuntimeException("Please Enter User Details");
                }
                List<String> facilityIds = new ArrayList<>();
                facilityIds.add(locationResource.getResourceId());
                userDto.setFacilityIds(facilityIds);
                userService.addUser(userDto);
            } catch (Exception ex) {
                locationResourceService.deleteLocationResource(locationResource.getResourceId());
                organizationResourceService.deleteOrganizationResource(orgId);
                locationService.deleteLocationById(locationMaster.getId());
                locationService.deleteHierarchyMaster(hierarchyMaster.getHierarchyType());
                afterExceptionProcess(tenantConfig);
                response = new Response(
                        "User not saved properly Or User Email Already Register In Other Country! Please Contact Administrative Department.",
                        HttpStatus.BAD_REQUEST.value()
                );
            }

            try {
                languageAddDto = tenantDto.getLanguage();
                if (Objects.nonNull(languageAddDto)) {
                    languageService.createNewLanguageTranslation(languageAddDto);
                }
            } catch (Exception ex) {
                response = new Response(
                        "Language not saved properly! Please Contact Administrative Department.",
                        HttpStatus.BAD_REQUEST.value()
                );
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(response);
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

    private void afterExceptionProcess(TenantConfig tenantConfig) {
        TenantContext.clearTenant();
        TenantContext.setCurrentTenant(defaultTenant);
        tenantConfigRepository.delete(tenantConfig);
    }

}
