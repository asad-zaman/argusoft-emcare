package com.argusoft.who.emcare.web.user.controller;

import com.argusoft.who.emcare.web.location.service.LocationService;
import com.argusoft.who.emcare.web.user.dto.RoleDto;
import com.argusoft.who.emcare.web.user.dto.RoleUpdateDto;
import com.argusoft.who.emcare.web.user.dto.UserDto;
import com.argusoft.who.emcare.web.user.dto.UserUpdateDto;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @GetMapping("/user/all")
    public ResponseEntity<Object> getAllUser(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getAllUser(request));
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

    /**
     * @param user
     * @return
     */
    @PostMapping("/signup")
    public ResponseEntity<Object> addUser(@RequestBody UserDto user) {
        return userService.signUp(user);
    }

    @PostMapping("/user/add")
    public ResponseEntity<Object> addUserFromWeb(@RequestBody UserDto user) {
        userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/role/add")
    public ResponseEntity<Object> addRealmRole(@RequestBody RoleDto role) {
        userService.addRealmRole(role);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable String userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/user/role/{userId}")
    public ResponseEntity<Object> getUserRoleById(@PathVariable String userId) {
        return userService.getUserRolesById(userId);
    }
    
    @PutMapping("/user/update/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto,
            @PathVariable String userId
    ) {
        return userService.updateUser(userDto, userId);
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<Object> getRoleById(@PathVariable String roleId, HttpServletRequest request) {
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
}
