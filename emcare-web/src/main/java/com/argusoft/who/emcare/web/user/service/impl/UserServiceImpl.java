package com.argusoft.who.emcare.web.user.service.impl;

import com.argusoft.who.emcare.web.adminSetting.Entity.Settings;
import com.argusoft.who.emcare.web.adminSetting.repository.AdminSettingRepository;
import com.argusoft.who.emcare.web.adminSetting.service.AdminSettingService;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.common.response.Response;
import com.argusoft.who.emcare.web.config.KeyCloakConfig;
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
import com.argusoft.who.emcare.web.user.cons.UserConst;
import com.argusoft.who.emcare.web.user.dto.*;
import com.argusoft.who.emcare.web.user.mapper.UserMapper;
import com.argusoft.who.emcare.web.user.service.UserService;
import com.argusoft.who.emcare.web.userLocationMapping.dao.UserLocationMappingRepository;
import com.argusoft.who.emcare.web.userLocationMapping.model.UserLocationMapping;
import com.google.gson.Gson;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
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

    @Override
    public UserMasterDto getCurrentUser() {
        AccessToken user = emCareSecurityUser.getLoggedInUser();
        Keycloak keycloak = keyCloakConfig.getInstance();
        UserRepresentation userInfo = keycloak.realm(KeyCloakConfig.REALM).users().get(user.getSubject()).toRepresentation();
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
        List<RoleRepresentation> roleRepresentationList = keycloak.realm(KeyCloakConfig.REALM).users().get(masterUser.getUserId()).roles().realmLevel().listAll();
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
        List<UserRepresentation> userRepresentations = keycloak.realm(KeyCloakConfig.REALM).users().list();
        for (UserRepresentation representation : userRepresentations) {
            List<RoleRepresentation> roleRepresentationList = keycloak.realm(KeyCloakConfig.REALM).users().get(representation.getId()).roles().realmLevel().listAll();
            List<String> roles = new ArrayList<>();
            for (RoleRepresentation roleRepresentation : roleRepresentationList) {
                roles.add(roleRepresentation.getName());
            }
            representation.setRealmRoles(roles);
        }
        for (UserRepresentation representation : userRepresentations) {
            List<UserLocationMapping> userLocation = userLocationMappingRepository.findByUserId(representation.getId());
            if (!userLocation.isEmpty()) {
//                CHNAGE IF REQUIRED
//                Optional<LocationMaster> locationMaster = locationMasterDao.findById(userLocation.get(0).getLocationId());
//                FacilityDto facilityDto = locationResourceService.getFacilityDto(facilityId);
//                userList.add(UserMapper.getUserListDto(representation, locationMaster.isPresent() ? locationMaster.get() : null));
            } else {
//                userList.add(UserMapper.getUserListDto(representation, null));
            }
        }
        return userList;
    }

    @Override
    public List<MultiLocationUserListDto> getAllUserWithMultiLocation(HttpServletRequest request) {
        List<MultiLocationUserListDto> userList = new ArrayList<>();
        Keycloak keycloak = keyCloakConfig.getInstance();
        List<UserRepresentation> userRepresentations = keycloak.realm(KeyCloakConfig.REALM).users().list();
        for (UserRepresentation representation : userRepresentations) {
            List<RoleRepresentation> roleRepresentationList = keycloak.realm(KeyCloakConfig.REALM).users().get(representation.getId()).roles().realmLevel().listAll();
            List<String> roles = new ArrayList<>();
            for (RoleRepresentation roleRepresentation : roleRepresentationList) {
                roles.add(roleRepresentation.getName());
            }
            representation.setRealmRoles(roles);
        }
        for (UserRepresentation representation : userRepresentations) {
            List<UserLocationMapping> userLocation = userLocationMappingRepository.findByUserId(representation.getId());
//            List<Integer> locationIds = userLocation.stream().map(UserLocationMapping::getLocationId).collect(Collectors.toList());
            List<FacilityDto> facilityDtos = new ArrayList<>();
            if (!userLocation.isEmpty()) {
//                Iterable<Integer> iterableLocationIds = locationIds;
//                List<LocationMaster> locationMaster = locationMasterDao.findAllById(iterableLocationIds);
//                List<LocationMasterWithHierarchy> locationMasterWithHierarchies = new ArrayList<>();
//                for (LocationMaster master : locationMaster) {
//                    locationMasterWithHierarchies.add(LocationMasterMapper.getLocationMasterWithHierarchy(master, locationMasterDao.getNameHierarchy(master.getId())));
//                }

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
        Integer userTotalCount = keycloak.realm(KeyCloakConfig.REALM).users().list().size();
        if (endIndex > userTotalCount) {
            endIndex = userTotalCount;
        }
        List<UserRepresentation> userRepresentations;
        if (searchString != null && !searchString.isEmpty()) {
            userRepresentations = keycloak.realm(KeyCloakConfig.REALM).users().search(searchString, 0, 1000);
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
            userRepresentations = keycloak.realm(KeyCloakConfig.REALM).users().list().subList(startIndex, endIndex);
        }

        for (UserRepresentation representation : userRepresentations) {
            List<RoleRepresentation> roleRepresentationList = keycloak.realm(KeyCloakConfig.REALM).users().get(representation.getId()).roles().realmLevel().listAll();
            List<String> roles = new ArrayList<>();
            for (RoleRepresentation roleRepresentation : roleRepresentationList) {
                roles.add(roleRepresentation.getName());
            }
            representation.setRealmRoles(roles);
        }
        for (UserRepresentation representation : userRepresentations) {
            List<UserLocationMapping> userLocation = userLocationMappingRepository.findByUserId(representation.getId());
//            Iterable<Integer> locationIds = userLocation.stream().map(UserLocationMapping::getLocationId).collect(Collectors.toList());
            List<FacilityDto> facilityDtos = new ArrayList<>();
            if (!userLocation.isEmpty()) {
//                List<LocationMaster> locationMaster = locationMasterDao.findAllById(locationIds);
//                List<LocationMasterWithHierarchy> locationMasterWithHierarchies = new ArrayList<>();
//                for (LocationMaster master : locationMaster) {
//                    locationMasterWithHierarchies.add(LocationMasterMapper.getLocationMasterWithHierarchy(master, locationMasterDao.getNameHierarchy(master.getId())));
//                }
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
        List<UserLocationMapping> newSignedUpUser = userLocationMappingRepository.findByIsFirst(true);
        for (UserLocationMapping userLocationMapping : newSignedUpUser) {
            UserListDto user = getUserDtoByIdAndLocation(userLocationMapping.getUserId(), userLocationMapping.getFacilityId());
            users.add(user);
        }
        return users;
//        List<String> userIds = mobileUsers.stream().map(UserLocationMapping::getUserId).collect(Collectors.toList());
//        return users.stream().filter(user -> userIds.contains(user.getId())).collect(Collectors.toList());
    }

    @Override
    public List<RoleRepresentation> getAllRoles(HttpServletRequest request) {
        Keycloak keycloakInstance = keyCloakConfig.getInstanceByAuth();
        return keycloakInstance.realm(KeyCloakConfig.REALM).roles().list();
    }

    @Override
    public RoleRepresentation getRoleByName(String roleId, HttpServletRequest request) {
        Keycloak keycloakInstance = keyCloakConfig.getInstanceByAuth();
        return keycloakInstance.realm(KeyCloakConfig.REALM).rolesById().getRole(roleId);
    }

    @Override
    public RolesResource getAllRolesForSignUp(HttpServletRequest request) {
        return getKeyCloakInstance().realm(KeyCloakConfig.REALM).roles();
    }

    @Override
    public ResponseEntity<Object> signUp(UserDto user) {
        Keycloak keycloakInstance = getKeyCloakInstance();

//        Get Realm Resource
        RealmResource realmResource = keycloakInstance.realm(KeyCloakConfig.REALM);
//        Get User Resource
        UsersResource usersResource = keycloakInstance.realm(KeyCloakConfig.REALM).users();
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
        Map<String, List<String>> attribute = new HashMap<>();
        attribute.put(CommonConstant.LANGUAGE_KEY, Arrays.asList(CommonConstant.ENGLISH));
        attribute.put(CommonConstant.PHONE_KEY, Arrays.asList(user.getPhone()));
        attribute.put(CommonConstant.COUNTRY_CODE, Arrays.asList(user.getCountryCode()));
        kcUser.setAttributes(attribute);

        try {
            javax.ws.rs.core.Response response = usersResource.create(kcUser);
            String userId = CreatedResponseUtil.getCreatedId(response);
            userLocationMappingRepository.saveAll(UserMapper.getUserMappingEntityPerLocation(user, userId));
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
                String mailBody = mailDto.getBody() + " " + user.getEmail();
                mailService.sendBasicMail(user.getEmail(), mailDto.getSubject(), mailBody);
            }
        });

        return ResponseEntity.ok(new Response(CommonConstant.REGISTER_SUCCESS, HttpStatus.OK.value()));
    }

    @Override
    public ResponseEntity<Object> addUser(UserDto user) {
        Keycloak keycloak = keyCloakConfig.getInstance();
//        Get Realm Resource
        RealmResource realmResource = keycloak.realm(KeyCloakConfig.REALM);
//        Get User Resource
        UsersResource usersResource = keycloak.realm(KeyCloakConfig.REALM).users();

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
        kcUser.setAttributes(attribute);

        try {
            javax.ws.rs.core.Response response = usersResource.create(kcUser);
            String userId = CreatedResponseUtil.getCreatedId(response);
            userLocationMappingRepository.saveAll(UserMapper.getUserMappingEntityPerLocation(user, userId));
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
                String mailBody = mailDto.getBody() + " " + user.getEmail();
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
        keycloak.realm(KeyCloakConfig.REALM).roles().create(roleRep);
        RoleResource roleResource = keycloak.realm(KeyCloakConfig.REALM).roles().get(role.getRoleName());
//      ADD COMPOSITE ROLE
        RoleRepresentation defaultRoleRepresentation = keycloak.realm(KeyCloakConfig.REALM).roles().get("default-roles-emcare").toRepresentation();
        List<RoleRepresentation> compositeRoles = new ArrayList<>();
        compositeRoles.add(defaultRoleRepresentation);
        roleResource.addComposites(compositeRoles);

//      ADD ALL MENU CONFIG FOR NEWLY ADDED ROLE
        RoleRepresentation roleRepresentation = keycloak.realm(KeyCloakConfig.REALM).roles().get(role.getRoleName()).toRepresentation();
        List<MenuConfig> menuList = menuConfigRepository.findAll();
        List<UserMenuConfig> userMenuConfigs = userMenuConfigRepository.findAll();
        List<RoleRepresentation> roleRepresentationList = keycloak.realm(KeyCloakConfig.REALM).roles().list();
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
    }

    @Override
    public ResponseEntity<Object> updateUserStatus(UserUpdateDto userUpdateDto) {
        Keycloak keycloak = keyCloakConfig.getInstanceByAuth();
//        Get User Resource
        UsersResource usersResource = keycloak.realm(KeyCloakConfig.REALM).users();
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
                    String mailBody = mailDto.getBody();
                    mailService.sendBasicMail(user.getEmail(), mailDto.getSubject(), mailBody);
                }
            });
        } else {
            CompletableFuture.runAsync(() -> {
                Settings settings = adminSettingService.getAdminSettingByName(CommonConstant.SETTING_TYPE_SEND_CONFIRMATION_EMAIL);
                if (settings.getValue().equals(CommonConstant.ACTIVE)) {
                    MailDto mailDto = mailDataSetterService.mailSubjectSetter(CommonConstant.MAIL_FOR_CONFIRMATION_EMAIL_REJECTED);
                    String mailBody = mailDto.getBody();
                    mailService.sendBasicMail(user.getEmail(), mailDto.getSubject(), mailBody);
                }
            });
        }

        return ResponseEntity.ok(oldUser);

    }

    @Override
    public ResponseEntity<Object> getUserRolesById(String userId) {
        Keycloak keycloak = keyCloakConfig.getInstanceByAuth();
        RoleMappingResource userRoles = keycloak.realm(KeyCloakConfig.REALM).users().get(userId).roles();
        return ResponseEntity.ok(userRoles.getAll());
    }

    @Override
    public ResponseEntity<Object> updateRole(RoleUpdateDto roleUpdateDto) {
        Keycloak keycloak = keyCloakConfig.getInstance();
        RoleRepresentation roleRep = new RoleRepresentation();
        roleRep.setName(roleUpdateDto.getName());
        roleRep.setDescription(roleUpdateDto.getDescription());
        RoleResource roleResource = keycloak.realm(KeyCloakConfig.REALM).roles().get(roleUpdateDto.getOldRoleName());
        roleResource.update(roleRep);
        return ResponseEntity.ok(roleUpdateDto);
    }

    @Override
    public String getRoleIdByName(String roleName) {
        Keycloak keycloak = keyCloakConfig.getInstanceByAuth();
        RoleResource roleResource = keycloak.realm(KeyCloakConfig.REALM).roles().get(roleName);
        return roleResource.toRepresentation().getId();
    }

    @Override
    public String getRoleNameById(String roleId) {
        String roleName = "";
        Keycloak keycloak = keyCloakConfig.getInstanceByAuth();
        RoleRepresentation roleResource = keycloak.realm(KeyCloakConfig.REALM).roles().list().stream().filter(role -> roleId.equals(role.getId())).findAny().orElse(null);
        if (roleResource != null) {
            roleName = roleResource.getName();
        }
        return roleName;
    }

    @Override
    public PageDto getUsersUnderLocation(Integer locationId, Integer pageNo) {
        List<MultiLocationUserListDto> userList = new ArrayList<>();

        Keycloak keycloak = keyCloakConfig.getInstance();
        Integer totalCount = userLocationMappingRepository.getAllUserOnChildLocations(locationId).size();
        List<String> allUsersIdUnderLocation = userLocationMappingRepository.getAllUserOnChildLocationsWithPage(locationId, pageNo, CommonConstant.PAGE_SIZE);
        List<UserRepresentation> userRepresentations = new ArrayList<>();
        for (String userId : allUsersIdUnderLocation) {
            userRepresentations.add(getUserById(userId));
        }
        for (UserRepresentation representation : userRepresentations) {
            List<RoleRepresentation> roleRepresentationList = keycloak.realm(KeyCloakConfig.REALM).users().get(representation.getId()).roles().realmLevel().listAll();
            List<String> roles = new ArrayList<>();
            for (RoleRepresentation roleRepresentation : roleRepresentationList) {
                roles.add(roleRepresentation.getName());
            }
            representation.setRealmRoles(roles);
        }
        for (UserRepresentation representation : userRepresentations) {
            List<UserLocationMapping> userLocation = userLocationMappingRepository.findByUserId(representation.getId());
//            Iterable<Integer> locationIds = userLocation.stream().map(UserLocationMapping::getLocationId).collect(Collectors.toList());
            List<FacilityDto> facilityDtos = new ArrayList<>();
            if (!userLocation.isEmpty()) {
//                List<LocationMaster> locationMaster = locationMasterDao.findAllById(locationIds);
//                List<LocationMasterWithHierarchy> locationMasterWithHierarchies = new ArrayList<>();
//                for (LocationMaster master : locationMaster) {
//                    locationMasterWithHierarchies.add(LocationMasterMapper.getLocationMasterWithHierarchy(master, locationMasterDao.getNameHierarchy(master.getId())));
//                }

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
        UsersResource usersResource = keycloak.realm(KeyCloakConfig.REALM).users();
        List<UserRepresentation> userRepresentation = keycloak.realm(KeyCloakConfig.REALM).users().search(emailId);
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
        UsersResource usersResource = keycloak.realm(KeyCloakConfig.REALM).users();

        List<UserRepresentation> userRepresentations = keycloak.realm(KeyCloakConfig.REALM).users().search(emailId);
        if (!userRepresentations.isEmpty()) {
            userRepresentation = userRepresentations.get(0);
            CredentialRepresentation credentialRepresentation = createPasswordCredentials(password);
            userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
            usersResource.get(userRepresentation.getId()).update(userRepresentation);
            return userRepresentation;
        }
        return null;
    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    @Override
    public MultiLocationUserListDto getUserDtoById(String userId) {
        Keycloak keycloak = keyCloakConfig.getInstanceByAuth();
        MultiLocationUserListDto user;
        UserRepresentation userRepresentation = keycloak.realm(KeyCloakConfig.REALM).users().get(userId).toRepresentation();
        List<RoleRepresentation> roleRepresentationList = keycloak.realm(KeyCloakConfig.REALM).users().get(userRepresentation.getId()).roles().realmLevel().listAll();
        List<String> roles = new ArrayList<>();
        for (RoleRepresentation roleRepresentation : roleRepresentationList) {
            roles.add(roleRepresentation.getName());
        }
        userRepresentation.setRealmRoles(roles);

        List<UserLocationMapping> userLocation = userLocationMappingRepository.findByUserId(userRepresentation.getId());
//        Iterable<Integer> locationIds = userLocation.stream().map(UserLocationMapping::getLocationId).collect(Collectors.toList());
        if (!userLocation.isEmpty()) {
//            List<LocationMaster> locationMaster = locationMasterDao.findAllById(locationIds);
//            List<LocationMasterWithHierarchy> locationMasterWithHierarchies = new ArrayList<>();
//            for (LocationMaster master : locationMaster) {
//                locationMasterWithHierarchies.add(LocationMasterMapper.getLocationMasterWithHierarchy(master, locationMasterDao.getNameHierarchy(master.getId())));
//            }
            List<FacilityDto> facilityDtos = new ArrayList<>();
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
    public UserRepresentation getUserById(String userId) {
        Keycloak keycloak = keyCloakConfig.getInstance();
        return keycloak.realm(KeyCloakConfig.REALM).users().get(userId).toRepresentation();
    }

    @Override
    public ResponseEntity<Object> updateUser(UserDto userDto, String userId) {
        Keycloak keycloak = keyCloakConfig.getInstance();
        RealmResource realmResource = keycloak.realm(KeyCloakConfig.REALM);
        UserResource userResource = keycloak.realm(KeyCloakConfig.REALM).users().get(userId);
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

        Map<String, List<String>> attribute = new HashMap<>();
        attribute.put(CommonConstant.LANGUAGE_KEY, Arrays.asList(userDto.getLanguage()));
        attribute.put(CommonConstant.PHONE_KEY, Arrays.asList(userDto.getPhone()));
        attribute.put(CommonConstant.COUNTRY_CODE, Arrays.asList(userDto.getCountryCode()));

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
        UserResource userResource = keycloak.realm(KeyCloakConfig.REALM).users().get(userId);
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
        UserRepresentation userRepresentation = keycloak.realm(KeyCloakConfig.REALM).users().get(userId).toRepresentation();
        List<RoleRepresentation> roleRepresentationList = keycloak.realm(KeyCloakConfig.REALM).users().get(userRepresentation.getId()).roles().realmLevel().listAll();
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

}
