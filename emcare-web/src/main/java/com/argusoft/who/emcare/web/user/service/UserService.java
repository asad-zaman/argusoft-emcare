package com.argusoft.who.emcare.web.user.service;

import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.user.dto.*;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author jay
 */
public interface UserService {

    public ResponseEntity getCurrentUser();

    public List<UserListDto> getAllUser(HttpServletRequest request);

    public List<MultiLocationUserListDto> getAllUserWithMultiLocation(HttpServletRequest request);

    public PageDto getUserPage(HttpServletRequest request, Integer pageNo, String searchString, Boolean filter);

    public List<UserListDto> getAllSignedUpUser(HttpServletRequest request);

    public List<RoleRepresentation> getAllRoles(HttpServletRequest request);

    public RoleRepresentation getRoleByName(String roleId, HttpServletRequest request);

    public RolesResource getAllRolesForSignUp(HttpServletRequest request);

    public ResponseEntity<Object> signUp(UserDto user, HttpServletRequest request);

    public ResponseEntity<Object> userLogin(LoginRequestDto loginCred, HttpServletRequest request);

    public ResponseEntity<Object> addUser(UserDto user, HttpServletRequest request);

    public void addRealmRole(RoleDto role);

    public ResponseEntity<Object> updateUserStatus(UserUpdateDto userUpdateDto);

    public UserRepresentation getUserById(String userId);

    public MultiLocationUserListDto getUserDtoById(String userId);

    public ResponseEntity<Object> getUserRolesById(String userId);

    public ResponseEntity<Object> updateUser(UserDto userDto, String userId, HttpServletRequest request);

    public ResponseEntity<Object> updatePassword(UserDto userDto, String userId);

    public ResponseEntity<Object> updateRole(RoleUpdateDto roleUpdateDto);

    public String getRoleIdByName(String roleName);

    public String getRoleNameById(String roleId);

    public PageDto getUsersUnderLocation(Object locationId, Integer pageNo, Boolean filter);

    public UserRepresentation getUserByEmailId(String emailId);

    public UserRepresentation resetPassword(String emailId, String password);

    public Map<String, Object> checkEmailIdExist(String email);

    public ResponseEntity<Object> addUserForCountry(UserDto user, String tenantId);

    public void removeRole(String roleName) throws Exception;

    public void removeUser(String email) throws Exception;

    public List<String> getCurrentUserFacility();


}
