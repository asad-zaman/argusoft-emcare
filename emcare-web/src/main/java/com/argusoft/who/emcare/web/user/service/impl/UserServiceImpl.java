package com.argusoft.who.emcare.web.user.service.impl;

import com.argusoft.who.emcare.web.config.KeyCloakConfig;
import com.argusoft.who.emcare.web.user.dto.RoleDto;
import com.argusoft.who.emcare.web.user.dto.UserDto;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author jay
 */
@Service
public class UserServiceImpl implements UserService {

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
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);
        Response response = usersResource.create(kcUser);


        String userId = CreatedResponseUtil.getCreatedId(response);
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

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

}
