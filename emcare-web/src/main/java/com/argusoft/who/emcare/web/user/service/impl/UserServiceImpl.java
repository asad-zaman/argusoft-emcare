package com.argusoft.who.emcare.web.user.service.impl;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.response.Response;
import com.argusoft.who.emcare.web.config.KeyCloakConfig;
import com.argusoft.who.emcare.web.location.model.LocationMaster;
import com.argusoft.who.emcare.web.location.service.LocationService;
import com.argusoft.who.emcare.web.secuirty.EmCareSecurityUser;
import com.argusoft.who.emcare.web.user.cons.UserConst;
import com.argusoft.who.emcare.web.user.dto.*;
import com.argusoft.who.emcare.web.user.mapper.UserMapper;
import com.argusoft.who.emcare.web.user.service.UserService;
import com.argusoft.who.emcare.web.userLocationMapping.dao.UserLocationMappingRepository;
import com.argusoft.who.emcare.web.userLocationMapping.model.UserLocationMapping;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author jay
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    KeyCloakConfig keyCloakConfig;

    @Autowired
    EmCareSecurityUser emCareSecurityUser;

    @Autowired
    UserLocationMappingRepository userLocationMappingRepository;

    @Autowired
    LocationService locationService;

    @Override
    public UserMasterDto getCurrentUser() {
        AccessToken user = emCareSecurityUser.getLoggedInUser();
        UserLocationMapping userLocationMapping = userLocationMappingRepository.findByUserId(user.getSubject()).get(0);
        LocationMaster userLocation = locationService.getLocationById(userLocationMapping.getLocationId());
        UserMasterDto masterUser = UserMapper.getMasterUser(user, userLocation);
        return masterUser;
    }

    @Override
    public List<UserRepresentation> getAllUser(HttpServletRequest request) {
        Keycloak keycloak = keyCloakConfig.getInstanceByAuth();
        List<UserRepresentation> userRepresentations = keycloak.realm(KeyCloakConfig.REALM).users().list();
        for (UserRepresentation representation : userRepresentations) {
            List<RoleRepresentation> roleRepresentationList = keycloak.realm(KeyCloakConfig.REALM).users().get(representation.getId()).roles().realmLevel().listAll();
            List<String> roles = new ArrayList<>();
            for (RoleRepresentation roleRepresentation : roleRepresentationList) {
                roles.add(roleRepresentation.getName());
            }
            representation.setRealmRoles(roles);
        }
        return userRepresentations;
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
        kcUser.setEmailVerified(false);

        kcUser.setEnabled(user.getRegRequestFrom().equalsIgnoreCase(UserConst.WEB));
        try {
            javax.ws.rs.core.Response response = usersResource.create(kcUser);
            String userId = CreatedResponseUtil.getCreatedId(response);
            userLocationMappingRepository.save(UserMapper.userDtoToUserLocationMappingEntity(user, userId));
            UserResource userResource = usersResource.get(userId);

//        Set Realm Role
            RoleRepresentation testerRealmRole = realmResource.roles().get(user.getRoleName()).toRepresentation();
            userResource.roles().realmLevel().add(Arrays.asList(testerRealmRole));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(CommonConstant.EMAIL_ALREADY_EXISTS, HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.ok(new Response(CommonConstant.REGISTER_SUCCESS, HttpStatus.OK.value()));
    }

    @Override
    public void addUser(UserDto user) {
        Keycloak keycloak = keyCloakConfig.getInstance();
//        Get Realm Resource
        RealmResource realmResource = keycloak.realm(KeyCloakConfig.REALM);
//        Get User Resource
        UsersResource usersResource = keycloak.realm(KeyCloakConfig.REALM).users();

//        Create User Representation
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(user.getEmail());
        kcUser.setFirstName(user.getFirstName());
        kcUser.setLastName(user.getLastName());
        kcUser.setEmail(user.getEmail());
        kcUser.setEmailVerified(false);

        kcUser.setEnabled(user.getRegRequestFrom().equalsIgnoreCase(UserConst.WEB));
        javax.ws.rs.core.Response response = usersResource.create(kcUser);

        String userId = CreatedResponseUtil.getCreatedId(response);
        userLocationMappingRepository.save(UserMapper.userDtoToUserLocationMappingEntity(user, userId));
        UserResource userResource = usersResource.get(userId);

//        Set Realm Role
//        TODO Set Role by admin not specific
        RoleRepresentation testerRealmRole = realmResource.roles().get(UserConst.ROLE_USER).toRepresentation();
        userResource.roles().realmLevel().add(Arrays.asList(testerRealmRole));
    }

    @Override
    public void addRealmRole(RoleDto role) {
        Keycloak keycloak = keyCloakConfig.getInstanceByAuth();
        RoleRepresentation roleRep = new RoleRepresentation();
        roleRep.setName(role.getRoleName());
        roleRep.setDescription(role.getRoleDescription());
        keycloak.realm(KeyCloakConfig.REALM).roles().create(roleRep);
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
        oldUser.setState(true);
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

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    @Override
    public ResponseEntity<Object> getUserById(String userId) {
        Keycloak keycloak = keyCloakConfig.getInstance();
        UserRepresentation retrievedUser = keycloak.realm(KeyCloakConfig.REALM)
                .users().get(userId).toRepresentation();
        return ResponseEntity.ok(retrievedUser);
    }

    @Override
    public ResponseEntity<Object> updateUser(UserDto userDto, String userId) {
        Keycloak keycloak = keyCloakConfig.getInstance();
        RealmResource realmResource = keycloak.realm(KeyCloakConfig.REALM);
        UserResource userResource = keycloak.realm(KeyCloakConfig.REALM)
                .users().get(userId);
        UserRepresentation oldUser = userResource.toRepresentation();

        oldUser.setFirstName(userDto.getFirstName());
        oldUser.setLastName(userDto.getLastName());

        oldUser.setEnabled(userDto.getRegRequestFrom().equalsIgnoreCase(UserConst.WEB));
        userResource.update(oldUser);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
