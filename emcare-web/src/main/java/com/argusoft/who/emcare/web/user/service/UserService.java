package com.argusoft.who.emcare.web.user.service;

import com.argusoft.who.emcare.web.config.KeyCloakConfig;
import com.argusoft.who.emcare.web.user.dto.RoleDto;
import com.argusoft.who.emcare.web.user.dto.RoleUpdateDto;
import com.argusoft.who.emcare.web.user.dto.UserDto;
import com.argusoft.who.emcare.web.user.dto.UserUpdateDto;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
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

    public RolesResource getAllRolesForSignUp(HttpServletRequest request);

    public void signUp(UserDto user);

    public void addUser(UserDto user);

    public void addRealmRole(RoleDto role);

    public ResponseEntity<Object> updateUserStatus(UserUpdateDto userUpdateDto);

    public ResponseEntity<Object> getUserRolesById(String useraId);

    public ResponseEntity<Object> updateRole(RoleUpdateDto roleUpdateDto);

    public default Keycloak getKeyCloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(KeyCloakConfig.SERVER_URL)
                .realm(KeyCloakConfig.REALM)
                .grantType(OAuth2Constants.PASSWORD)
                .username(KeyCloakConfig.USER_NAME)
                .password(KeyCloakConfig.PASSWORD)
                .clientId(KeyCloakConfig.CLIENT_ID)
                .authorization(KeyCloakConfig.getAccessToken())
                .clientSecret(KeyCloakConfig.CLIENT_SECRET)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();
    }
}
