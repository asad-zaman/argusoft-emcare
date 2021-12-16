package com.argusoft.who.emcare.web.user.service.impl;

import com.argusoft.who.emcare.web.config.KeyCloakConfig;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

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

}
