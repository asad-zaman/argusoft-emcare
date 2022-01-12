package com.argusoft.who.emcare.web.user.controller;

import com.argusoft.who.emcare.web.location.service.LocationService;
import com.argusoft.who.emcare.web.user.dto.*;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @GetMapping("/user/all")
    public ResponseEntity<Object> getAllUser(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getAllUser(request));
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
    public ResponseEntity<Object> addUser(@RequestBody UserDto user) {
        return userService.signUp(user);
    }

    @PostMapping("/user/add")
    public ResponseEntity<Object> addUserFromWeb(@RequestBody UserDto user) {
        return userService.addUser(user);
    }

    @PostMapping("/role/add")
    public ResponseEntity<Object> addRealmRole(@RequestBody RoleDto role) {
        userService.addRealmRole(role);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/user/{userId}")
    public UserListDto getUserById(@PathVariable(value = "userId") String userId) {
        return userService.getUserDtoById(userId);
    }

    @GetMapping("/user/role/{userId}")
    public ResponseEntity<Object> getUserRoleById(@PathVariable(value = "userId") String userId) {
        return userService.getUserRolesById(userId);
    }

    @PutMapping("/user/update/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable(value = "userId") String userId) {
        return userService.updateUser(userDto, userId);
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
    public List<UserListDto> getUsersUnderLocation(@PathVariable(value = "locationId") Integer locationId) {
        return userService.getUsersUnderLocation(locationId);
    }
}
