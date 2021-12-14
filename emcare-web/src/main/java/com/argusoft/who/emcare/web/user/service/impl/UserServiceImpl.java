package com.argusoft.who.emcare.web.user.service.impl;

import com.argusoft.who.emcare.web.config.KeyCloakConfig;
import com.argusoft.who.emcare.web.user.cons.UserConst;
import com.argusoft.who.emcare.web.user.dao.UserRepository;
import com.argusoft.who.emcare.web.user.dto.RoleDto;
import com.argusoft.who.emcare.web.user.dto.UserDto;
import com.argusoft.who.emcare.web.user.dto.UserUpdateDto;
import com.argusoft.who.emcare.web.user.mapper.UserMapper;
import com.argusoft.who.emcare.web.user.model.User;
import com.argusoft.who.emcare.web.user.service.UserService;
import com.google.gson.Gson;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author jay
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UsersResource getAllUserResource(HttpServletRequest request) {
        Keycloak keycloak = KeyCloakConfig.getInstanceByAuth(request);
        return keycloak.realm(KeyCloakConfig.realm).users();
    }

    @Override
    public RolesResource getAllRoles(HttpServletRequest request) {
        Keycloak keycloakInstance = KeyCloakConfig.getInstanceByAuth(request);
        return keycloakInstance.realm(KeyCloakConfig.realm).roles();
    }

    @Override
    public void addUser(UserDto user, HttpServletRequest request) {
        Keycloak keycloak = KeyCloakConfig.getInstance(request);

//        Get Realm Resource
        RealmResource realmResource = keycloak.realm(KeyCloakConfig.realm);
//        Get User Resource
        UsersResource usersResource = keycloak.realm(KeyCloakConfig.realm).users();
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

        if (user.getRegRequestFrom().equalsIgnoreCase(UserConst.WEB)) {
            kcUser.setEnabled(true);
        } else {
            kcUser.setEnabled(false);
        }
        Response response = usersResource.create(kcUser);

        String userId = CreatedResponseUtil.getCreatedId(response);
        userRepository.save(UserMapper.userDtoToUserEntity(user, userId));
        UserResource userResource = usersResource.get(userId);

//        Set Realm Role
        RoleRepresentation testerRealmRole = realmResource.roles().get("user").toRepresentation();
        userResource.roles().realmLevel().add(Arrays.asList(testerRealmRole));
    }

    @Override
    public void addRealmRole(RoleDto role, HttpServletRequest request) {
        Keycloak keycloak = KeyCloakConfig.getInstance(request);
        RoleRepresentation roleRep = new RoleRepresentation();
        roleRep.setName(role.getRoleName());
        roleRep.setDescription(role.getRoleDescription());
        keycloak.realm(KeyCloakConfig.realm).roles().create(roleRep);
    }

    @Override
    public ResponseEntity<Object> updateUserStatus(UserUpdateDto userUpdateDto, HttpServletRequest request) {
        Keycloak keycloak = KeyCloakConfig.getInstance(request);
//        Get Realm Resource
        RealmResource realmResource = keycloak.realm(KeyCloakConfig.realm);
//        Get User Resource
        UsersResource usersResource = keycloak.realm(KeyCloakConfig.realm).users();
        UserRepresentation user = usersResource.get(userUpdateDto.getUserId()).toRepresentation();
        user.setEnabled(userUpdateDto.getIsEnabled());
        usersResource.get(userUpdateDto.getUserId()).update(user);

        User oldUser = userRepository.findById(userUpdateDto.getUserId()).get();
        oldUser.setRegStatus(UserConst.REGISTRATION_COMPLETED);
        userRepository.save(oldUser);
        return ResponseEntity.ok(oldUser);
    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    private static ResponseEntity getAccessToken(){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", KeyCloakConfig.masterUserId);
        map.add("password", KeyCloakConfig.masterUserPassword);
        map.add("grant_type", "password");
        map.add("client_id", KeyCloakConfig.clientId);
        map.add("client_secret", KeyCloakConfig.clientSecret);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        return restTemplate.exchange("http://localhost:8180/auth/realms/emcare/protocol/openid-connect/token", HttpMethod.POST, entity, String.class);
    }

}
