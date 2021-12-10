package com.argusoft.who.emcare.web.user.service;

import javax.servlet.http.HttpServletRequest;
import org.keycloak.admin.client.resource.UsersResource;

/**
 *
 * @author jay
 */
public interface UserService {
 
    public UsersResource getAllUserResource(HttpServletRequest request);
}
