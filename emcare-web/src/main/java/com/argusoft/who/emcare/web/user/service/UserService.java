package com.argusoft.who.emcare.web.user.service;

import com.argusoft.who.emcare.web.user.dto.RoleDto;
import com.argusoft.who.emcare.web.user.dto.UserDto;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jay
 */
public interface UserService {

    public UsersResource getAllUserResource(HttpServletRequest request);

    public RolesResource getAllRoles(HttpServletRequest request);

    public void addUser(UserDto user, HttpServletRequest request);

    public void addRealmRole(RoleDto role, HttpServletRequest request);
}
