package com.argusoft.who.emcare.web.user.service;

import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jay
 */
public interface UserService {

    public UsersResource getAllUserResource(HttpServletRequest request);

    public RolesResource getAllRoles(HttpServletRequest request);
}
