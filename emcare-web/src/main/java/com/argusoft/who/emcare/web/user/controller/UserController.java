package com.argusoft.who.emcare.web.user.controller;

import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.location.service.LocationService;
import com.argusoft.who.emcare.web.user.dto.*;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

/**
 * @author jay
 */
@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    LocationService locationConfigService;

    @GetMapping("/user")
    public ResponseEntity<Object> getCurrentLoggedInUser() {
        return userService.getCurrentUser();
    }

    @GetMapping("/user/all")
    public ResponseEntity<Object> getAllUser(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getAllUserWithMultiLocation(request));
    }

    @GetMapping("/user/page")
    public ResponseEntity<Object> getUserPage(HttpServletRequest request,
                                              @RequestParam(value = "pageNo") Integer pageNo,
                                              @Nullable @RequestParam(value = "search", required = false) String searchString,
                                              @RequestParam(value = "filter", required = false) Boolean filter) {
        return ResponseEntity.ok(userService.getUserPage(request, pageNo, searchString, filter));
    }

    @GetMapping("/user/signedup")
    public ResponseEntity<Object> getAllSignedUpUser(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getAllSignedUpUser(request));
    }

    @GetMapping("/role")
    public ResponseEntity<Object> getAllRoles(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getAllRoles(request));
    }

    @GetMapping("/signup/roles")
    public ResponseEntity<Object> getAllRolesForSignup(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getAllRolesForSignUp(request).list());
    }

    @GetMapping("/signup/location")
    public ResponseEntity<Object> getAllLocation() {
        return locationConfigService.getAllLocation();
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> addUser(@RequestBody UserDto user, HttpServletRequest request) {
        return userService.signUp(user, request);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Object> userLogin(@RequestBody LoginRequestDto loginCred, HttpServletRequest request) {
        return userService.userLogin(loginCred, request);
    }

    @PostMapping("/user/add")
    public ResponseEntity<Object> addUserFromWeb(@RequestBody UserDto user, HttpServletRequest request) {
        return userService.addUser(user, request);
    }

    @PostMapping("/role/add")
    public ResponseEntity<Object> addRealmRole(@RequestBody RoleDto role) {
        userService.addRealmRole(role);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/user/{userId}")
    public MultiLocationUserListDto getUserById(@PathVariable(value = "userId") String userId) {
        return userService.getUserDtoById(userId);
    }

    @GetMapping("/user/role/{userId}")
    public ResponseEntity<Object> getUserRoleById(@PathVariable(value = "userId") String userId) {
        return userService.getUserRolesById(userId);
    }

    @PutMapping("/user/update/{userId}")
    public ResponseEntity<Object> updateUser(
            @RequestBody UserDto userDto,
            @PathVariable(value = "userId") String userId,
            HttpServletRequest request) {
        return userService.updateUser(userDto, userId, request);
    }

    @PutMapping("/user/update/password/{userId}")
    public ResponseEntity<Object> updatePassword(@RequestBody UserDto userDto, @PathVariable(value = "userId") String userId) {
        return userService.updatePassword(userDto, userId);
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<Object> getRoleById(@PathVariable(value = "roleId") String roleId, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getRoleByName(roleId, request));
    }

    @PutMapping("/role/update")
    public ResponseEntity<Object> updateRole(@RequestBody RoleUpdateDto roleUpdateDto) {
        return userService.updateRole(roleUpdateDto);
    }

    @PostMapping("/user/status/change")
    public ResponseEntity<Object> changeUserStatus(@RequestBody UserUpdateDto userUpdateDto) {
        return userService.updateUserStatus(userUpdateDto);
    }

    @GetMapping("user/locationId/{locationId}")
    public PageDto getUsersUnderLocation(@PathVariable(value = "locationId") Object locationId,
                                         @RequestParam(value = "pageNo") Integer pageNo,@RequestParam(value = "filter", required = false) Boolean filter) {
        return userService.getUsersUnderLocation(locationId, pageNo,filter);
    }

    @GetMapping("user/check/email")
    public ResponseEntity checkEmailAlreadyExist(@RequestParam(value = "emailId") String emailId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.checkEmailIdExist(emailId));
    }
}
