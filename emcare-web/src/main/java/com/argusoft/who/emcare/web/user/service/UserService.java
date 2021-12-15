package com.argusoft.who.emcare.web.user.service;

import com.argusoft.who.emcare.web.user.dto.RoleDto;
import com.argusoft.who.emcare.web.user.dto.RoleUpdateDto;
import com.argusoft.who.emcare.web.user.dto.UserDto;
import com.argusoft.who.emcare.web.user.dto.UserUpdateDto;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessToken;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jay
 */
public interface UserService {

    public AccessToken getCurrentUser();

    public UsersResource getAllUserResource(HttpServletRequest request);

    public RolesResource getAllRoles(HttpServletRequest request);

    public void signUp(UserDto user);

    public void addUser(UserDto user);

    public void addRealmRole(RoleDto role);

    public ResponseEntity<Object> updateUserStatus(UserUpdateDto userUpdateDto);

    public ResponseEntity<Object> getUserRolesById(String useraId);

    public ResponseEntity<Object> updateRole(RoleUpdateDto roleUpdateDto);
}
