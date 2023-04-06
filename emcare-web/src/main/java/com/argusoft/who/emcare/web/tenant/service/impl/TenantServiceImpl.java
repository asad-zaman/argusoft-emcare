package com.argusoft.who.emcare.web.tenant.service.impl;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.response.Response;
import com.argusoft.who.emcare.web.config.tenant.MultitenantDataSourceConfiguration;
import com.argusoft.who.emcare.web.config.tenant.TenantContext;
import com.argusoft.who.emcare.web.exception.EmCareException;
import com.argusoft.who.emcare.web.fhir.resourceprovider.OrganizationResourceProvider;
import com.argusoft.who.emcare.web.language.dto.LanguageDto;
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
import com.argusoft.who.emcare.web.user.dto.RoleDto;
import com.argusoft.who.emcare.web.user.dto.UserDto;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.hl7.fhir.r4.model.Organization;
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
import java.security.KeyStoreException;
import java.sql.*;
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
    OrganizationResourceProvider organizationResourceProvider;

    @Autowired
    UserService userService;

    @Autowired
    LanguageService languageService;

    @Value("${defaultTenant}")
    String defaultTenant;

    @Override
    public ResponseEntity addNewTenant(TenantDto tenantDto) throws Exception {
        Optional<TenantConfig> tConfig = tenantConfigRepository.findByTenantId(tenantDto.getTenantId());
        if (tConfig.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Country Already Exist", HttpStatus.BAD_REQUEST.value()));
        }
        tConfig = tenantConfigRepository.findByUrlAndDatabaseName(tenantDto.getUrl(), tenantDto.getDatabaseName());
        if (tConfig.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(tenantDto.getUrl() + "and " + tenantDto.getDatabaseName() + "URL Already Exist", HttpStatus.BAD_REQUEST.value()));
        }
        tConfig = tenantConfigRepository.findByDomain(tenantDto.getDomain());
        if (tConfig.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Domain Already Exist", HttpStatus.BAD_REQUEST.value()));
        }
        Organization organization = parser.parseResource(Organization.class, tenantDto.getOrganization());
        TenantConfig tenantConfig = TenantMapper.getTenantConfig(tenantDto, organization.getName());
        try {
            //    Create Database
            System.out.println("=======================================================================");
            createDatabase(tenantConfig);
            System.out.println("DataBase created Successfully+++++++++++++++++++++++++++++++++++++++++");

            //    Check Database Connection
            System.out.println("=======================================================================");
            checkDatabaseConnection(tenantConfig);
            System.out.println("Database Connection Checked +++++++++++++++++++++++++++++++++++++++++++");

            //    Select Database If create
            tenantConfig = tenantConfigRepository.save(tenantConfig);
            multitenantDataSourceConfiguration.addDataSourceDynamic();
            TenantContext.clearTenant();
            TenantContext.setCurrentTenant(tenantConfig.getTenantId());

            //    Add Default Data In Database
            System.out.println("=======================================================================");
            addDefaultDataSourceInDatabase();
            System.out.println("Data Added Successfully +++++++++++++++++++++++++++++++++++++++++++++++");

            //    Add HierarchyMaster in DB
            System.out.println("=======================================================================");
            addHierarchyMasterForNewDatabase(tenantDto);
            System.out.println("Hierachy Added ++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

            //    Add Location Master in DB
            System.out.println("=======================================================================");
            addLocationMasterInDatabase(tenantDto);
            System.out.println("Location Added ++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

            //    Add Organization Resource in DB
            System.out.println("=======================================================================");
            addOrganizationInDatabase(tenantDto);
            System.out.println("Organization added ++++++++++++++++++++++++++++++++++++++++++++++++++++");
            //    Add Role in DB
            System.out.println("=======================================================================");
            addRoleForTenant(tenantDto);
            System.out.println("Role Added ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            //    Add User in DB
            System.out.println("=======================================================================");
            addUserForTenant(tenantDto, tenantConfig);
            System.out.println("User Added ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            //    Add Language in DB
            System.out.println("=======================================================================");
            addLanguageInTenant(tenantDto);
            System.out.println("Language added ++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        } catch (Exception ex) {
            //    Remove All The Data Which Added in Database
            afterExceptionProcess(tenantConfig, tenantDto);
            ex.printStackTrace();
            throw new Exception(ex.getMessage());
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
        } else {
            return ResponseEntity.ok().body(new Response("This key doesn't exist", HttpStatus.OK.value()));
        }
        return ResponseEntity.ok().body(new Response("This key doesn't exist", HttpStatus.OK.value()));
    }

    private void afterExceptionProcess(TenantConfig tenantConfig, TenantDto tenantDto) throws Exception {
        TenantContext.clearTenant();
        TenantContext.setCurrentTenant(defaultTenant);
        tenantConfigRepository.delete(tenantConfig);
        multitenantDataSourceConfiguration.addDataSourceDynamic();
        dropDatabase(tenantConfig);
        removeUser(tenantDto);
        removeUser(tenantDto);
    }

    private void createDatabase(TenantConfig tenantConfig) {
        Connection connection = null;
        Statement statement = null;

        try {
            String databaseConnectionURL = CommonConstant.URL_PREFIX + tenantConfig.getUrl() + ":" + tenantConfig.getDatabasePort() + "/";
            connection = DriverManager.getConnection(
                    databaseConnectionURL,
                    CommonConstant.POSTGRESQL_DEFAULT_DATABASE,
                    tenantConfig.getPassword());
            statement = connection.createStatement();
            statement.executeQuery("SELECT count(*) FROM pg_database WHERE datname = '" + tenantConfig.getDatabaseName() + "'");
            ResultSet resultSet = statement.getResultSet();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count <= 0) {
                statement.executeUpdate("CREATE DATABASE " + tenantConfig.getDatabaseName());
            } else {
                throw new EmCareException("Database Already Exist with this name", new SQLException());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkDatabaseConnection(TenantConfig tenantConfig) throws SQLException {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            boolean isDatabaseConnected = connection.isValid(20);
            if (!isDatabaseConnected) {
                throw new SQLException();
            }
        } catch (SQLException sqlException) {
            throw new SQLException();
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void dropDatabase(TenantConfig tenantConfig) throws SQLException {
        Connection connection = null;
        Statement statement = null;

        try {
            String databaseConnectionURL = CommonConstant.URL_PREFIX + tenantConfig.getUrl() + ":" + tenantConfig.getDatabasePort() + "/";
            connection = DriverManager.getConnection(
                    databaseConnectionURL,
                    CommonConstant.POSTGRESQL_DEFAULT_DATABASE,
                    tenantConfig.getPassword());

            statement.executeUpdate("DROP DATABASE " + tenantConfig.getDatabaseName());
        } catch (Exception ex) {
            System.out.println("Database Doesn't drop Properly Because ==========>");
            ex.printStackTrace();
            throw new SQLException();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void addDefaultDataSourceInDatabase() throws SQLException {
        try {
            Resource resource = new ClassPathResource("New_Database.sql");
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.execute(FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new SQLException();
        }
    }

    private HierarchyMaster addHierarchyMasterForNewDatabase(TenantDto tenantDto) {
        HierarchyMaster hierarchyMaster = new HierarchyMaster();
        try {
            HierarchyMasterDto hierarchyMasterDto = tenantDto.getHierarchy();
            if (Objects.isNull(hierarchyMasterDto)) {
                throw new NullPointerException("Hierarchy not Present In Data!");
            }
            return (HierarchyMaster) locationService.createHierarchyMaster(hierarchyMasterDto).getBody();
        } catch (Exception ex) {
            throw new NullPointerException();
        }
    }

    private LocationMaster addLocationMasterInDatabase(TenantDto tenantDto) {
        try {
            LocationMasterDto locationMasterDto = tenantDto.getLocation();
            if (Objects.isNull(locationMasterDto)) {
                throw new NullPointerException("Location not Present In Data!");
            }
            return (LocationMaster) locationService.createOrUpdate(locationMasterDto).getBody();
        } catch (Exception ex) {
            throw new NullPointerException();
        }
    }

    private void addOrganizationInDatabase(TenantDto tenantDto) {
        Organization organization;
        try {
            organization = parser.parseResource(Organization.class, tenantDto.getOrganization());
            if (Objects.isNull(organization)) {
                throw new NullPointerException("Organization Data Not Present!");
            }
            organizationResourceProvider.createOrganization(organization).getId().getIdPart();
        } catch (Exception ex) {
            throw new NullPointerException();
        }
    }

    private void addRoleForTenant(TenantDto tenantDto) throws KeyStoreException {
        try {
            UserDto userDto = tenantDto.getUser();
            if (Objects.isNull(userDto)) {
                throw new NullPointerException("Role Data Not Present!");
            }
            RoleDto roleDto = new RoleDto();
            roleDto.setRoleName(userDto.getRoleName());
            roleDto.setRoleDescription(CommonConstant.ADMIN_ROLE_DESCRIPTION);
            userService.addRealmRole(roleDto);
        } catch (Exception ex) {
            throw new KeyStoreException();
        }
    }

    private void addUserForTenant(TenantDto tenantDto, TenantConfig tenantConfig) {
        try {
            UserDto userDto = tenantDto.getUser();
            if (Objects.isNull(userDto)) {
                throw new NullPointerException("User Data Not Present!");
            }
            userDto.setFacilityIds(new ArrayList<>());
            userService.addUserForCountry(userDto, tenantConfig.getTenantId());
        } catch (Exception ex) {
            throw new NullPointerException();
        }
    }

    private void addLanguageInTenant(TenantDto tenantDto) {
        try {
            LanguageDto languageDto = new LanguageDto();
            languageDto.setLanguageCode(CommonConstant.ENGLISH);
            languageDto.setLanguageName("English");
            languageDto.setLanguageTranslation(tenantDto.getDefaultLanguage());
            languageService.addOrUpdateLanguageTranslation(languageDto);

//                languageAddDto = tenantDto.getLanguage();
//                if (Objects.nonNull(languageAddDto)) {
//                    TenantContext.setCurrentTenant(tenantConfig.getTenantId());
//                    languageService.createNewLanguageTranslation(languageAddDto);
//                }
        } catch (Exception ex) {
            throw new NullPointerException();
        }
    }

    private void removeUser(TenantDto tenantDto) throws Exception {
        try {
            userService.removeUser(tenantDto.getUser().getEmail());
        } catch (Exception ex) {
            throw new Exception();
        }
    }

    private void removeRole(TenantDto tenantDto) throws Exception {
        try {
            userService.removeRole(tenantDto.getUser().getRoleName());
        } catch (Exception ex) {
            throw new Exception();
        }
    }
}
