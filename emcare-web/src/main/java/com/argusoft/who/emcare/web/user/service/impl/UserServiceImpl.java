package com.argusoft.who.emcare.web.user.service.impl;

import com.argusoft.who.emcare.web.config.KeyCloakConfig;
import javax.servlet.http.HttpServletRequest;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.stereotype.Service;
import com.argusoft.who.emcare.web.user.service.UserService;

/**
 *
 * @author jay
 */
@Service
public class UserServiceImpl implements UserService {

    @Override
    public UsersResource getAllUserResource(HttpServletRequest request) {
        Keycloak keycloak = KeyCloakConfig.getInstanceByAuth(request);
        return keycloak.realm(KeyCloakConfig.realm).users();
    }

}
