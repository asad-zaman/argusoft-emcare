package com.argusoft.who.emcare.web.user.service;

import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.config.KeyCloakConfig;
import com.argusoft.who.emcare.web.user.dto.*;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author jay
 */
public interface UserService {

    public UserMasterDto getCurrentUser();

    public List<UserListDto> getAllUser(HttpServletRequest request);

    public PageDto getUserPage(HttpServletRequest request, Integer pageNo,String searchString);

    public List<UserListDto> getAllSignedUpUser(HttpServletRequest request);

    public List<RoleRepresentation> getAllRoles(HttpServletRequest request);

    public RoleRepresentation getRoleByName(String roleId, HttpServletRequest request);

    public RolesResource getAllRolesForSignUp(HttpServletRequest request);

    public ResponseEntity<Object> signUp(UserDto user);

    public ResponseEntity<Object> addUser(UserDto user);

    public void addRealmRole(RoleDto role);

    public ResponseEntity<Object> updateUserStatus(UserUpdateDto userUpdateDto);

    public UserRepresentation getUserById(String userId);

    public UserListDto getUserDtoById(String userId);

    public ResponseEntity<Object> getUserRolesById(String userId);

    public ResponseEntity<Object> updateUser(UserDto userDto, String userId);
    
    public ResponseEntity<Object> updatePassword(UserDto userDto, String userId);

    public ResponseEntity<Object> updateRole(RoleUpdateDto roleUpdateDto);

    public String getRoleIdByName(String roleName);

    public String getRoleNameById(String roleId);

    public PageDto getUsersUnderLocation(Integer locationId,Integer pageNo);

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
