package com.argusoft.who.emcare.web.user.service.impl;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.common.response.Response;
import com.argusoft.who.emcare.web.config.KeyCloakConfig;
import com.argusoft.who.emcare.web.location.dao.LocationMasterDao;
import com.argusoft.who.emcare.web.location.model.LocationMaster;
import com.argusoft.who.emcare.web.location.service.LocationService;
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

    @Override
    public UserMasterDto getCurrentUser() {
        AccessToken user = emCareSecurityUser.getLoggedInUser();
        Keycloak keycloak = keyCloakConfig.getInstance();
        UserRepresentation userInfo = keycloak.realm(KeyCloakConfig.REALM).users().get(user.getSubject()).toRepresentation();
        List<UserLocationMapping> userLocationList = userLocationMappingRepository.findByUserId(user.getSubject());
        UserLocationMapping userLocationMapping;
        LocationMaster userLocation;
        if (userLocationList.isEmpty()) {
            userLocation = null;
        } else {
            userLocationMapping = userLocationMappingRepository.findByUserId(user.getSubject()).get(0);
            userLocation = locationService.getLocationById(userLocationMapping.getLocationId());
        }
        UserMasterDto masterUser = UserMapper.getMasterUser(user, userLocation, userInfo);
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
                Optional<LocationMaster> locationMaster = locationMasterDao.findById(userLocation.get(0).getLocationId());
                userList.add(UserMapper.getUserListDto(representation, locationMaster.isPresent() ? locationMaster.get() : null));
            } else {
                userList.add(UserMapper.getUserListDto(representation, null));
            }
        }
        return userList;
    }

    @Override
    public PageDto getUserPage(HttpServletRequest request, Integer pageNo, String searchString) {
        Integer pageSize = CommonConstant.PAGE_SIZE;
        Integer startIndex = pageNo * pageSize;
        Integer endIndex = (pageNo + 1) * pageSize;
        List<UserListDto> userList = new ArrayList<>();
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
            if (!userLocation.isEmpty()) {
                Optional<LocationMaster> locationMaster = locationMasterDao.findById(userLocation.get(0).getLocationId());
                userList.add(UserMapper.getUserListDto(representation, locationMaster.isPresent() ? locationMaster.get() : null));
            } else {
                userList.add(UserMapper.getUserListDto(representation, null));
            }
        }
        PageDto page = new PageDto();
        page.setList(userList);
        page.setTotalCount(userTotalCount.longValue());
        return page;
    }

    @Override
    public List<UserListDto> getAllSignedUpUser(HttpServletRequest request) {
        List<UserListDto> users = getAllUser(request);
        List<UserLocationMapping> mobileUsers = userLocationMappingRepository.findByIsFirst(true);
        Set<String> userIds = mobileUsers.stream().map(UserLocationMapping::getUserId).collect(Collectors.toSet());
        return users.stream().filter(user -> userIds.contains(user.getId())).collect(Collectors.toList());
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
        kcUser.setUsername(user.getEmail());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setFirstName(user.getFirstName());
        kcUser.setLastName(user.getLastName());
        kcUser.setEmail(user.getEmail());
        kcUser.setEnabled(Boolean.FALSE);
        kcUser.setEmailVerified(false);
        Map<String, List<String>> languageAttribute = new HashMap<>();
        languageAttribute.put(CommonConstant.LANGUAGE_KEY, Arrays.asList(CommonConstant.ENGLISH));
        kcUser.setAttributes(languageAttribute);

        try {
            javax.ws.rs.core.Response response = usersResource.create(kcUser);
            String userId = CreatedResponseUtil.getCreatedId(response);
            userLocationMappingRepository.save(UserMapper.userDtoToUserLocationMappingEntityForSignup(user, userId));
            UserResource userResource = usersResource.get(userId);

//        Set Realm Role
            RoleRepresentation testerRealmRole = realmResource.roles().get(user.getRoleName()).toRepresentation();
            RoleRepresentation defaultRole = realmResource.roles().get(CommonConstant.DEFAULT_ROLE_EMCARE).toRepresentation();
            userResource.roles().realmLevel().add(Arrays.asList(testerRealmRole));
            userResource.roles().realmLevel().remove(Arrays.asList(defaultRole));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(CommonConstant.EMAIL_ALREADY_EXISTS, HttpStatus.BAD_REQUEST.value()));
        }
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
        kcUser.setUsername(user.getEmail());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setFirstName(user.getFirstName());
        kcUser.setLastName(user.getLastName());
        kcUser.setEmail(user.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);
        Map<String, List<String>> languageAttribute = new HashMap<>();
        languageAttribute.put(CommonConstant.LANGUAGE_KEY, Arrays.asList(CommonConstant.ENGLISH));
        kcUser.setAttributes(languageAttribute);

        try {
            javax.ws.rs.core.Response response = usersResource.create(kcUser);
            String userId = CreatedResponseUtil.getCreatedId(response);
            userLocationMappingRepository.save(UserMapper.userDtoToUserLocationMappingEntity(user, userId));
            UserResource userResource = usersResource.get(userId);

//        Set Realm Role
            RoleRepresentation testerRealmRole = realmResource.roles().get(user.getRoleName()).toRepresentation();
            RoleRepresentation defaultRole = realmResource.roles().get("default-roles-emcare").toRepresentation();
            userResource.roles().realmLevel().add(Arrays.asList(testerRealmRole));
            userResource.roles().realmLevel().remove(Arrays.asList(defaultRole));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(CommonConstant.EMAIL_ALREADY_EXISTS, HttpStatus.BAD_REQUEST.value()));
        }
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
        List<UserListDto> userList = new ArrayList<>();

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
            if (!userLocation.isEmpty()) {
                Optional<LocationMaster> locationMaster = locationMasterDao.findById(userLocation.get(0).getLocationId());
                userList.add(UserMapper.getUserListDto(representation, locationMaster.isPresent() ? locationMaster.get() : null));
            } else {
                userList.add(UserMapper.getUserListDto(representation, null));
            }
        }
        PageDto pageDto = new PageDto();
        pageDto.setTotalCount(totalCount.longValue());
        pageDto.setList(userList);
        return pageDto;
    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    @Override
    public UserListDto getUserDtoById(String userId) {
        Keycloak keycloak = keyCloakConfig.getInstanceByAuth();
        UserListDto user;
        UserRepresentation userRepresentation = keycloak.realm(KeyCloakConfig.REALM).users().get(userId).toRepresentation();
        List<RoleRepresentation> roleRepresentationList = keycloak.realm(KeyCloakConfig.REALM).users().get(userRepresentation.getId()).roles().realmLevel().listAll();
        List<String> roles = new ArrayList<>();
        for (RoleRepresentation roleRepresentation : roleRepresentationList) {
            roles.add(roleRepresentation.getName());
        }
        userRepresentation.setRealmRoles(roles);

        List<UserLocationMapping> userLocation = userLocationMappingRepository.findByUserId(userRepresentation.getId());
        if (!userLocation.isEmpty()) {
            Optional<LocationMaster> locationMaster = locationMasterDao.findById(userLocation.get(0).getLocationId());
            user = UserMapper.getUserListDto(userRepresentation, locationMaster.isPresent() ? locationMaster.get() : null);
        } else {
            user = UserMapper.getUserListDto(userRepresentation, null);
        }
        return user;
    }

    @Override
    public UserRepresentation getUserById(String userId) {
        Keycloak keycloak = keyCloakConfig.getInstanceByAuth();
        return keycloak.realm(KeyCloakConfig.REALM).users().get(userId).toRepresentation();
    }

    @Override
    public ResponseEntity<Object> updateUser(UserDto userDto, String userId) {
        Keycloak keycloak = keyCloakConfig.getInstance();
        UserResource userResource = keycloak.realm(KeyCloakConfig.REALM).users().get(userId);
        UserRepresentation newUser = userResource.toRepresentation();

        newUser.setFirstName(userDto.getFirstName());
        newUser.setLastName(userDto.getLastName());
        List<UserLocationMapping> userLocationMappingList = userLocationMappingRepository.findByUserId(userId);
        UserLocationMapping ulm = new UserLocationMapping();
        if (!userLocationMappingList.isEmpty()) {

            ulm = userLocationMappingList.get(0);
            ulm.setLocationId(userDto.getLocationId());
        } else {
            ulm.setLocationId(userDto.getLocationId());
            ulm.setUserId(userId);
            ulm.setIsFirst(false);
            ulm.setRegRequestFrom(UserConst.WEB);
            ulm.setState(true);
        }
        userLocationMappingRepository.save(ulm);

        Map<String, List<String>> languageAttribute = new HashMap<>();
        languageAttribute.put(CommonConstant.LANGUAGE_KEY, Arrays.asList(userDto.getLanguage()));
        newUser.setAttributes(languageAttribute);
        newUser.setEnabled(ulm.isState());
        userResource.update(newUser);
        return ResponseEntity.status(HttpStatus.OK).body(new Response(CommonConstant.UPDATE_SUCCESS, HttpStatus.OK.value()));
    }

    @Override
    public ResponseEntity<Object> updatePassword(UserDto userDto, String userId) {
        Keycloak keycloak = keyCloakConfig.getInstance();
        UserResource userResource = keycloak.realm(KeyCloakConfig.REALM).users().get(userId);
        UserRepresentation newUser = userResource.toRepresentation();

        if (userDto.getPassword() != null) {
            CredentialRepresentation credentialRepresentation =
                    createPasswordCredentials(userDto.getPassword());
            newUser.setCredentials(Collections
                    .singletonList(credentialRepresentation));
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
            if (originalList.size() < 1) {
                if (list.size() > 1) {
                    String json = getMargedStringOfFeatureJson(list);
                    featureJsons.add(MenuConfigMapper.getCurrentUserFeatureJson(ufj, json));
                } else {
                    featureJsons.add(MenuConfigMapper.getCurrentUserFeatureJson(ufj, null));
                }
            }
        }
        return featureJsons;
    }

    private String getMargedStringOfFeatureJson(List<UserFeatureJson> featureJsonList) {
        if (featureJsonList.size() > 2) {
            return featureJsonList.get(0).getFeatureJson();
        } else {
            FeatureJSON featureJSON = new FeatureJSON(true, true, true, true);
            Gson g = new Gson();
            FeatureJSON f1 = g.fromJson(featureJsonList.get(0).getFeatureJson(), FeatureJSON.class);
            FeatureJSON f2 = g.fromJson(featureJsonList.get(1).getFeatureJson(), FeatureJSON.class);
            if (!f1.getCanAdd() && !f2.getCanAdd()) {
                featureJSON.setCanAdd(false);
            }
            if (!f1.getCanDelete() && !f2.getCanDelete()) {
                featureJSON.setCanDelete(false);
            }
            if (!f1.getCanEdit() && !f2.getCanEdit()) {
                featureJSON.setCanEdit(false);
            }
            if (!f1.getCanView() && !f2.getCanView()) {
                featureJSON.setCanView(false);
            }
            return featureJSON.toString();
        }
    }

}
