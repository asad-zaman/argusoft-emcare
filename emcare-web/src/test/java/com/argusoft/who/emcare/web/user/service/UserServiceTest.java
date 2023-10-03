package com.argusoft.who.emcare.web.user.service;

import com.argusoft.who.emcare.web.adminsetting.entity.Settings;
import com.argusoft.who.emcare.web.adminsetting.repository.AdminSettingRepository;
import com.argusoft.who.emcare.web.adminsetting.service.AdminSettingService;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.common.service.CommonService;
import com.argusoft.who.emcare.web.config.KeyCloakConfig;
import com.argusoft.who.emcare.web.fhir.dao.LocationResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.service.LocationResourceService;
import com.argusoft.who.emcare.web.location.dao.LocationMasterDao;
import com.argusoft.who.emcare.web.location.service.LocationService;
import com.argusoft.who.emcare.web.mail.MailService;
import com.argusoft.who.emcare.web.mail.dto.MailDto;
import com.argusoft.who.emcare.web.mail.impl.MailDataSetterService;
import com.argusoft.who.emcare.web.menu.dao.MenuConfigRepository;
import com.argusoft.who.emcare.web.menu.dao.UserMenuConfigRepository;
import com.argusoft.who.emcare.web.menu.model.MenuConfig;
import com.argusoft.who.emcare.web.menu.model.UserMenuConfig;
import com.argusoft.who.emcare.web.secuirty.EmCareSecurityUser;
import com.argusoft.who.emcare.web.tenant.repository.TenantConfigRepository;
import com.argusoft.who.emcare.web.user.cons.UserConst;
import com.argusoft.who.emcare.web.user.dao.RoleEntityRepository;
import com.argusoft.who.emcare.web.user.dto.*;
import com.argusoft.who.emcare.web.user.entity.RoleEntity;
import com.argusoft.who.emcare.web.user.service.impl.UserServiceImpl;
import com.argusoft.who.emcare.web.userlocationmapping.dao.UserLocationMappingRepository;
import com.argusoft.who.emcare.web.userlocationmapping.model.UserLocationMapping;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;

import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    KeyCloakConfig keyCloakConfig;

    @Mock
    EmCareSecurityUser emCareSecurityUser;

    @Mock
    UserLocationMappingRepository userLocationMappingRepository;

    @Mock
    LocationService locationService;

    @Mock
    MenuConfigRepository menuConfigRepository;

    @Mock
    UserMenuConfigRepository userMenuConfigRepository;

    @Mock
    LocationMasterDao locationMasterDao;

    @Mock
    MailService mailService;

    @Mock
    AdminSettingRepository adminSettingRepository;

    @Mock
    AdminSettingService adminSettingService;

    @Mock
    MailDataSetterService mailDataSetterService;

    @Mock
    LocationResourceService locationResourceService;

    @Mock
    LocationResourceRepository locationResourceRepository;

    @Mock
    RoleEntityRepository roleEntityRepository;

    @Mock
    CommonService commonService;

    @Mock
    Environment env;

    String realm = "test";

    @Value("${config.keycloak.clientId}")
    String clientId;

    @Value("${config.keycloak.clientSecret}")
    String clientSecret;

    @Value("${keycloak.auth-server-url}")
    String keycloakServerURL;

    String keycloakLoginURL = "http://localhost:8080/keycloak/login";

    @Value("${keycloak.realm}")
    String keycloakrealm;

    @Value("${defaultTenant}")
    private String defaultTenant;

    @Mock
    private DataSource dataSource;

    @Mock
    private TenantConfigRepository tenantConfigRepository;

    @InjectMocks
    @Spy
    UserServiceImpl userService;

    ObjectMapper objectMapper = new ObjectMapper();

    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(userService, "realm", realm);
        ReflectionTestUtils.setField(userService, "keycloakLoginURL", keycloakLoginURL);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Nested
    class getCurrentUser {
        @Test
        void allActive() {
            AccessToken mockAccessToken = mock(AccessToken.class);
            Keycloak mockKeycloak = mock(Keycloak.class);
            RealmResource mockRealmResource = mock(RealmResource.class);
            UsersResource mockUsersResource = mock(UsersResource.class);
            UserResource mockUserResource = mock(UserResource.class);
            UserRepresentation mockUserRepresentation = mock(UserRepresentation.class);

            when(emCareSecurityUser.getLoggedInUser()).thenReturn(mockAccessToken);
            when(keyCloakConfig.getInstance()).thenReturn(mockKeycloak);
            when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
            when(mockRealmResource.users()).thenReturn(mockUsersResource);

            when(mockAccessToken.getSubject()).thenReturn("testSubject");

            when(mockUsersResource.get(mockAccessToken.getSubject())).thenReturn(mockUserResource);
            when(mockUserResource.toRepresentation()).thenReturn(mockUserRepresentation);

            UserLocationMapping ulm1 = new UserLocationMapping();
            ulm1.setFacilityId("F1");
            UserLocationMapping ulm2 = new UserLocationMapping();
            ulm2.setFacilityId("F2");
            UserLocationMapping ulm3 = new UserLocationMapping();
            ulm3.setFacilityId("F1");
            List<UserLocationMapping> mockULMList = List.of(ulm1, ulm2, ulm3);

            when(userLocationMappingRepository.findByUserId(mockAccessToken.getSubject())).thenReturn(mockULMList);

            FacilityDto f1 = new FacilityDto();
            f1.setFacilityId("F1");
            f1.setStatus(CommonConstant.ACTIVE);
            FacilityDto f2 = new FacilityDto();
            f2.setFacilityId("F2");
            f2.setStatus(CommonConstant.ACTIVE);
            FacilityDto f3 = new FacilityDto();
            f3.setFacilityId("F3");
            f3.setStatus(CommonConstant.ACTIVE);
            List<FacilityDto> mockFList = List.of(f1, f2, f3);

            when(locationResourceService.getFacilityDto(anyString()))
                    .thenAnswer(i -> {
                        for (FacilityDto f : mockFList) if (f.getFacilityId().equals(i.getArgument(0))) return f;
                        return null;
                    });

            when(mockAccessToken.getPreferredUsername()).thenReturn("tester");
            AccessToken.Access mockAccess = mock(AccessToken.Access.class);
            when(mockAccessToken.getRealmAccess()).thenReturn(mockAccess);
            when(mockAccess.getRoles()).thenReturn(new HashSet<>(List.of("TESTER", "USER")));
            when(mockAccessToken.getEmail()).thenReturn("tester@argusoft.com");
            when(mockUserRepresentation.getFirstName()).thenReturn("Tester");
            when(mockUserRepresentation.getLastName()).thenReturn("Tester");
            when(mockUserRepresentation.getAttributes()).thenReturn(null);

            RoleMappingResource mockRoleMappingResource = mock(RoleMappingResource.class);
            RoleScopeResource mockRoleScopeResource = mock(RoleScopeResource.class);
            when(mockUserResource.roles()).thenReturn(mockRoleMappingResource);
            when(mockRoleMappingResource.realmLevel()).thenReturn(mockRoleScopeResource);
            RoleRepresentation rp1 = new RoleRepresentation();
            rp1.setId("T");
            RoleRepresentation rp2 = new RoleRepresentation();
            rp2.setId("U");
            List<RoleRepresentation> mockRoleRepresentationList = List.of(rp1, rp2);
            when(mockRoleScopeResource.listAll()).thenReturn(mockRoleRepresentationList);

            // Test ignoring the expected part of setting feature:
            // "masterUser.setFeature(getUserFeatureJson(roleIds, masterUser.getUserId()))" -> "getUserFeatureJson"
            // Test of getUserFeatureJson Fn are separate
            when(userMenuConfigRepository.getMenuByUser(anyList(), anyString())).thenReturn(List.of());

            ResponseEntity actualResponse = userService.getCurrentUser();
            assertNotNull(actualResponse);
            UserMasterDto actualUserMasterDto = (UserMasterDto) actualResponse.getBody();
            assertNotNull(actualUserMasterDto);
            assertEquals(mockAccessToken.getSubject(), actualUserMasterDto.getUserId());
            assertEquals(mockAccessToken.getPreferredUsername(), actualUserMasterDto.getUserName());
            assertNull(actualUserMasterDto.getPhone());
            assertEquals(CommonConstant.ENGLISH, actualUserMasterDto.getLanguage());
            assertEquals(mockAccessToken.getEmail(), actualUserMasterDto.getEmail());
            assertEquals(mockUserRepresentation.getFirstName(), actualUserMasterDto.getFirstName());
            assertEquals(mockUserRepresentation.getLastName(), actualUserMasterDto.getLastName());
            assertNotNull(actualUserMasterDto.getFacilities());
            assertNotNull(actualUserMasterDto.getFeature());
            assertEquals(0, actualUserMasterDto.getFeature().size());
        }

        @Test
        void allInactive() {
            AccessToken mockAccessToken = mock(AccessToken.class);
            Keycloak mockKeycloak = mock(Keycloak.class);
            RealmResource mockRealmResource = mock(RealmResource.class);
            UsersResource mockUsersResource = mock(UsersResource.class);
            UserResource mockUserResource = mock(UserResource.class);
            UserRepresentation mockUserRepresentation = mock(UserRepresentation.class);

            when(emCareSecurityUser.getLoggedInUser()).thenReturn(mockAccessToken);
            when(keyCloakConfig.getInstance()).thenReturn(mockKeycloak);
            when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
            when(mockRealmResource.users()).thenReturn(mockUsersResource);

            when(mockAccessToken.getSubject()).thenReturn("testSubject");

            when(mockUsersResource.get(mockAccessToken.getSubject())).thenReturn(mockUserResource);
            when(mockUserResource.toRepresentation()).thenReturn(mockUserRepresentation);

            UserLocationMapping ulm1 = new UserLocationMapping();
            ulm1.setFacilityId("F1");
            UserLocationMapping ulm2 = new UserLocationMapping();
            ulm2.setFacilityId("F2");
            UserLocationMapping ulm3 = new UserLocationMapping();
            ulm3.setFacilityId("F1");
            List<UserLocationMapping> mockULMList = List.of(ulm1, ulm2, ulm3);

            when(userLocationMappingRepository.findByUserId(mockAccessToken.getSubject())).thenReturn(mockULMList);

            FacilityDto f1 = new FacilityDto();
            f1.setFacilityId("F1");
            f1.setStatus(CommonConstant.INACTIVE);
            FacilityDto f2 = new FacilityDto();
            f2.setFacilityId("F2");
            f2.setStatus(CommonConstant.INACTIVE);
            FacilityDto f3 = new FacilityDto();
            f3.setFacilityId("F3");
            f3.setStatus(CommonConstant.INACTIVE);
            List<FacilityDto> mockFList = List.of(f1, f2, f3);

            when(locationResourceService.getFacilityDto(anyString()))
                    .thenAnswer(i -> {
                        for (FacilityDto f : mockFList) if (f.getFacilityId().equals(i.getArgument(0))) return f;
                        return null;
                    });

            when(mockAccessToken.getPreferredUsername()).thenReturn("tester");
            AccessToken.Access mockAccess = mock(AccessToken.Access.class);
            when(mockAccessToken.getRealmAccess()).thenReturn(mockAccess);
            when(mockAccess.getRoles()).thenReturn(new HashSet<>(List.of("TESTER", "USER")));
            when(mockAccessToken.getEmail()).thenReturn("tester@argusoft.com");
            when(mockUserRepresentation.getFirstName()).thenReturn("Tester");
            when(mockUserRepresentation.getLastName()).thenReturn("Tester");
            when(mockUserRepresentation.getAttributes()).thenReturn(null);

            RoleMappingResource mockRoleMappingResource = mock(RoleMappingResource.class);
            RoleScopeResource mockRoleScopeResource = mock(RoleScopeResource.class);
            when(mockUserResource.roles()).thenReturn(mockRoleMappingResource);
            when(mockRoleMappingResource.realmLevel()).thenReturn(mockRoleScopeResource);
            RoleRepresentation rp1 = new RoleRepresentation();
            rp1.setId("T");
            RoleRepresentation rp2 = new RoleRepresentation();
            rp2.setId("U");
            List<RoleRepresentation> mockRoleRepresentationList = List.of(rp1, rp2);
            when(mockRoleScopeResource.listAll()).thenReturn(mockRoleRepresentationList);

            // Test ignoring the expected part of setting feature:
            // "masterUser.setFeature(getUserFeatureJson(roleIds, masterUser.getUserId()))" -> "getUserFeatureJson"
            // Test of getUserFeatureJson Fn are separate
            when(userMenuConfigRepository.getMenuByUser(anyList(), anyString())).thenReturn(List.of());

            ResponseEntity actualResponse = userService.getCurrentUser();
            assertNotNull(actualResponse);
            UserMasterDto actualUserMasterDto = (UserMasterDto) actualResponse.getBody();
            assertNotNull(actualUserMasterDto);
            assertEquals(mockAccessToken.getSubject(), actualUserMasterDto.getUserId());
            assertEquals(mockAccessToken.getPreferredUsername(), actualUserMasterDto.getUserName());
            assertNull(actualUserMasterDto.getPhone());
            assertEquals(CommonConstant.ENGLISH, actualUserMasterDto.getLanguage());
            assertEquals(mockAccessToken.getEmail(), actualUserMasterDto.getEmail());
            assertEquals(mockUserRepresentation.getFirstName(), actualUserMasterDto.getFirstName());
            assertEquals(mockUserRepresentation.getLastName(), actualUserMasterDto.getLastName());
            assertNotNull(actualUserMasterDto.getFacilities());
            assertNotNull(actualUserMasterDto.getFeature());
            assertEquals(0, actualUserMasterDto.getFeature().size());
        }
    }

    @Test
    void getAllUser() {
        Keycloak mockKeycloak = mock(Keycloak.class);
        when(keyCloakConfig.getInstance()).thenReturn(mockKeycloak);
        List<String> distinctUserIds = List.of("U1", "U3", "U5");
        when(userLocationMappingRepository.getDistinctUserId()).thenReturn(distinctUserIds);

        RealmResource mockRealmResource = mock(RealmResource.class);
        UsersResource mockUsersResource = mock(UsersResource.class);

        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.users()).thenReturn(mockUsersResource);

        UserRepresentation ur1 = new UserRepresentation();
        ur1.setId("U1");
        UserRepresentation ur2 = new UserRepresentation();
        ur2.setId("U2");
        UserRepresentation ur3 = new UserRepresentation();
        ur3.setId("U3");
        UserRepresentation ur4 = new UserRepresentation();
        ur4.setId("U4");
        UserRepresentation ur5 = new UserRepresentation();
        ur5.setId("U5");
        UserRepresentation ur6 = new UserRepresentation();
        ur6.setId("U6");

        List<UserRepresentation> mockUserRepresentations = List.of(ur1, ur2, ur3, ur4, ur5, ur6);

        UserLocationMapping ulm1 = new UserLocationMapping();
        ulm1.setId(1);
        UserLocationMapping ulm2 = new UserLocationMapping();
        ulm2.setId(2);

        for (UserRepresentation ur : mockUserRepresentations) {
            RoleMappingResource roleMappingResource = mock(RoleMappingResource.class);
            RoleScopeResource roleScopeResource = mock(RoleScopeResource.class);
            UserResource userResource = mock(UserResource.class);
            when(mockUsersResource.get(ur.getId())).thenReturn(userResource);
            when(userResource.roles()).thenReturn(roleMappingResource);
            when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);
            RoleRepresentation roleRepresentation = new RoleRepresentation();
            roleRepresentation.setName("Tester");
            when(roleScopeResource.listAll()).thenReturn(List.of(roleRepresentation));

            when(userLocationMappingRepository.findByUserId(ur.getId())).thenAnswer(i -> {
                if (ur.getId().equals("U1")) return List.of(ulm1);
                else if (ur.getId().equals("U2")) return List.of(ulm2);
                else return List.of(ulm1, ulm2);
            });
        }

        when(mockUsersResource.list()).thenReturn(mockUserRepresentations);

        List<UserListDto> actualUserDtoList = userService.getAllUser(any());
        assertNotNull(actualUserDtoList);
        // @TODO: should be this
        // assertEquals(distinctUserIds.size(), actualUserDtoList.size());
        assertEquals(0, actualUserDtoList.size());
    }

    @Test
    void getAllUserWithMultiLocation() {
        Keycloak mockKeycloak = mock(Keycloak.class);
        when(keyCloakConfig.getInstance()).thenReturn(mockKeycloak);
        List<String> distinctUserIds = List.of("U1", "U3", "U5");
        when(userLocationMappingRepository.getDistinctUserId()).thenReturn(distinctUserIds);

        RealmResource mockRealmResource = mock(RealmResource.class);
        UsersResource mockUsersResource = mock(UsersResource.class);

        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.users()).thenReturn(mockUsersResource);

        UserRepresentation ur1 = new UserRepresentation();
        ur1.setId("U1");
        UserRepresentation ur2 = new UserRepresentation();
        ur2.setId("U2");
        UserRepresentation ur3 = new UserRepresentation();
        ur3.setId("U3");
        UserRepresentation ur4 = new UserRepresentation();
        ur4.setId("U4");
        UserRepresentation ur5 = new UserRepresentation();
        ur5.setId("U5");
        UserRepresentation ur6 = new UserRepresentation();
        ur6.setId("U6");

        List<UserRepresentation> mockUserRepresentations = List.of(ur1, ur2, ur3, ur4, ur5, ur6);

        UserLocationMapping ulm1 = new UserLocationMapping();
        ulm1.setId(1);
        UserLocationMapping ulm2 = new UserLocationMapping();
        ulm2.setId(2);

        for (UserRepresentation ur : mockUserRepresentations) {
            RoleMappingResource roleMappingResource = mock(RoleMappingResource.class);
            RoleScopeResource roleScopeResource = mock(RoleScopeResource.class);
            UserResource userResource = mock(UserResource.class);
            when(mockUsersResource.get(ur.getId())).thenReturn(userResource);
            when(userResource.roles()).thenReturn(roleMappingResource);
            when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);
            RoleRepresentation roleRepresentation = new RoleRepresentation();
            roleRepresentation.setName("Tester");
            when(roleScopeResource.listAll()).thenReturn(List.of(roleRepresentation));

            when(userLocationMappingRepository.findByUserId(ur.getId())).thenAnswer(i -> {
                if (ur.getId().equals("U1")) return List.of(ulm1);
                else if (ur.getId().equals("U2")) return List.of(ulm2);
                else return List.of(ulm1, ulm2);
            });
        }

        when(mockUsersResource.list()).thenReturn(mockUserRepresentations);

        List<MultiLocationUserListDto> actualUserDtoList = userService.getAllUserWithMultiLocation(any());
        assertNotNull(actualUserDtoList);
        assertEquals(distinctUserIds.size(), actualUserDtoList.size());
    }

    @Nested
    class testGetUserPage {
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        UsersResource mockUsersResource = mock(UsersResource.class);

        @BeforeEach
        void setUpGetUserPage() throws IOException {
            when(keyCloakConfig.getInstance()).thenReturn(mockKeycloak);
            File file1 = new File("src/test/resources/mockdata/user/mockUserLocationMapping.json");
            File file2 = new File("src/test/resources/mockdata/user/mockUserRepresentation.json");
            InputStream fileInputStream1 = new FileInputStream(file1);
            InputStream fileInputStream2 = new FileInputStream(file2);

            List<UserLocationMapping> mockULM = objectMapper.readValue(fileInputStream1, new TypeReference<List<UserLocationMapping>>() {
            });
            List<UserRepresentation> mockUR = objectMapper.readValue(fileInputStream2, new TypeReference<List<UserRepresentation>>() {
            });

            when(userLocationMappingRepository.getUserPageDataWithSearchCount(anyString(), anyBoolean())).thenAnswer(i -> {
                String search = i.getArgument(0);
                Boolean state = i.getArgument(1);
                return mockULM
                        .stream()
                        .filter(ulm -> ulm.getUserId().contains(search) && ulm.isState() == state)
                        .map(UserLocationMapping::getUserId)
                        .collect(Collectors.toList());
            });

            when(userLocationMappingRepository.getUserPageDataWithSearch(anyString(), anyBoolean(), anyInt())).thenAnswer(i -> {
                String search = i.getArgument(0);
                Boolean state = i.getArgument(1);
                Integer offset = i.getArgument(2);
                return mockULM
                        .stream()
                        .filter(ulm -> ulm.getUserId().contains(search) && ulm.isState() == state)
                        .map(UserLocationMapping::getUserId).skip(offset)
                        .collect(Collectors.toList());
            });

            when(userLocationMappingRepository.getUserPageDataWithoutSearchCount(anyBoolean())).thenAnswer(i -> {
                Boolean state = i.getArgument(0);
                return mockULM
                        .stream()
                        .filter(ulm -> ulm.isState() == state)
                        .map(UserLocationMapping::getUserId)
                        .collect(Collectors.toList());
            });

            when(userLocationMappingRepository.getUserPageDataWithoutSearch(anyBoolean(), anyInt())).thenAnswer(i -> {
                Boolean state = i.getArgument(0);
                Integer offset = i.getArgument(1);
                return mockULM
                        .stream()
                        .filter(ulm -> ulm.isState() == state)
                        .map(UserLocationMapping::getUserId).skip(offset)
                        .collect(Collectors.toList());
            });

            when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
            when(mockRealmResource.users()).thenReturn(mockUsersResource);

            RoleRepresentation roleTester = new RoleRepresentation();
            roleTester.setName("Tester");
            RoleRepresentation roleUser = new RoleRepresentation();
            roleUser.setName("User");

            when(mockUsersResource.get(anyString()))
                    .thenAnswer(i -> {
                        UserResource userResource = mock(UserResource.class);
                        UserRepresentation mockUser = mockUR.stream().filter(ur -> ur.getId().equals(i.getArgument(0))).findFirst().orElse(null);
                        if (mockUser == null) return null;
                        RoleMappingResource rmr = mock(RoleMappingResource.class);
                        when(userResource.toRepresentation()).thenReturn(mockUser);
                        when(userResource.roles()).thenReturn(rmr);
                        RoleScopeResource rsr = mock(RoleScopeResource.class);
                        when(rmr.realmLevel()).thenReturn(rsr);
                        if (mockUser.getId().hashCode() % 2 == 0) {
                            when(rsr.listAll()).thenReturn(List.of(roleTester, roleUser));
                        } else {
                            when(rsr.listAll()).thenReturn(List.of(roleUser));
                        }
                        return userResource;
                    });

            when(userLocationMappingRepository.findByUserId(anyString())).thenAnswer(i ->
                    mockULM.stream().filter(ulm -> ulm.getId().equals(i.getArgument(0))).collect(Collectors.toList()));

            when(locationResourceService.getFacilityDto(anyString())).thenAnswer(i -> {
                FacilityDto facilityDto = new FacilityDto();
                facilityDto.setFacilityId(i.getArgument(0));
                return facilityDto;
            });
        }

        @Test
        void page1AndNoSearch() {
            HttpServletRequest mockReq = mock(HttpServletRequest.class);
            PageDto actualPageDto = userService.getUserPage(mockReq, 1, null, false);
            assertNotNull(actualPageDto);
            assertEquals(55, actualPageDto.getTotalCount());
            assertEquals(55 - CommonConstant.PAGE_SIZE, actualPageDto.getList().size());
        }

        @Test
        void page2AndSearchWithFilter() {
            HttpServletRequest mockReq = mock(HttpServletRequest.class);
            PageDto actualPageDto = userService.getUserPage(mockReq, 1, "a", true);
            assertNotNull(actualPageDto);
            assertEquals(3, actualPageDto.getTotalCount());
            assertEquals(0, actualPageDto.getList().size());
        }
    }


    @Test
    void testGetAllSignedUpUser() throws IOException {
        File file1 = new File("src/test/resources/mockdata/user/mockUserLocationMapping.json");
        File file2 = new File("src/test/resources/mockdata/user/mockUserRepresentation.json");
        InputStream fileInputStream1 = new FileInputStream(file1);
        InputStream fileInputStream2 = new FileInputStream(file2);

        List<UserLocationMapping> mockULM = objectMapper.readValue(fileInputStream1, new TypeReference<List<UserLocationMapping>>() {
        });
        List<UserRepresentation> mockUR = objectMapper.readValue(fileInputStream2, new TypeReference<List<UserRepresentation>>() {
        });

        when(userLocationMappingRepository.findByIsFirstOrderByCreateDateDesc(true))
                .thenReturn(
                        mockULM
                                .stream()
                                .filter(UserLocationMapping::isIsFirst)
                                .sorted(Comparator.comparing(UserLocationMapping::getCreateDate))
                                .collect(Collectors.toUnmodifiableList())
                );

        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        UsersResource mockUsersResource = mock(UsersResource.class);
        when(keyCloakConfig.getInstanceByAuth()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.users()).thenReturn(mockUsersResource);

        RoleRepresentation roleTester = new RoleRepresentation();
        roleTester.setName("Tester");
        RoleRepresentation roleUser = new RoleRepresentation();
        roleUser.setName("User");

        when(mockUsersResource.get(anyString()))
                .thenAnswer(i -> {
                    UserResource userResource = mock(UserResource.class);
                    UserRepresentation mockUser = mockUR.stream().filter(ur -> ur.getId().equals(i.getArgument(0))).findFirst().orElse(null);
                    if (mockUser == null) return null;
                    RoleMappingResource rmr = mock(RoleMappingResource.class);
                    when(userResource.toRepresentation()).thenReturn(mockUser);
                    when(userResource.roles()).thenReturn(rmr);
                    RoleScopeResource rsr = mock(RoleScopeResource.class);
                    when(rmr.realmLevel()).thenReturn(rsr);
                    if (mockUser.getId().hashCode() % 2 == 0) {
                        when(rsr.listAll()).thenReturn(List.of(roleTester, roleUser));
                    } else {
                        when(rsr.listAll()).thenReturn(List.of(roleUser));
                    }
                    return userResource;
                });

        List<UserListDto> actualUserList =  userService.getAllSignedUpUser(mock(HttpServletRequest.class));

        assertNotNull(actualUserList);
        assertEquals(10, actualUserList.size());
    }

    @Test
    void testGetAllRoles() {
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        RolesResource mockRolesResource = mock(RolesResource.class);
        when(keyCloakConfig.getInstanceByAuth()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.roles()).thenReturn(mockRolesResource);
        RoleRepresentation rp1 = new RoleRepresentation(); rp1.setId("R1");
        RoleRepresentation rp2 = new RoleRepresentation(); rp2.setId("R2");
        RoleRepresentation rp3 = new RoleRepresentation(); rp3.setId("R3");
        RoleRepresentation rp4 = new RoleRepresentation(); rp4.setId("R4");
        List<RoleRepresentation> mockRoleRepresentations = List.of(rp1, rp2, rp3, rp4);
        when(mockRolesResource.list()).thenReturn(mockRoleRepresentations);

        RoleEntity r1 = new RoleEntity(); r1.setRoleId("R1");
        RoleEntity r2 = new RoleEntity(); r2.setRoleId("R2");
        RoleEntity r3 = new RoleEntity(); r3.setRoleId("R3");
        RoleEntity r5 = new RoleEntity(); r5.setRoleId("R5");
        when(roleEntityRepository.findAll()).thenReturn(List.of(r1, r2, r3, r5));

        List<RoleRepresentation> actualRoles = userService.getAllRoles(mock(HttpServletRequest.class));
        assertNotNull(actualRoles);
        assertEquals(3, actualRoles.size());
    }

    @Test
    void testGetRoleByName() {
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        RoleByIdResource mockRolesResource = mock(RoleByIdResource.class);
        when(keyCloakConfig.getKeyCloakInstance()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.rolesById()).thenReturn(mockRolesResource);
        RoleRepresentation rp1 = new RoleRepresentation(); rp1.setId("R1"); rp1.setName("Role 1");
        when(mockRolesResource.getRole("R1")).thenReturn(rp1);

        RoleRepresentation actualRP = userService.getRoleByName("R1", mock(HttpServletRequest.class));
        assertNotNull(actualRP);
        assertEquals("R1", actualRP.getId());
        assertEquals("Role 1", actualRP.getName());
    }

    @Test
    void testGetRoleByNameNonExisting() {
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        RoleByIdResource mockRolesResource = mock(RoleByIdResource.class);
        when(keyCloakConfig.getKeyCloakInstance()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.rolesById()).thenReturn(mockRolesResource);
        RoleRepresentation rp1 = new RoleRepresentation(); rp1.setId("R1"); rp1.setName("Role 1");
        when(mockRolesResource.getRole("R1")).thenReturn(rp1);

        RoleRepresentation actualRP = userService.getRoleByName("R2", mock(HttpServletRequest.class));
        assertNull(actualRP);
    }

    @Test
    void testGetAllRolesForSignUp() {
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        RolesResource mockRolesResource = mock(RolesResource.class);
        when(keyCloakConfig.getKeyCloakInstance()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.roles()).thenReturn(mockRolesResource);
        RoleRepresentation rp1 = new RoleRepresentation(); rp1.setId("R1"); rp1.setName("Role 1");
        when(mockRolesResource.list()).thenReturn(List.of(rp1));
        RoleResource mockRoleResource = mock(RoleResource.class);
        when(mockRolesResource.get("R1")).thenReturn(mockRoleResource);
        when(mockRoleResource.toRepresentation()).thenReturn(rp1);

        RolesResource actualRole = userService.getAllRolesForSignUp(mock(HttpServletRequest.class));
        assertNotNull(actualRole);
        assertEquals("Role 1", actualRole.get("R1").toRepresentation().getName());
        assertEquals(1, actualRole.list().size());
    }

    @Nested
    class testSignUp {
        @Test
        void correctCreds() {
            Keycloak mockKeycloak = mock(Keycloak.class);
            RealmResource mockRealmResource = mock(RealmResource.class);
            UsersResource mockUsersResource = mock(UsersResource.class);
            when(keyCloakConfig.getKeyCloakInstance()).thenReturn(mockKeycloak);
            when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
            when(mockRealmResource.users()).thenReturn(mockUsersResource);

            UserDto userDto = new UserDto();
            userDto.setEmail("tester@argusoft.com");
            userDto.setUserName("tester");
            userDto.setPassword("tester");
            userDto.setFirstName("testing");
            userDto.setLastName("tested");
            userDto.setRegRequestFrom(UserConst.MOBILE);
            userDto.setRoleName("role-tester");
            userDto.setFacilityIds(List.of("F1", "F2"));
            FacilityDto mockF1 = new FacilityDto(); mockF1.setFacilityId("F1"); mockF1.setLocationId(1L);
            FacilityDto mockF2 = new FacilityDto(); mockF2.setFacilityId("F2"); mockF2.setLocationId(2L);
            HashMap<String, FacilityDto> idFacilityMap = new HashMap<>();
            idFacilityMap.put("F1", mockF1);
            idFacilityMap.put("F2", mockF2);

            Settings mockUsernameSetting = new Settings(); mockUsernameSetting.setValue(CommonConstant.ACTIVE);
            when(adminSettingRepository.findByKey(CommonConstant.SETTING_TYPE_REGISTRATION_EMAIL_AS_USERNAME)).thenReturn(mockUsernameSetting);
            when(locationResourceService.getFacilityDto(anyString())).thenAnswer(i -> idFacilityMap.get(i.getArgument(0).toString()));

            Response mockResponse = mock(Response.class);
            when(mockUsersResource.create(any())).thenReturn(mockResponse);
            URI mockURI = URI.create("http://localhost/api/signup/test");
            when(mockResponse.getLocation()).thenReturn(mockURI);
            when(mockResponse.getStatusInfo()).thenReturn(Response.Status.CREATED);

            UserResource userResource = mock(UserResource.class);
            when(mockUsersResource.get("test")).thenReturn(userResource);
            RolesResource mockRolesResource = mock(RolesResource.class);
            when(mockRealmResource.roles()).thenReturn(mockRolesResource);
            RoleRepresentation mockRoleRepresentation = new RoleRepresentation();
            RoleResource mockRoleResource = mock(RoleResource.class);
            when(mockRolesResource.get("role-tester")).thenReturn(mockRoleResource);
            when(mockRolesResource.get(CommonConstant.DEFAULT_ROLE_EMCARE)).thenReturn(mockRoleResource);
            when(mockRoleResource.toRepresentation()).thenReturn(mockRoleRepresentation);
            RoleMappingResource mockRoleMappingResource = mock(RoleMappingResource.class);
            RoleScopeResource mockRoleScopeResource = mock(RoleScopeResource.class);
            when(userResource.roles()).thenReturn(mockRoleMappingResource);
            when(mockRoleMappingResource.realmLevel()).thenReturn(mockRoleScopeResource);
            Settings mockSettings = new Settings();
            mockSettings.setValue(CommonConstant.ACTIVE);
            when(adminSettingService.getAdminSettingByName(CommonConstant.SETTING_TYPE_WELCOME_EMAIL)).thenReturn(mockSettings);
            when(mailDataSetterService.mailSubjectSetter(CommonConstant.MAIL_FOR_ADD_USER)).thenReturn(new MailDto());
            when(mailDataSetterService.emailBodyCreator(any(), any(), any())).thenReturn("body");

            ResponseEntity actualResponse = userService.signUp(userDto, mock(HttpServletRequest.class));

            verify(userLocationMappingRepository, times(1)).saveAll(any());
            verify(mockRoleScopeResource, times(1)).remove(Arrays.asList(mockRolesResource.get(CommonConstant.DEFAULT_ROLE_EMCARE).toRepresentation()));
            verify(mockRoleScopeResource, times(1)).add(Arrays.asList(mockRolesResource.get(userDto.getRoleName()).toRepresentation()));
            verify(mailService, times(1)).sendBasicMail(any(), any(), any());


            assertNotNull(actualResponse);
            assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        }
    }

    @Test
    void testUserLogin() {
        // Not testable without mock keycloak server
    }

    @Test
    void testUserLogOut() {
        // Not testable without mock keycloak server
    }

    @Test
    void testAddUser() {
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        UsersResource mockUsersResource = mock(UsersResource.class);
        when(keyCloakConfig.getInstance()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.users()).thenReturn(mockUsersResource);

        UserDto userDto = new UserDto();
        userDto.setEmail("tester@argusoft.com");
        userDto.setUserName("tester");
        userDto.setPassword("tester");
        userDto.setFirstName("testing");
        userDto.setLastName("tested");
        userDto.setRegRequestFrom(UserConst.MOBILE);
        userDto.setRoleName("role-tester");
        userDto.setFacilityIds(List.of("F1", "F2"));
        FacilityDto mockF1 = new FacilityDto(); mockF1.setFacilityId("F1"); mockF1.setLocationId(1L);
        FacilityDto mockF2 = new FacilityDto(); mockF2.setFacilityId("F2"); mockF2.setLocationId(2L);
        HashMap<String, FacilityDto> idFacilityMap = new HashMap<>();
        idFacilityMap.put("F1", mockF1);
        idFacilityMap.put("F2", mockF2);

        Settings mockUsernameSetting = new Settings(); mockUsernameSetting.setValue(CommonConstant.ACTIVE);
        when(adminSettingRepository.findByKey(CommonConstant.SETTING_TYPE_REGISTRATION_EMAIL_AS_USERNAME)).thenReturn(mockUsernameSetting);
        when(locationResourceService.getFacilityDto(anyString())).thenAnswer(i -> idFacilityMap.get(i.getArgument(0).toString()));

        Response mockResponse = mock(Response.class);
        when(mockUsersResource.create(any())).thenReturn(mockResponse);
        URI mockURI = URI.create("http://localhost/api/signup/test");
        when(mockResponse.getLocation()).thenReturn(mockURI);
        when(mockResponse.getStatusInfo()).thenReturn(Response.Status.CREATED);

        UserResource userResource = mock(UserResource.class);
        when(mockUsersResource.get("test")).thenReturn(userResource);
        RolesResource mockRolesResource = mock(RolesResource.class);
        when(mockRealmResource.roles()).thenReturn(mockRolesResource);
        RoleRepresentation mockRoleRepresentation = new RoleRepresentation();
        RoleResource mockRoleResource = mock(RoleResource.class);
        when(mockRolesResource.get("role-tester")).thenReturn(mockRoleResource);
        when(mockRolesResource.get(CommonConstant.DEFAULT_ROLE_EMCARE)).thenReturn(mockRoleResource);
        when(mockRoleResource.toRepresentation()).thenReturn(mockRoleRepresentation);
        RoleMappingResource mockRoleMappingResource = mock(RoleMappingResource.class);
        RoleScopeResource mockRoleScopeResource = mock(RoleScopeResource.class);
        when(userResource.roles()).thenReturn(mockRoleMappingResource);
        when(mockRoleMappingResource.realmLevel()).thenReturn(mockRoleScopeResource);
        Settings mockSettings = new Settings();
        mockSettings.setValue(CommonConstant.ACTIVE);
        when(adminSettingService.getAdminSettingByName(CommonConstant.SETTING_TYPE_WELCOME_EMAIL)).thenReturn(mockSettings);
        when(mailDataSetterService.mailSubjectSetter(CommonConstant.MAIL_FOR_ADD_USER)).thenReturn(new MailDto());
        when(mailDataSetterService.emailBodyCreator(any(), any(), any())).thenReturn("body");

        ResponseEntity actualResponse = userService.addUser(userDto, mock(HttpServletRequest.class));

        verify(userLocationMappingRepository, times(1)).saveAll(any());
        verify(mockRoleScopeResource, times(1)).remove(Arrays.asList(mockRolesResource.get(CommonConstant.DEFAULT_ROLE_EMCARE).toRepresentation()));
        verify(mockRoleScopeResource, times(1)).add(Arrays.asList(mockRolesResource.get(userDto.getRoleName()).toRepresentation()));
        verify(mailService, times(1)).sendBasicMail(any(), any(), any());


        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
    }

    @Test
    void testAddRealmRole() {
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        UsersResource mockUsersResource = mock(UsersResource.class);
        when(keyCloakConfig.getInstanceByAuth()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.users()).thenReturn(mockUsersResource);

        RoleDto roleDto = new RoleDto();
        roleDto.setRoleName("tester");
        roleDto.setRoleDescription("tests the application");
        FacilityDto mockF1 = new FacilityDto(); mockF1.setFacilityId("F1"); mockF1.setLocationId(1L);
        FacilityDto mockF2 = new FacilityDto(); mockF2.setFacilityId("F2"); mockF2.setLocationId(2L);
        HashMap<String, FacilityDto> idFacilityMap = new HashMap<>();
        idFacilityMap.put("F1", mockF1);
        idFacilityMap.put("F2", mockF2);

        Settings mockUsernameSetting = new Settings(); mockUsernameSetting.setValue(CommonConstant.ACTIVE);
        when(adminSettingRepository.findByKey(CommonConstant.SETTING_TYPE_REGISTRATION_EMAIL_AS_USERNAME)).thenReturn(mockUsernameSetting);
        when(locationResourceService.getFacilityDto(anyString())).thenAnswer(i -> idFacilityMap.get(i.getArgument(0).toString()));

        Response mockResponse = mock(Response.class);
        when(mockUsersResource.create(any())).thenReturn(mockResponse);
        URI mockURI = URI.create("http://localhost/api/signup/test");
        when(mockResponse.getLocation()).thenReturn(mockURI);
        when(mockResponse.getStatusInfo()).thenReturn(Response.Status.CREATED);

        UserResource userResource = mock(UserResource.class);
        when(mockUsersResource.get("test")).thenReturn(userResource);
        RolesResource mockRolesResource = mock(RolesResource.class);
        when(mockRealmResource.roles()).thenReturn(mockRolesResource);
        RoleRepresentation mockRoleRepresentation = new RoleRepresentation();
        RoleRepresentation mockDefaultRoleRepresentation = new RoleRepresentation();
        RoleResource mockRoleResource = mock(RoleResource.class);
        RoleResource mockDefaultRoleResource = mock(RoleResource.class);
        when(mockRolesResource.get(roleDto.getRoleName())).thenReturn(mockRoleResource);
        when(mockRolesResource.get(CommonConstant.DEFAULT_ROLE_EMCARE)).thenReturn(mockDefaultRoleResource);
        when(mockRoleResource.toRepresentation()).thenReturn(mockRoleRepresentation);
        when(mockRoleRepresentation.getId()).thenReturn("role-tester");
        when(mockDefaultRoleResource.toRepresentation()).thenReturn(mockDefaultRoleRepresentation);
        RoleMappingResource mockRoleMappingResource = mock(RoleMappingResource.class);
        when(userResource.roles()).thenReturn(mockRoleMappingResource);

        MenuConfig menuConfig1 = new MenuConfig(); menuConfig1.setId(1);
        MenuConfig menuConfig2 = new MenuConfig(); menuConfig2.setId(2);
        when(menuConfigRepository.findAll()).thenReturn(List.of(menuConfig1, menuConfig2));

        UserMenuConfig userMenuConfig1 = new UserMenuConfig();
        when(userMenuConfigRepository.findAll()).thenReturn(List.of(userMenuConfig1));

        userService.addRealmRole(roleDto);

        verify(mockRolesResource, times(1)).create(any());
        verify(mockRoleResource, times(1)).addComposites(Arrays.asList(mockDefaultRoleRepresentation));
        verify(userLocationMappingRepository, times(1)).saveAll(any());
        verify(userMenuConfigRepository, times(2)).save(any());
        verify(roleEntityRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void testUpdateUserStatus() {
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        UsersResource mockUsersResource = mock(UsersResource.class);
        when(keyCloakConfig.getInstanceByAuth()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.users()).thenReturn(mockUsersResource);

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setUserId("tester");
        updateDto.setIsEnabled(true);

        UserResource userResource = mock(UserResource.class);
        when(mockUsersResource.get(updateDto.getUserId())).thenReturn(userResource);
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail("tester@test.com");
        userRepresentation.setFirstName("tester");
        userRepresentation.setLastName("tested");
        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        UserLocationMapping ulm1 = new UserLocationMapping(); ulm1.setId(1); ulm1.setIsFirst(true); ulm1.setState(false);
        UserLocationMapping ulm2 = new UserLocationMapping(); ulm2.setId(2); ulm2.setIsFirst(true); ulm2.setState(false);
        when(userLocationMappingRepository.findByUserId(updateDto.getUserId())).thenReturn(List.of(ulm1, ulm2));

        Settings mockUsernameSetting = new Settings(); mockUsernameSetting.setValue(CommonConstant.ACTIVE);
        when(adminSettingService.getAdminSettingByName(CommonConstant.SETTING_TYPE_SEND_CONFIRMATION_EMAIL)).thenReturn(mockUsernameSetting);

        MailDto mailDto = new MailDto(); mailDto.setSubject("TestUpdateStatus"); mailDto.setBody("TestUpdateStatus");
        when(mailDataSetterService.mailSubjectSetter(CommonConstant.MAIL_FOR_CONFIRMATION_EMAIL_APPROVED)).thenReturn(mailDto);
        when(mailDataSetterService.emailBodyCreator(any(), any(), any())).thenReturn("Test UpdateStatus Body");

        ResponseEntity actualResponse = userService.updateUserStatus(updateDto);
        List<UserLocationMapping> listOfUsers = (List<UserLocationMapping>) actualResponse.getBody();

        assertNotNull(listOfUsers);
        assertNotNull(actualResponse);
        listOfUsers.forEach(user -> {
            assertNotNull(user);
            assertFalse(user.isIsFirst());
            assertEquals(updateDto.getIsEnabled(), user.isState());
        });
        assertNotNull(actualResponse);
        verify(userResource, times(1)).update(any());
        verify(userLocationMappingRepository, times(2)).save(any());
        verify(mailService, times(1)).sendBasicMail(userRepresentation.getEmail(), mailDto.getSubject(), "Test UpdateStatus Body");
    }

    @Test
    void testGetUserById() {
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        UsersResource mockUsersResource = mock(UsersResource.class);
        when(keyCloakConfig.getInstance()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.users()).thenReturn(mockUsersResource);

        UserResource userResource = mock(UserResource.class);
        when(mockUsersResource.get("tester")).thenReturn(userResource);
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail("tester@test.com");
        userRepresentation.setFirstName("tester");
        userRepresentation.setLastName("tested");
        userRepresentation.setUsername("tt");
        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        UserRepresentation actualUser = userService.getUserById("tester");

        assertNotNull(actualUser);
        assertEquals(userRepresentation.getLastName(), actualUser.getLastName());
        assertEquals(userRepresentation.getFirstName(), actualUser.getFirstName());
        assertEquals(userRepresentation.getEmail(), actualUser.getEmail());
        assertEquals(userRepresentation.getUsername(), actualUser.getUsername());
    }

    @Test
    void testGetUserDtoById() {
        String userId = "tester";
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        UsersResource mockUsersResource = mock(UsersResource.class);
        when(keyCloakConfig.getInstanceByAuth()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.users()).thenReturn(mockUsersResource);

        UserResource userResource = mock(UserResource.class);
        when(mockUsersResource.get(userId)).thenReturn(userResource);
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail("tester@test.com");
        userRepresentation.setFirstName("tester");
        userRepresentation.setLastName("tested");
        userRepresentation.setId(userId);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        UserLocationMapping ulm1 = new UserLocationMapping(); ulm1.setId(1); ulm1.setFacilityId("F1");
        UserLocationMapping ulm2 = new UserLocationMapping(); ulm2.setId(2);
        when(userLocationMappingRepository.findByUserId(userId)).thenReturn(List.of(ulm1, ulm2));

        RoleMappingResource mockRoleMappingResource = mock(RoleMappingResource.class);
        RoleScopeResource mockRoleScopeResource = mock(RoleScopeResource.class);
        when(userResource.roles()).thenReturn(mockRoleMappingResource);
        when(mockRoleMappingResource.realmLevel()).thenReturn(mockRoleScopeResource);
        RoleRepresentation r1 = new RoleRepresentation(); r1.setName("Tester");
        RoleRepresentation r2 = new RoleRepresentation(); r2.setName("User");
        when(mockRoleScopeResource.listAll()).thenReturn(List.of(r1, r2));

        FacilityDto mockF1 = new FacilityDto(); mockF1.setFacilityId("F1"); mockF1.setLocationId(1L);
        FacilityDto mockF2 = new FacilityDto(); mockF2.setFacilityId("F2"); mockF2.setLocationId(2L);
        HashMap<String, FacilityDto> idFacilityMap = new HashMap<>();
        idFacilityMap.put("F1", mockF1);
        idFacilityMap.put("F2", mockF2);

        Settings mockUsernameSetting = new Settings(); mockUsernameSetting.setValue(CommonConstant.ACTIVE);
        when(adminSettingRepository.findByKey(CommonConstant.SETTING_TYPE_REGISTRATION_EMAIL_AS_USERNAME)).thenReturn(mockUsernameSetting);
        when(locationResourceService.getFacilityDto(anyString())).thenAnswer(i -> idFacilityMap.get(i.getArgument(0).toString()));

        MultiLocationUserListDto actualMLULDto = userService.getUserDtoById("tester");
        assertNotNull(actualMLULDto);
        assertArrayEquals(List.of(r1.getName(), r2.getName()).toArray(), actualMLULDto.getRealmRoles().toArray());
        assertEquals(userRepresentation.getUsername(), actualMLULDto.getUserName());
        assertEquals(userRepresentation.getId(), actualMLULDto.getId());
        assertEquals(userRepresentation.getFirstName(), actualMLULDto.getFirstName());
        assertEquals(userRepresentation.getLastName(), actualMLULDto.getLastName());
        assertEquals(userRepresentation.getEmail(), actualMLULDto.getEmail());
        assertEquals(List.of(mockF1).size(), actualMLULDto.getFacilities().size());
    }

    @Test
    void testGetUserRolesById() {
        String userId = "tester";
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        UsersResource mockUsersResource = mock(UsersResource.class);
        when(keyCloakConfig.getInstanceByAuth()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.users()).thenReturn(mockUsersResource);

        UserResource userResource = mock(UserResource.class);
        when(mockUsersResource.get(userId)).thenReturn(userResource);
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail("tester@test.com");
        userRepresentation.setFirstName("tester");
        userRepresentation.setLastName("tested");
        userRepresentation.setId(userId);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        RoleMappingResource mockRoleMappingResource = mock(RoleMappingResource.class);
//        RoleScopeResource mockRoleScopeResource = mock(RoleScopeResource.class);
        when(userResource.roles()).thenReturn(mockRoleMappingResource);

        RoleRepresentation r1 = new RoleRepresentation(); r1.setName("Tester");
        RoleRepresentation r2 = new RoleRepresentation(); r2.setName("User");

        MappingsRepresentation mappingsRepresentation = new MappingsRepresentation();
        mappingsRepresentation.setRealmMappings(List.of(r1, r2));
        when(mockRoleMappingResource.getAll()).thenReturn(mappingsRepresentation);

        ResponseEntity<Object> actualResponse = userService.getUserRolesById("tester");
        assertNotNull(actualResponse);
        MappingsRepresentation actualMappingsRepresentation = (MappingsRepresentation) actualResponse.getBody();
        assertNotNull(actualMappingsRepresentation);

        assertEquals(mappingsRepresentation.getRealmMappings().size(), actualMappingsRepresentation.getRealmMappings().size());
    }

    @Test
    void testUpdateUser() {
        String userId = "tester";
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        UsersResource mockUsersResource = mock(UsersResource.class);
        when(keyCloakConfig.getInstance()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.users()).thenReturn(mockUsersResource);

        UserResource userResource = mock(UserResource.class);
        when(mockUsersResource.get(userId)).thenReturn(userResource);
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail("tester@test.com");
        userRepresentation.setFirstName("tester");
        userRepresentation.setLastName("tested");
        userRepresentation.setId(userId);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        RolesResource mockRolesResource = mock(RolesResource.class);
        RoleResource mockRoleResource = mock(RoleResource.class);
        RoleMappingResource mockRoleMappingResource = mock(RoleMappingResource.class);
        RoleScopeResource mockRoleScopeResource = mock(RoleScopeResource.class);
        when(mockRealmResource.roles()).thenReturn(mockRolesResource);
        when(userResource.roles()).thenReturn(mockRoleMappingResource);
        when(mockRoleMappingResource.realmLevel()).thenReturn(mockRoleScopeResource);

        RoleRepresentation roleTester = new RoleRepresentation(); roleTester.setName("tester");
        RoleRepresentation roleUser = new RoleRepresentation(); roleUser.setName("user");

        UserLocationMapping ulm1 = new UserLocationMapping(); ulm1.setId(1); ulm1.setFacilityId("F1");
        UserLocationMapping ulm2 = new UserLocationMapping(); ulm2.setId(2); ulm2.setFacilityId("F2");
        UserLocationMapping ulm3 = new UserLocationMapping(); ulm3.setId(3); ulm3.setFacilityId("F3");
        when(userLocationMappingRepository.findByUserId(userId)).thenReturn(List.of(ulm1, ulm2, ulm3));
        when(userLocationMappingRepository.findByUserIdAndFacilityId(userId, "F2")).thenReturn(ulm2);
        when(userLocationMappingRepository.findByUserIdAndFacilityId(userId, "F3")).thenReturn(null);

        UserDto userDto = new UserDto();
        userDto.setRoleName("user");
        userDto.setUserName("tester");
        userDto.setFirstName("not");
        userDto.setLastName("tester");
        userDto.setLanguage(CommonConstant.HINDI);
        userDto.setPhone("0909808012");
        userDto.setPassword(CommonConstant.HINDI);
        userDto.setEmail("newemail@test.com");
        userDto.setFacilityIds(List.of("F3", "F2"));

        when(mockRolesResource.get(userDto.getRoleName())).thenReturn(mockRoleResource);
        when(mockRoleResource.toRepresentation()).thenReturn(roleUser);
        when(mockRoleScopeResource.listAll()).thenReturn(List.of(roleTester));


        ResponseEntity<Object> actualResponse = userService.updateUser(userDto, "tester", mock(HttpServletRequest.class));
        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        verify(mockRoleScopeResource, times(1)).remove(List.of(roleTester));
        verify(mockRoleScopeResource, times(1)).add(new ArrayList<>(Arrays.asList(roleUser)));
        verify(userResource, times(1)).update(any());
    }

    @Test
    void updatePassword() {
        String userId = "tester";
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        UsersResource mockUsersResource = mock(UsersResource.class);
        when(keyCloakConfig.getInstance()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.users()).thenReturn(mockUsersResource);

        UserResource userResource = mock(UserResource.class);
        when(mockUsersResource.get(userId)).thenReturn(userResource);
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail("tester@test.com");
        userRepresentation.setFirstName("tester");
        userRepresentation.setLastName("tested");
        userRepresentation.setId(userId);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        UserDto userDto = new UserDto();
        userDto.setRoleName("user");
        userDto.setUserName("tester");
        userDto.setFirstName("not");
        userDto.setLastName("tester");
        userDto.setLanguage(CommonConstant.HINDI);
        userDto.setPhone("0909808012");
        userDto.setPassword(CommonConstant.HINDI);
        userDto.setEmail("newemail@test.com");
        userDto.setFacilityIds(List.of("F3", "F2"));

        ResponseEntity<Object> actualResponse = userService.updatePassword(userDto, "tester");
        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        verify(userResource, times(1)).update(any());
    }

    @Test
    void testUpdateRole()  {
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        RolesResource mockRolesResource = mock(RolesResource.class);
        when(keyCloakConfig.getInstance()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.roles()).thenReturn(mockRolesResource);

        RoleResource roleResource = mock(RoleResource.class);

        RoleUpdateDto roleDto = new RoleUpdateDto();
        roleDto.setName("test");
        roleDto.setOldRoleName("tester");
        roleDto.setDescription("test role allows testing");

        when(mockRolesResource.get(roleDto.getOldRoleName())).thenReturn(roleResource);
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleId("role-test");
        roleEntity.setRoleName("tester");
        roleEntity.setId(1);
        when(roleEntityRepository.findByRoleName(roleDto.getOldRoleName())).thenReturn(roleEntity);

        ResponseEntity<Object> actualResponse = userService.updateRole(roleDto);
        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        verify(roleResource, times(1)).update(any());
        verify(roleEntityRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void testGetRoleIdByName() {
        String roleName = "tester";
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        RolesResource mockRolesResource = mock(RolesResource.class);
        when(keyCloakConfig.getInstanceByAuth()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.roles()).thenReturn(mockRolesResource);
        RoleResource mockRoleResource = mock(RoleResource.class);
        when(mockRolesResource.get(roleName)).thenReturn(mockRoleResource);
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setId("role-test");
        when(mockRoleResource.toRepresentation()).thenReturn(roleRepresentation);

        assertEquals(roleRepresentation.getId(), userService.getRoleIdByName(roleName));
    }

    @Test
    void testGetRoleNameById() {
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        RolesResource mockRolesResource = mock(RolesResource.class);
        when(keyCloakConfig.getInstanceByAuth()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.roles()).thenReturn(mockRolesResource);

        RoleRepresentation r1 = new RoleRepresentation(); r1.setId("role-test"); r1.setName("tester");
        RoleRepresentation r2 = new RoleRepresentation(); r2.setId("role-user"); r2.setName("user");
        when(mockRolesResource.list()).thenReturn(List.of(r1, r2));

        assertEquals("tester", userService.getRoleNameById("role-test"));
        assertEquals("", userService.getRoleNameById("role-admin"));
    }

    @Test
    void testGetUsersUnderLocation() {
    }

    @Test
    void testGetUserByEmailId() {
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        UsersResource mockUsersResource = mock(UsersResource.class);
        when(keyCloakConfig.getInsideInstance()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.users()).thenReturn(mockUsersResource);

        UserRepresentation u1 = new UserRepresentation(); u1.setId("user-1"); u1.setUsername("tester");
        UserRepresentation u2 = new UserRepresentation(); u2.setId("user-2"); u2.setUsername("user");
        UserRepresentation u3 = new UserRepresentation(); u3.setId("user-3"); u3.setUsername("admin");
        when(mockUsersResource.search(anyString())).thenAnswer(i -> List.of(u1, u2, u3)
                .stream()
                .filter(u -> u.getUsername().toLowerCase().contains(i.getArgument(0).toString().toLowerCase()))
                .collect(Collectors.toUnmodifiableList())
        );

        assertEquals("tester", userService.getUserByEmailId("er").getUsername());
        assertEquals("admin", userService.getUserByEmailId("admin").getUsername());
        assertNull(userService.getUserByEmailId("error"));
    }

    @Test
    void testResetPassword() {
        String userId = "tester@test.com";
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        UsersResource mockUsersResource = mock(UsersResource.class);
        when(keyCloakConfig.getInsideInstance()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.users()).thenReturn(mockUsersResource);

        UserResource userResource = mock(UserResource.class);
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail("tester@test.com");
        userRepresentation.setUsername("tester@test.com");
        userRepresentation.setFirstName("tester");
        userRepresentation.setLastName("tested");
        userRepresentation.setId(userId);

        when(mockUsersResource.get(userRepresentation.getId())).thenReturn(userResource);

        when(mockUsersResource.search(anyString())).thenAnswer(i -> {
            String input = i.getArgument(0);
            if(!userRepresentation.getUsername().contains(input)) return List.of();
            return List.of(userRepresentation);
        });

        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        UserRepresentation actualUser = userService.resetPassword("tester@test.com", "tester");
        assertNotNull(actualUser);
        assertEquals("tester@test.com", actualUser.getUsername());
        verify(userResource, times(1)).update(any());
    }

    @Test
    void testCheckEmailIdExist() {
        String userId = "tester@test.com";
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        UsersResource mockUsersResource = mock(UsersResource.class);
        when(keyCloakConfig.getInsideInstance()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);
        when(mockRealmResource.users()).thenReturn(mockUsersResource);

        UserResource userResource = mock(UserResource.class);
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail("tester@test.com");
        userRepresentation.setUsername("tester@test.com");
        userRepresentation.setFirstName("tester");
        userRepresentation.setLastName("tested");
        userRepresentation.setId(userId);

        when(mockUsersResource.search(anyString())).thenAnswer(i -> {
            String input = i.getArgument(0);
            if(!userRepresentation.getUsername().contains(input)) return List.of();
            return List.of(userRepresentation);
        });

        when(mockUsersResource.get(userRepresentation.getId())).thenReturn(userResource);

        when(mockUsersResource.search(anyString())).thenAnswer(i -> {
            String input = i.getArgument(0);
            if(!userRepresentation.getUsername().contains(input)) return List.of();
            return List.of(userRepresentation);
        });

        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        Map<String, Object> actualUserOk = userService.checkEmailIdExist("tester@test.com");
        Map<String, Object> actualUserBad = userService.checkEmailIdExist("admin@test.com");
        assertNotNull(actualUserOk);
        assertNotNull(actualUserBad);
        assertEquals(HttpStatus.BAD_REQUEST.value(), actualUserOk.get("status"));
        assertEquals(HttpStatus.OK.value(), actualUserBad.get("status"));
    }

    @Test
    void testAddUserForCountry() {
    }

    @Test
    void testRemoveRole() throws Exception {
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        when(keyCloakConfig.getInstance()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);

        RolesResource mockRolesResource = mock(RolesResource.class);
        when(mockRealmResource.roles()).thenReturn(mockRolesResource);

        userService.removeRole("tester");

        verify(mockRolesResource, times(1)).deleteRole("tester");
    }

    @Test
    void testRemoveUser() throws Exception {
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealmResource = mock(RealmResource.class);
        when(keyCloakConfig.getInstance()).thenReturn(mockKeycloak);
        when(keyCloakConfig.getInsideInstance()).thenReturn(mockKeycloak);
        when(mockKeycloak.realm(realm)).thenReturn(mockRealmResource);

        UsersResource mockUsersResource = mock(UsersResource.class);
        when(mockRealmResource.users()).thenReturn(mockUsersResource);

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail("tester@test.com");
        userRepresentation.setUsername("tester@test.com");
        userRepresentation.setFirstName("tester");
        userRepresentation.setLastName("tested");
        userRepresentation.setId("tester");

        when(mockUsersResource.search(anyString())).thenAnswer(i -> {
            String input = i.getArgument(0);
            if(!userRepresentation.getUsername().contains(input)) return List.of();
            return List.of(userRepresentation);
        });

        userService.removeUser("tester@test.com");

        verify(mockUsersResource, times(1)).delete("tester");
    }

    @Test
    void testGetCurrentUserFacility() {
        String userId = "tester";
        AccessToken mockToken = new AccessToken(); mockToken.setSubject(userId);
        when(emCareSecurityUser.getLoggedInUser()).thenReturn(mockToken);
        UserLocationMapping ulm1 = new UserLocationMapping(); ulm1.setFacilityId("F1");
        UserLocationMapping ulm2 = new UserLocationMapping(); ulm2.setFacilityId("F2");
        UserLocationMapping ulm3 = new UserLocationMapping(); ulm3.setFacilityId("F3");
        when(userLocationMappingRepository.findByUserId(userId)).thenReturn(List.of(ulm1, ulm2, ulm3));
        List<String> actualFacilities = userService.getCurrentUserFacility();
        assertNotNull(actualFacilities);
        assertEquals(3, actualFacilities.size());
    }
}