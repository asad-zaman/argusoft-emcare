package com.argusoft.who.emcare.web.user.service.impl;

import com.argusoft.who.emcare.web.adminsetting.entity.Settings;
import com.argusoft.who.emcare.web.adminsetting.repository.AdminSettingRepository;
import com.argusoft.who.emcare.web.adminsetting.service.AdminSettingService;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.common.response.Response;
import com.argusoft.who.emcare.web.common.service.CommonService;
import com.argusoft.who.emcare.web.config.KeyCloakConfig;
import com.argusoft.who.emcare.web.config.tenant.TenantContext;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.service.LocationResourceService;
import com.argusoft.who.emcare.web.location.dao.LocationMasterDao;
import com.argusoft.who.emcare.web.location.service.LocationService;
import com.argusoft.who.emcare.web.mail.MailService;
import com.argusoft.who.emcare.web.mail.dto.MailDto;
import com.argusoft.who.emcare.web.mail.impl.MailDataSetterService;
import com.argusoft.who.emcare.web.menu.dao.MenuConfigRepository;
import com.argusoft.who.emcare.web.menu.dao.UserMenuConfigRepository;
import com.argusoft.who.emcare.web.menu.dto.CurrentUserFeatureJson;
import com.argusoft.who.emcare.web.menu.dto.FeatureJSON;
import com.argusoft.who.emcare.web.menu.dto.UserFeatureJson;
import com.argusoft.who.emcare.web.menu.mapper.MenuConfigMapper;
import com.argusoft.who.emcare.web.menu.model.MenuConfig;
import com.argusoft.who.emcare.web.menu.model.UserMenuConfig;
import com.argusoft.who.emcare.web.secuirty.EmCareSecurityUser;
import com.argusoft.who.emcare.web.tenant.repository.TenantConfigRepository;
import com.argusoft.who.emcare.web.user.cons.UserConst;
import com.argusoft.who.emcare.web.user.dao.RoleEntityRepository;
import com.argusoft.who.emcare.web.user.dto.*;
import com.argusoft.who.emcare.web.user.entity.RoleEntity;
import com.argusoft.who.emcare.web.user.mapper.UserMapper;
import com.argusoft.who.emcare.web.user.service.UserService;
import com.argusoft.who.emcare.web.userlocationmapping.dao.UserLocationMappingRepository;
import com.argusoft.who.emcare.web.userlocationmapping.model.UserLocationMapping;
import com.google.gson.Gson;
import org.keycloak.TokenVerifier;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author jay
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    KeyCloakConfig keyCloakConfig;

    @Autowired
    EmCareSecurityUser emCareSecurityUser;

    @Autowired
    UserLocationMappingRepository userLocationMappingRepository;

    @Autowired
    LocationService locationService;

    @Autowired
    MenuConfigRepository menuConfigRepository;

    @Autowired
    UserMenuConfigRepository userMenuConfigRepository;

    @Autowired
    LocationMasterDao locationMasterDao;

    @Autowired
    MailService mailService;

    @Autowired
    AdminSettingRepository adminSettingRepository;

    @Autowired
    AdminSettingService adminSettingService;

    @Autowired
    MailDataSetterService mailDataSetterService;

    @Autowired
    LocationResourceService locationResourceService;

    @Autowired
    RoleEntityRepository roleEntityRepository;

    @Autowired
    CommonService commonService;

    @Autowired
    Environment env;

    @Value("${keycloak.realm}")
    String realm;

    @Value("${config.keycloak.clientId}")
    String clientId;

    @Value("${config.keycloak.clientSecret}")
    String clientSecret;

    @Value("${config.keycloak.login-server-url}")
    String keycloakLoginURL;

    @Value("${defaultTenant}")
    private String defaultTenant;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TenantConfigRepository tenantConfigRepository;

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    @Override
    public UserMasterDto getCurrentUser() {
        AccessToken user = emCareSecurityUser.getLoggedInUser();
        Keycloak keycloak = keyCloakConfig.getInstance();
        UserRepresentation userInfo = keycloak.realm(realm).users().get(user.getSubject()).toRepresentation();
        List<UserLocationMapping> userLocationList = userLocationMappingRepository.findByUserId(user.getSubject());
        List<UserLocationMapping> fecilitys;
        List<FacilityDto> facilityDtos = new ArrayList<>();
        if (!userLocationList.isEmpty()) {
            fecilitys = userLocationMappingRepository.findByUserId(user.getSubject());
            Iterable<String> facilityIds = fecilitys.stream().map(UserLocationMapping::getFacilityId).collect(Collectors.toList());
            for (String id : facilityIds)
                facilityDtos.add(locationResourceService.getFacilityDto(id));
        }
        UserMasterDto masterUser = UserMapper.getMasterUser(user, facilityDtos, userInfo);
        List<RoleRepresentation> roleRepresentationList = keycloak.realm(realm).users().get(masterUser.getUserId()).roles().realmLevel().listAll();
        List<String> roleIds = new ArrayList<>();
        for (RoleRepresentation role : roleRepresentationList) {
            roleIds.add(role.getId());
        }

        masterUser.setFeature(getUserFeatureJson(roleIds, masterUser.getUserId()));
        return masterUser;
    }

    @Override
    public List<UserListDto> getAllUser(HttpServletRequest request) {
        List<UserListDto> userList = new ArrayList<>();
        Keycloak keycloak = keyCloakConfig.getInstance();
        List<String> countryUsers = userLocationMappingRepository.getDistinctUserId();
        List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().list();
        userRepresentations = userRepresentations.stream().filter(userRepresentation -> countryUsers.contains(userRepresentation.getId())).collect(Collectors.toList());
        for (UserRepresentation representation : userRepresentations) {
            List<RoleRepresentation> roleRepresentationList = keycloak.realm(realm).users().get(representation.getId()).roles().realmLevel().listAll();
            List<String> roles = new ArrayList<>();
            for (RoleRepresentation roleRepresentation : roleRepresentationList) {
                roles.add(roleRepresentation.getName());
            }
            representation.setRealmRoles(roles);
        }
        for (UserRepresentation representation : userRepresentations) {
            userLocationMappingRepository.findByUserId(representation.getId());

        }
        return userList;
    }

    @Override
    public List<MultiLocationUserListDto> getAllUserWithMultiLocation(HttpServletRequest request) {
        List<MultiLocationUserListDto> userList = new ArrayList<>();
        Keycloak keycloak = keyCloakConfig.getInstance();
        List<String> countryUsers = userLocationMappingRepository.getDistinctUserId();
        List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().list();
        userRepresentations = userRepresentations.stream().filter(userRepresentation -> countryUsers.contains(userRepresentation.getId())).collect(Collectors.toList());
        for (UserRepresentation representation : userRepresentations) {
            List<RoleRepresentation> roleRepresentationList = keycloak.realm(realm).users().get(representation.getId()).roles().realmLevel().listAll();
            List<String> roles = new ArrayList<>();
            for (RoleRepresentation roleRepresentation : roleRepresentationList) {
                roles.add(roleRepresentation.getName());
            }
            representation.setRealmRoles(roles);
        }
        for (UserRepresentation representation : userRepresentations) {
            List<UserLocationMapping> userLocation = userLocationMappingRepository.findByUserId(representation.getId());
            List<FacilityDto> facilityDtos;
            if (!userLocation.isEmpty()) {
                facilityDtos = new ArrayList<>();
                for (UserLocationMapping mapping : userLocation) {
                    if (mapping.getFacilityId() != null) {
                        facilityDtos.add(locationResourceService.getFacilityDto(mapping.getFacilityId()));
                    }
                }
                userList.add(UserMapper.getMultiLocationUserListDto(representation, facilityDtos));
            } else {
                userList.add(UserMapper.getMultiLocationUserListDto(representation, null));
            }
        }
        return userList;
    }

    @Override
    public PageDto getUserPage(HttpServletRequest request, Integer pageNo, String searchString) {
        Integer pageSize = CommonConstant.PAGE_SIZE;
        Integer startIndex = pageNo * pageSize;
        Integer endIndex = (pageNo + 1) * pageSize;
        List<MultiLocationUserListDto> userList = new ArrayList<>();
        Keycloak keycloak = keyCloakConfig.getInstance();
        List<String> countryUsers = userLocationMappingRepository.getDistinctUserId();
        Integer userTotalCount = countryUsers.size();
        if (endIndex > userTotalCount) {
            endIndex = userTotalCount;
        }
        List<UserRepresentation> userRepresentations;
        if (searchString != null && !searchString.isEmpty()) {
            userRepresentations = keycloak.realm(realm).users().search(searchString, 0, 1000);
            userRepresentations = userRepresentations.stream().filter(userRepresentation -> countryUsers.contains(userRepresentation.getId())).collect(Collectors.toList());

            Collections.sort(userRepresentations, (rp1, rp2) -> rp2.getCreatedTimestamp().compareTo(rp1.getCreatedTimestamp()));
            if (userRepresentations.size() <= endIndex) {
                endIndex = userRepresentations.size();

            }
            userTotalCount = userRepresentations.size();
            if (startIndex > endIndex) {
                userRepresentations = new ArrayList<>();
            } else {
                userRepresentations = userRepresentations.subList(startIndex, endIndex);
            }
        } else {

            List<UserRepresentation> representations = keycloak.realm(realm).users().list();
            userRepresentations = representations.stream().filter(userRepresentation -> countryUsers.contains(userRepresentation.getId())).collect(Collectors.toList());

            Collections.sort(userRepresentations, (rp1, rp2) -> rp2.getCreatedTimestamp().compareTo(rp1.getCreatedTimestamp()));
            userRepresentations = userRepresentations.subList(startIndex, endIndex);
        }

        for (UserRepresentation representation : userRepresentations) {
            List<RoleRepresentation> roleRepresentationList = keycloak.realm(realm).users().get(representation.getId()).roles().realmLevel().listAll();
            List<String> roles = new ArrayList<>();
            for (RoleRepresentation roleRepresentation : roleRepresentationList) {
                roles.add(roleRepresentation.getName());
            }
            representation.setRealmRoles(roles);
        }
        for (UserRepresentation representation : userRepresentations) {
            List<UserLocationMapping> userLocation = userLocationMappingRepository.findByUserId(representation.getId());
            List<FacilityDto> facilityDtos;
            if (!userLocation.isEmpty()) {
                facilityDtos = new ArrayList<>();
                for (UserLocationMapping mapping : userLocation) {
                    if (mapping.getFacilityId() != null) {
                        facilityDtos.add(locationResourceService.getFacilityDto(mapping.getFacilityId()));
                    }
                }

                userList.add(UserMapper.getMultiLocationUserListDto(representation, facilityDtos));
            } else {
                userList.add(UserMapper.getMultiLocationUserListDto(representation, null));
            }


        }
        PageDto page = new PageDto();
        page.setList(userList);
        page.setTotalCount(userTotalCount.longValue());
        return page;
    }

    @Override
    public List<UserListDto> getAllSignedUpUser(HttpServletRequest request) {
        List<UserListDto> users = new ArrayList<>();
        List<UserLocationMapping> newSignedUpUser = userLocationMappingRepository.findByIsFirstOrderByCreateDateDesc(true);
        for (UserLocationMapping userLocationMapping : newSignedUpUser) {
            UserListDto user = getUserDtoByIdAndLocation(userLocationMapping.getUserId(), userLocationMapping.getFacilityId());
            users.add(user);
        }
        return users;
    }

    @Override
    public List<RoleRepresentation> getAllRoles(HttpServletRequest request) {
        Keycloak keycloakInstance = keyCloakConfig.getInstanceByAuth();
        List<RoleRepresentation> roleRepresentationList = keycloakInstance.realm(realm).roles().list();
        List<String> countryRoleId = roleEntityRepository.findAll().stream().map(RoleEntity::getRoleId).collect(Collectors.toList());
        roleRepresentationList = roleRepresentationList.stream().filter(roleRepresentation -> countryRoleId.contains(roleRepresentation.getId())).collect(Collectors.toList());
        return roleRepresentationList;
    }

    @Override
    public RoleRepresentation getRoleByName(String roleId, HttpServletRequest request) {
        Keycloak keycloakInstance = keyCloakConfig.getKeyCloakInstance();
        return keycloakInstance.realm(realm).rolesById().getRole(roleId);
    }

    @Override
    public RolesResource getAllRolesForSignUp(HttpServletRequest request) {
        return keyCloakConfig.getKeyCloakInstance().realm(realm).roles();
    }

    @Override
    public ResponseEntity<Object> signUp(UserDto user, HttpServletRequest request) {
        Keycloak keycloakInstance = keyCloakConfig.getKeyCloakInstance();

//        Get Realm Resource
        RealmResource realmResource = keycloakInstance.realm(realm);
//        Get User Resource
        UsersResource usersResource = keycloakInstance.realm(realm).users();
//        Generate Password
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());

//        Create User Representation
        UserRepresentation kcUser = new UserRepresentation();
        Settings usernameSetting = adminSettingRepository.findByKey(CommonConstant.SETTING_TYPE_REGISTRATION_EMAIL_AS_USERNAME);
        if (usernameSetting.getValue().equals(CommonConstant.ACTIVE)) {
            kcUser.setUsername(user.getEmail());
        } else {
            kcUser.setUsername(user.getUserName());
        }
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setFirstName(user.getFirstName());
        kcUser.setLastName(user.getLastName());
        kcUser.setEmail(user.getEmail());
        kcUser.setEnabled(Boolean.FALSE);
        kcUser.setEmailVerified(false);


//        String tenantId = commonService.getTenantIdFromURL(request.getRequestURL().toString(), request.getRequestURI());
        Map<String, List<String>> attribute = new HashMap<>();
        attribute.put(CommonConstant.LANGUAGE_KEY, Arrays.asList(CommonConstant.ENGLISH));
        attribute.put(CommonConstant.PHONE_KEY, Arrays.asList(user.getPhone()));
        attribute.put(CommonConstant.COUNTRY_CODE, Arrays.asList(user.getCountryCode()));
        attribute.put(CommonConstant.TENANT_ID, Arrays.asList(TenantContext.getCurrentTenant()));
        kcUser.setAttributes(attribute);

        Map<String, Long> locationMap = new HashMap<>();
        for (String facilityId : user.getFacilityIds()) {
            FacilityDto facilityDto = locationResourceService.getFacilityDto(facilityId);
            locationMap.put(facilityId, facilityDto.getLocationId());
        }

        try {
            javax.ws.rs.core.Response response = usersResource.create(kcUser);
            String userId = CreatedResponseUtil.getCreatedId(response);
            userLocationMappingRepository.saveAll(UserMapper.getUserMappingEntityPerLocation(user, userId, locationMap));
            UserResource userResource = usersResource.get(userId);

//        Set Realm Role
            RoleRepresentation testerRealmRole = realmResource.roles().get(user.getRoleName()).toRepresentation();
            RoleRepresentation defaultRole = realmResource.roles().get(CommonConstant.DEFAULT_ROLE_EMCARE).toRepresentation();
            userResource.roles().realmLevel().add(Arrays.asList(testerRealmRole));
            userResource.roles().realmLevel().remove(Arrays.asList(defaultRole));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(CommonConstant.EMAIL_ALREADY_EXISTS, HttpStatus.BAD_REQUEST.value()));
        }

        CompletableFuture.runAsync(() -> {
            Settings settings = adminSettingService.getAdminSettingByName(CommonConstant.SETTING_TYPE_WELCOME_EMAIL);
            if (settings.getValue().equals(CommonConstant.ACTIVE)) {
                MailDto mailDto = mailDataSetterService.mailSubjectSetter(CommonConstant.MAIL_FOR_ADD_USER);
                Map<String, Object> mailData = new HashMap<>();
                mailData.put(CommonConstant.FIRST_NAME, user.getFirstName());
                mailData.put(CommonConstant.LAST_NAME, user.getLastName());
                String mailBody = mailDataSetterService.emailBodyCreator(mailData, mailDto.getBody(), mailDto);
                mailService.sendBasicMail(user.getEmail(), mailDto.getSubject(), mailBody);
            }
        });

        return ResponseEntity.ok(new Response(CommonConstant.REGISTER_SUCCESS, HttpStatus.OK.value()));
    }

    @Override
    public ResponseEntity<Object> userLogin(LoginRequestDto loginCred, HttpServletRequest request) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(CommonConstant.USERNAME, loginCred.getUsername());
        map.add(CommonConstant.PASSWORD, loginCred.getPassword());
        map.add(CommonConstant.GRANT_TYPE, CommonConstant.PASSWORD);
        map.add(CommonConstant.CLIENT_ID, clientId);
        map.add(CommonConstant.CLIENT_SECRET, clientSecret);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity data = null;
        try {
            data = restTemplate.exchange(keycloakLoginURL, HttpMethod.POST, entity, Map.class);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Invalid Credentials", HttpStatus.BAD_REQUEST.value()));
        }
        if (!data.getStatusCode().equals(HttpStatus.OK)) {
            return data;
        } else {
//            List<TenantConfig> tenantConfigList = tenantConfigRepository.findAll();
//            String domain = commonService.getDomainFormUrl(request.getRequestURL().toString(), request.getRequestURI());
            Map<String, Object> loginResponse = (Map<String, Object>) data.getBody();
            try {

                AccessToken accessToken = TokenVerifier.create(loginResponse.get(CommonConstant.ACCESS_TOKEN).toString(), AccessToken.class).getToken();
                String userId = accessToken.getSubject();
                UserRepresentation userRepresentation = getUserByEmailId(loginCred.getUsername());
                String tenantId = Objects.nonNull(userRepresentation.getAttributes().get(CommonConstant.TENANT_ID))
                    ? userRepresentation.getAttributes().get(CommonConstant.TENANT_ID).get(0)
                    : defaultTenant;
                Set<String> roles = accessToken.getRealmAccess().getRoles();
                TenantContext.clearTenant();
                TenantContext.setCurrentTenant(tenantId);
                List<UserLocationMapping> userLocationMappings = userLocationMappingRepository.findByUserId(userId);
                loginResponse.put("Application-Agent", tenantId);
                return ResponseEntity.ok().body(loginResponse);
//                if (userLocationMappings.size() > 0) {
//                    return ResponseEntity.ok().body(loginResponse);
//                } else {
//                    return ResponseEntity.ok().body(loginResponse);
//                    if (roles.contains(CommonConstant.SUPER_ADMIN_ROLE)) {
//                        return ResponseEntity.ok().body(loginResponse);
//                    } else (roles.contains(tenantId + "_Admin") || roles.contains("admin_user")) {
//                        return ResponseEntity.ok().body(loginResponse);
//                    }
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("You don't have access for this domain", HttpStatus.BAD_REQUEST.value()));
//                }
            } catch (Exception ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("You don't have access for this domain", HttpStatus.BAD_REQUEST.value()));
            }
        }

    }

    @Override
    public ResponseEntity<Object> addUser(UserDto user, HttpServletRequest request) {
        Keycloak keycloak = keyCloakConfig.getInstance();
//        Get Realm Resource
        RealmResource realmResource = keycloak.realm(realm);
//        Get User Resource
        UsersResource usersResource = keycloak.realm(realm).users();

        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());
//        Create User Representation
        UserRepresentation kcUser = new UserRepresentation();
        Settings usernameSetting = adminSettingRepository.findByKey(CommonConstant.SETTING_TYPE_REGISTRATION_EMAIL_AS_USERNAME);
        if (usernameSetting.getValue().equals(CommonConstant.ACTIVE)) {
            kcUser.setUsername(user.getEmail());
        } else {
            kcUser.setUsername(user.getUserName());
        }
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setFirstName(user.getFirstName());
        kcUser.setLastName(user.getLastName());
        kcUser.setEmail(user.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);
//        String tenantId = commonService.getTenantIdFromURL(request.getRequestURL().toString(), request.getRequestURI());
        Map<String, List<String>> attribute = new HashMap<>();
        attribute.put(CommonConstant.LANGUAGE_KEY, Arrays.asList(CommonConstant.ENGLISH));
        attribute.put(CommonConstant.PHONE_KEY, Arrays.asList(user.getPhone()));
        attribute.put(CommonConstant.COUNTRY_CODE, Arrays.asList(user.getCountryCode()));
        attribute.put(CommonConstant.TENANT_ID, Arrays.asList(TenantContext.getCurrentTenant()));
        kcUser.setAttributes(attribute);

        try {

            Map<String, Long> locationMap = new HashMap<>();
            for (String facilityId : user.getFacilityIds()) {
                FacilityDto facilityDto = locationResourceService.getFacilityDto(facilityId);
                locationMap.put(facilityId, facilityDto.getLocationId());
            }

            javax.ws.rs.core.Response response = usersResource.create(kcUser);
            String userId = CreatedResponseUtil.getCreatedId(response);
            userLocationMappingRepository.saveAll(UserMapper.getUserMappingEntityPerLocation(user, userId, locationMap));
            UserResource userResource = usersResource.get(userId);

//        Set Realm Role
            RoleRepresentation testerRealmRole = realmResource.roles().get(user.getRoleName()).toRepresentation();
            RoleRepresentation defaultRole = realmResource.roles().get("default-roles-emcare").toRepresentation();
            userResource.roles().realmLevel().add(Arrays.asList(testerRealmRole));
            userResource.roles().realmLevel().remove(Arrays.asList(defaultRole));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }

        CompletableFuture.runAsync(() -> {
            Settings settings = adminSettingService.getAdminSettingByName(CommonConstant.SETTING_TYPE_WELCOME_EMAIL);
            if (settings.getValue().equals(CommonConstant.ACTIVE)) {

                MailDto mailDto = mailDataSetterService.mailSubjectSetter(CommonConstant.MAIL_FOR_ADD_USER);
                Map<String, Object> mailData = new HashMap<>();
                mailData.put("firstName", user.getFirstName());
                mailData.put("lastName", user.getLastName());
                String mailBody = mailDataSetterService.emailBodyCreator(mailData, mailDto.getBody(), mailDto);
                mailService.sendBasicMail(user.getEmail(), mailDto.getSubject(), mailBody);
            }
        });

        return ResponseEntity.ok(new Response(CommonConstant.REGISTER_SUCCESS, HttpStatus.OK.value()));
    }

    @Override
    public void addRealmRole(RoleDto role) {
        Keycloak keycloak = keyCloakConfig.getInstanceByAuth();
        RoleRepresentation roleRep = new RoleRepresentation();
        roleRep.setName(role.getRoleName());
        roleRep.setDescription(role.getRoleDescription());
        keycloak.realm(realm).roles().create(roleRep);
        RoleResource roleResource = keycloak.realm(realm).roles().get(role.getRoleName());
//      ADD COMPOSITE ROLE
        RoleRepresentation defaultRoleRepresentation = keycloak.realm(realm).roles().get("default-roles-emcare").toRepresentation();
        List<RoleRepresentation> compositeRoles = new ArrayList<>();
        compositeRoles.add(defaultRoleRepresentation);
        roleResource.addComposites(compositeRoles);

//      ADD ALL MENU CONFIG FOR NEWLY ADDED ROLE
        RoleRepresentation roleRepresentation = keycloak.realm(realm).roles().get(role.getRoleName()).toRepresentation();
        List<MenuConfig> menuList = menuConfigRepository.findAll();
        List<UserMenuConfig> userMenuConfigs = userMenuConfigRepository.findAll();
        List<RoleRepresentation> roleRepresentationList = keycloak.realm(realm).roles().list();
        if (userMenuConfigs.isEmpty()) {
            for (MenuConfig menu : menuList) {
                for (RoleRepresentation roleReps : roleRepresentationList) {
                    UserMenuConfig userMenuConfig = new UserMenuConfig();
                    userMenuConfig.setMenuId(menu.getId());
                    userMenuConfig.setRoleId(roleReps.getId());
                    userMenuConfigRepository.save(userMenuConfig);
                }
            }
        } else {
            for (MenuConfig menu : menuList) {
                UserMenuConfig userMenuConfig = new UserMenuConfig();
                userMenuConfig.setMenuId(menu.getId());
                userMenuConfig.setRoleId(roleRepresentation.getId());
                userMenuConfigRepository.save(userMenuConfig);
            }
        }

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleId(roleRepresentation.getId());
        roleEntity.setRoleName(role.getRoleName());
        roleEntityRepository.saveAndFlush(roleEntity);
    }

    @Override
    public ResponseEntity<Object> updateUserStatus(UserUpdateDto userUpdateDto) {
        Keycloak keycloak = keyCloakConfig.getInstanceByAuth();
//        Get User Resource
        UsersResource usersResource = keycloak.realm(realm).users();
        UserRepresentation user = usersResource.get(userUpdateDto.getUserId()).toRepresentation();
        user.setEnabled(userUpdateDto.getIsEnabled());
        usersResource.get(userUpdateDto.getUserId()).update(user);
        UserLocationMapping oldUser = userLocationMappingRepository.findByUserId(userUpdateDto.getUserId()).get(0);
        oldUser.setState(userUpdateDto.getIsEnabled());
        oldUser.setIsFirst(false);
        userLocationMappingRepository.save(oldUser);

        if (userUpdateDto.getIsEnabled()) {
            CompletableFuture.runAsync(() -> {
                Settings settings = adminSettingService.getAdminSettingByName(CommonConstant.SETTING_TYPE_SEND_CONFIRMATION_EMAIL);
                if (settings.getValue().equals(CommonConstant.ACTIVE)) {
                    MailDto mailDto = mailDataSetterService.mailSubjectSetter(CommonConstant.MAIL_FOR_CONFIRMATION_EMAIL_APPROVED);
                    Map<String, Object> mailData = new HashMap<>();
                    mailData.put("firstName", user.getFirstName());
                    mailData.put("lastName", user.getLastName());
                    String mailBody = mailDataSetterService.emailBodyCreator(mailData, mailDto.getBody(), mailDto);
                    mailService.sendBasicMail(user.getEmail(), mailDto.getSubject(), mailBody);
                }
            });
        } else {
            CompletableFuture.runAsync(() -> {
                Settings settings = adminSettingService.getAdminSettingByName(CommonConstant.SETTING_TYPE_SEND_CONFIRMATION_EMAIL);
                if (settings.getValue().equals(CommonConstant.ACTIVE)) {
                    MailDto mailDto = mailDataSetterService.mailSubjectSetter(CommonConstant.MAIL_FOR_CONFIRMATION_EMAIL_REJECTED);
                    Map<String, Object> mailData = new HashMap<>();
                    mailData.put("firstName", user.getFirstName());
                    mailData.put("lastName", user.getLastName());
                    String mailBody = mailDataSetterService.emailBodyCreator(mailData, mailDto.getBody(), mailDto);
                    mailService.sendBasicMail(user.getEmail(), mailDto.getSubject(), mailBody);
                }
            });
        }

        return ResponseEntity.ok(oldUser);

    }

    @Override
    public ResponseEntity<Object> getUserRolesById(String userId) {
        Keycloak keycloak = keyCloakConfig.getInstanceByAuth();
        RoleMappingResource userRoles = keycloak.realm(realm).users().get(userId).roles();
        return ResponseEntity.ok(userRoles.getAll());
    }

    @Override
    public ResponseEntity<Object> updateRole(RoleUpdateDto roleUpdateDto) {
        Keycloak keycloak = keyCloakConfig.getInstance();
        RoleRepresentation roleRep = new RoleRepresentation();
        roleRep.setName(roleUpdateDto.getName());
        roleRep.setDescription(roleUpdateDto.getDescription());
        RoleResource roleResource = keycloak.realm(realm).roles().get(roleUpdateDto.getOldRoleName());
        roleResource.update(roleRep);

        RoleEntity roleEntity = roleEntityRepository.findByRoleName(roleUpdateDto.getOldRoleName());
        roleEntity.setRoleName(roleUpdateDto.getName());
        roleEntityRepository.saveAndFlush(roleEntity);
        return ResponseEntity.ok(roleUpdateDto);
    }

    @Override
    public String getRoleIdByName(String roleName) {
        Keycloak keycloak = keyCloakConfig.getInstanceByAuth();
        RoleResource roleResource = keycloak.realm(realm).roles().get(roleName);
        return roleResource.toRepresentation().getId();
    }

    @Override
    public String getRoleNameById(String roleId) {
        String roleName = "";
        Keycloak keycloak = keyCloakConfig.getInstanceByAuth();
        RoleRepresentation roleResource = keycloak.realm(realm).roles().list().stream().filter(role -> roleId.equals(role.getId())).findAny().orElse(null);
        if (roleResource != null) {
            roleName = roleResource.getName();
        }
        return roleName;
    }

    @Override
    public PageDto getUsersUnderLocation(Object locationId, Integer pageNo) {
        List<MultiLocationUserListDto> userList = new ArrayList<>();
        if (!isNumeric(locationId.toString())) {
            locationId = locationResourceService.getFacilityDto(locationId.toString()).getLocationId().intValue();
        }
        Keycloak keycloak = keyCloakConfig.getInstance();
        Integer totalCount = userLocationMappingRepository.getAllUserOnChildLocations(Integer.parseInt(locationId.toString())).size();
        List<String> allUsersIdUnderLocation = userLocationMappingRepository.getAllUserOnChildLocationsWithPage(Integer.parseInt(locationId.toString()), pageNo, CommonConstant.PAGE_SIZE);
        List<UserRepresentation> userRepresentations = new ArrayList<>();
        for (String userId : allUsersIdUnderLocation) {
            userRepresentations.add(getUserById(userId));
        }
        for (UserRepresentation representation : userRepresentations) {
            List<RoleRepresentation> roleRepresentationList = keycloak.realm(realm).users().get(representation.getId()).roles().realmLevel().listAll();
            List<String> roles = new ArrayList<>();
            for (RoleRepresentation roleRepresentation : roleRepresentationList) {
                roles.add(roleRepresentation.getName());
            }
            representation.setRealmRoles(roles);
        }
        for (UserRepresentation representation : userRepresentations) {
            List<UserLocationMapping> userLocation = userLocationMappingRepository.findByUserId(representation.getId());
            List<FacilityDto> facilityDtos;
            if (!userLocation.isEmpty()) {

                facilityDtos = new ArrayList<>();
                for (UserLocationMapping mapping : userLocation) {
                    if (mapping.getFacilityId() != null) {
                        facilityDtos.add(locationResourceService.getFacilityDto(mapping.getFacilityId()));
                    }
                }

                userList.add(UserMapper.getMultiLocationUserListDto(representation, facilityDtos));
            } else {
                userList.add(UserMapper.getMultiLocationUserListDto(representation, null));
            }
        }
        PageDto pageDto = new PageDto();
        pageDto.setTotalCount(totalCount.longValue());
        pageDto.setList(userList);
        return pageDto;
    }

    @Override
    public UserRepresentation getUserByEmailId(String emailId) {
        Keycloak keycloak = keyCloakConfig.getInsideInstance();
        UsersResource usersResource = keycloak.realm(realm).users();
        List<UserRepresentation> userRepresentation = usersResource.search(emailId);
        if (!userRepresentation.isEmpty()) {
            return userRepresentation.get(0);
        } else {
            return null;
        }
    }

    @Override
    public UserRepresentation resetPassword(String emailId, String password) {
        Keycloak keycloak = keyCloakConfig.getInsideInstance();
        UserRepresentation userRepresentation = null;
        UsersResource usersResource = keycloak.realm(realm).users();

        List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().search(emailId);
        if (!userRepresentations.isEmpty()) {
            userRepresentation = userRepresentations.get(0);
            CredentialRepresentation credentialRepresentation = createPasswordCredentials(password);
            userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
            usersResource.get(userRepresentation.getId()).update(userRepresentation);
            return userRepresentation;
        }
        return null;
    }

    @Override
    public Map<String, Object> checkEmailIdExist(String email) {
        Keycloak keycloak = keyCloakConfig.getInsideInstance();
        UserRepresentation userRepresentation = null;
        UsersResource usersResource = keycloak.realm(realm).users();

        List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().search(email);
        Map<String, Object> response = new HashMap<>();
        if (userRepresentations.isEmpty()) {
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Valid Email Address");
        } else {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Email Already Taken By User");
        }
        return response;
    }

    @Override
    public ResponseEntity<Object> addUserForCountry(UserDto user, String tenantId) {
        Keycloak keycloak = keyCloakConfig.getInstance();
//        Get Realm Resource
        RealmResource realmResource = keycloak.realm(realm);
//        Get User Resource
        UsersResource usersResource = keycloak.realm(realm).users();

        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());
//        Create User Representation
        UserRepresentation kcUser = new UserRepresentation();
        Settings usernameSetting = adminSettingRepository.findByKey(CommonConstant.SETTING_TYPE_REGISTRATION_EMAIL_AS_USERNAME);
        if (usernameSetting.getValue().equals(CommonConstant.ACTIVE)) {
            kcUser.setUsername(user.getEmail());
        } else {
            kcUser.setUsername(user.getUserName());
        }
        kcUser.setUsername(user.getUserName());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setFirstName(user.getFirstName());
        kcUser.setLastName(user.getLastName());
        kcUser.setEmail(user.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);
        Map<String, List<String>> attribute = new HashMap<>();
        attribute.put(CommonConstant.LANGUAGE_KEY, Arrays.asList(CommonConstant.ENGLISH));
        attribute.put(CommonConstant.PHONE_KEY, Arrays.asList(user.getPhone()));
        attribute.put(CommonConstant.COUNTRY_CODE, Arrays.asList(user.getCountryCode()));
        attribute.put(CommonConstant.TENANT_ID, Arrays.asList(tenantId));
        kcUser.setAttributes(attribute);

        try {

            Map<String, Long> locationMap = new HashMap<>();
//            for (String facilityId : user.getFacilityIds()) {
//                FacilityDto facilityDto = locationResourceService.getFacilityDto(facilityId);
//                locationMap.put(facilityId, facilityDto.getLocationId());
//            }

            javax.ws.rs.core.Response response = usersResource.create(kcUser);
            String userId = CreatedResponseUtil.getCreatedId(response);
            userLocationMappingRepository.saveAll(UserMapper.getUserMappingEntityPerLocationForTenant(user, userId, locationMap));
            UserResource userResource = usersResource.get(userId);

//        Set Realm Role
            RoleRepresentation testerRealmRole = realmResource.roles().get(user.getRoleName()).toRepresentation();
            RoleRepresentation defaultRole = realmResource.roles().get("default-roles-emcare").toRepresentation();
            userResource.roles().realmLevel().add(Arrays.asList(testerRealmRole));
            userResource.roles().realmLevel().remove(Arrays.asList(defaultRole));

            List<MenuConfig> menuList = menuConfigRepository.findAll();
            List<UserMenuConfig> userMenuConfigs = new ArrayList<>();
            for (MenuConfig menu : menuList) {
                UserMenuConfig userMenuConfig = new UserMenuConfig();
                userMenuConfig.setMenuId(menu.getId());
                userMenuConfig.setUserId(userId);
                userMenuConfigs.add(userMenuConfig);
            }
            userMenuConfigRepository.saveAllAndFlush(userMenuConfigs);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }

        CompletableFuture.runAsync(() -> {
            MailDto mailDto = mailDataSetterService.mailSubjectSetter(CommonConstant.MAIL_FOR_CONFIRMATION_EMAIL_APPROVED);
            Map<String, Object> mailData = new HashMap<>();
            mailData.put("firstName", user.getFirstName());
            mailData.put("lastName", user.getLastName());
            String mailBody = mailDataSetterService.emailBodyCreator(mailData, mailDto.getBody(), mailDto);
            mailService.sendBasicMail(user.getEmail(), mailDto.getSubject(), mailBody);
        });

        return ResponseEntity.ok(new Response(CommonConstant.REGISTER_SUCCESS, HttpStatus.OK.value()));
    }

    @Override
    public void removeRole(String roleName) throws Exception {
        try {
            Keycloak keycloak = keyCloakConfig.getInstance();
            RealmResource realmResource = keycloak.realm(realm);
            keycloak.realm(realm).roles().deleteRole(roleName);
        } catch (Exception ex) {
            throw new Exception();
        }
    }

    @Override
    public void removeUser(String email) throws Exception {
        try {
            Keycloak keycloak = keyCloakConfig.getInstance();
            RealmResource realmResource = keycloak.realm(realm);
            UserRepresentation userRepresentation = getUserByEmailId(email);
            keycloak.realm(realm).users().delete(userRepresentation.getId());
        } catch (Exception ex) {
            throw new Exception();
        }
    }

    @Override
    public List<String> getCurrentUserFacility() {
        String userId = emCareSecurityUser.getLoggedInUser().getSubject();
        List<UserLocationMapping> locationMappings = userLocationMappingRepository.findByUserId(userId);
        return locationMappings.stream().map(UserLocationMapping::getFacilityId).collect(Collectors.toList());
    }

    @Override
    public MultiLocationUserListDto getUserDtoById(String userId) {
        Keycloak keycloak = keyCloakConfig.getInstanceByAuth();
        MultiLocationUserListDto user;
        UserRepresentation userRepresentation = keycloak.realm(realm).users().get(userId).toRepresentation();
        List<RoleRepresentation> roleRepresentationList = keycloak.realm(realm).users().get(userRepresentation.getId()).roles().realmLevel().listAll();
        List<String> roles = new ArrayList<>();
        for (RoleRepresentation roleRepresentation : roleRepresentationList) {
            roles.add(roleRepresentation.getName());
        }
        userRepresentation.setRealmRoles(roles);

        List<UserLocationMapping> userLocation = userLocationMappingRepository.findByUserId(userRepresentation.getId());
        if (!userLocation.isEmpty()) {
            List<FacilityDto> facilityDtos;
            facilityDtos = new ArrayList<>();
            for (UserLocationMapping mapping : userLocation) {
                if (mapping.getFacilityId() != null) {
                    facilityDtos.add(locationResourceService.getFacilityDto(mapping.getFacilityId()));
                }
            }
            user = UserMapper.getMultiLocationUserListDto(userRepresentation, facilityDtos);
        } else {
            user = UserMapper.getMultiLocationUserListDto(userRepresentation, null);
        }

        return user;
    }

    @Override
    @Scope("request")
    public UserRepresentation getUserById(String userId) {
        Keycloak keycloak = keyCloakConfig.getInstance();
        return keycloak.realm(realm).users().get(userId).toRepresentation();
    }

    @Override
    public ResponseEntity<Object> updateUser(UserDto userDto, String userId, HttpServletRequest request) {
        Keycloak keycloak = keyCloakConfig.getInstance();
        RealmResource realmResource = keycloak.realm(realm);
        UserResource userResource = keycloak.realm(realm).users().get(userId);
        UserRepresentation newUser = userResource.toRepresentation();

        newUser.setFirstName(userDto.getFirstName());
        newUser.setLastName(userDto.getLastName());
        List<UserLocationMapping> userLocationMappingList = userLocationMappingRepository.findByUserId(userId);

        for (UserLocationMapping userLocationMapping : userLocationMappingList) {
            if (!userDto.getFacilityIds().contains(userLocationMapping.getFacilityId())) {
                userLocationMappingRepository.delete(userLocationMapping);
            }
        }
        for (String facilityId : userDto.getFacilityIds()) {
            UserLocationMapping assignedLocation = userLocationMappingRepository.findByUserIdAndFacilityId(userId, facilityId);
            UserLocationMapping ulm = new UserLocationMapping();
            if (assignedLocation == null) {
                ulm.setFacilityId(facilityId);
                ulm.setUserId(userId);
                ulm.setIsFirst(false);
                ulm.setRegRequestFrom(UserConst.WEB);
                ulm.setState(true);
                userLocationMappingRepository.save(ulm);
            }
        }
//        String tenantId = commonService.getTenantIdFromURL(request.getRequestURL().toString(), request.getRequestURI());
        Map<String, List<String>> attribute = new HashMap<>();
        attribute.put(CommonConstant.LANGUAGE_KEY, Arrays.asList(userDto.getLanguage()));
        attribute.put(CommonConstant.PHONE_KEY, Arrays.asList(userDto.getPhone()));
        attribute.put(CommonConstant.COUNTRY_CODE, Arrays.asList(userDto.getCountryCode()));
        attribute.put(CommonConstant.TENANT_ID, Arrays.asList(TenantContext.getCurrentTenant()));

        newUser.setAttributes(attribute);
        newUser.setEnabled(newUser.isEnabled());

        if (userDto.getRoleName() != null) {
            RoleRepresentation newRole = realmResource.roles().get(userDto.getRoleName()).toRepresentation();
            List<RoleRepresentation> removableRoles = userResource.roles().realmLevel().listAll();
            userResource.roles().realmLevel().remove(removableRoles);
            userResource.roles().realmLevel().add(new ArrayList<>(Arrays.asList(newRole)));
        }

        userResource.update(newUser);
        return ResponseEntity.status(HttpStatus.OK).body(new Response(CommonConstant.UPDATE_SUCCESS, HttpStatus.OK.value()));
    }

    @Override
    public ResponseEntity<Object> updatePassword(UserDto userDto, String userId) {
        Keycloak keycloak = keyCloakConfig.getInstance();
        UserResource userResource = keycloak.realm(realm).users().get(userId);
        UserRepresentation newUser = userResource.toRepresentation();

        if (userDto.getPassword() != null) {
            CredentialRepresentation credentialRepresentation = createPasswordCredentials(userDto.getPassword());
            newUser.setCredentials(Collections.singletonList(credentialRepresentation));
        }

        userResource.update(newUser);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private List<CurrentUserFeatureJson> getUserFeatureJson(List<String> roleIds, String userId) {
        List<UserFeatureJson> userFeatureJSONs = userMenuConfigRepository.getMenuByUser(roleIds, userId);
        List<CurrentUserFeatureJson> featureJsons = new ArrayList<>();

        for (UserFeatureJson ufj : userFeatureJSONs) {
            List<UserFeatureJson> list = userFeatureJSONs.stream().filter(feature -> ufj.getId().equals(feature.getId())).collect(Collectors.toList());
            List<CurrentUserFeatureJson> originalList = featureJsons.stream().filter(feature -> ufj.getMenuName().equals(feature.getMenuName())).collect(Collectors.toList());
            if (originalList.isEmpty()) {
                if (list.size() > 1) {
                    String json = getMargedStringOfFeatureJson(list);
                    featureJsons.add(MenuConfigMapper.getCurrentUserFeatureJson(ufj, json));
                } else {
                    featureJsons.add(MenuConfigMapper.getCurrentUserFeatureJson(ufj, null));
                }
            }
        }
        Collections.sort(featureJsons, (o1, o2) -> o1.getOrderNumber().compareTo(o2.getOrderNumber()));
        List<CurrentUserFeatureJson> mainMenuList = featureJsons.stream().filter(feature -> feature.getParent() == null || feature.getParent() == 0).collect(Collectors.toList());
        List<CurrentUserFeatureJson> finalMenuList = new ArrayList<>();
        for (CurrentUserFeatureJson mainMenu : mainMenuList) {
            List<CurrentUserFeatureJson> subMenu = featureJsons.stream().filter(feature -> feature.getParent() != null && feature.getParent() == mainMenu.getId().longValue()).collect(Collectors.toList());
            CurrentUserFeatureJson menu = new CurrentUserFeatureJson();
            menu.setSubMenu(subMenu);
            menu.setFeatureJson(mainMenu.getFeatureJson());
            menu.setOrderNumber(mainMenu.getOrderNumber());
            menu.setMenuName(mainMenu.getMenuName());
            menu.setId(mainMenu.getId());
            menu.setParent(mainMenu.getParent());
            finalMenuList.add(menu);
        }
        Collections.sort(finalMenuList, (o1, o2) -> o1.getOrderNumber().compareTo(o2.getOrderNumber()));
        return finalMenuList;
    }

    private String getMargedStringOfFeatureJson(List<UserFeatureJson> featureJsonList) {
        if (featureJsonList.size() > 2) {
            return featureJsonList.get(0).getFeatureJson();
        } else {
            FeatureJSON featureJSON = new FeatureJSON(true, true, true, true);
            Gson g = new Gson();
            FeatureJSON f1 = g.fromJson(featureJsonList.get(0).getFeatureJson(), FeatureJSON.class);
            FeatureJSON f2 = g.fromJson(featureJsonList.get(1).getFeatureJson(), FeatureJSON.class);
            if (!f1.getCanAdd().booleanValue() && !f2.getCanAdd().booleanValue()) {
                featureJSON.setCanAdd(false);
            }
            if (!f1.getCanDelete().booleanValue() && !f2.getCanDelete().booleanValue()) {
                featureJSON.setCanDelete(false);
            }
            if (!f1.getCanEdit().booleanValue() && !f2.getCanEdit().booleanValue()) {
                featureJSON.setCanEdit(false);
            }
            if (!f1.getCanView().booleanValue() && !f2.getCanView().booleanValue()) {
                featureJSON.setCanView(false);
            }
            return featureJSON.toString();
        }
    }

    private UserListDto getUserDtoByIdAndLocation(String userId, String facilityId) {
        Keycloak keycloak = keyCloakConfig.getInstanceByAuth();
        UserListDto user;
        UserRepresentation userRepresentation = keycloak.realm(realm).users().get(userId).toRepresentation();
        List<RoleRepresentation> roleRepresentationList = keycloak.realm(realm).users().get(userRepresentation.getId()).roles().realmLevel().listAll();
        List<String> roles = new ArrayList<>();
        for (RoleRepresentation roleRepresentation : roleRepresentationList) {
            roles.add(roleRepresentation.getName());
        }
        userRepresentation.setRealmRoles(roles);
        if (facilityId == null) {
            user = UserMapper.getUserListDto(userRepresentation, null);
        } else {
            FacilityDto facilityDto = locationResourceService.getFacilityDto(facilityId);
            user = UserMapper.getUserListDto(userRepresentation, facilityDto);
        }


        return user;
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


}
