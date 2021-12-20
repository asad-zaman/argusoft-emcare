package com.argusoft.who.emcare.web.user.controller;

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

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<Object> getCurrentLoggedInUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @RequestMapping(value = "/user/all", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllUser(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getAllUserResource(request).list());
    }

    @RequestMapping(value = "/user/roles", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllRoles(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getAllRoles(request).list());
    }

    @RequestMapping(value = "/signup/roles", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllRolesForSignup(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getAllRolesForSignUp(request).list());
    }

    /**
     * @param user
     * @return
     */
    @PostMapping("/signup")
    public ResponseEntity<Object> addUser(@RequestBody UserDto user) {
        userService.signUp(user);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/user/add")
    public ResponseEntity<Object> addUserFromWeb(@RequestBody UserDto user) {
        userService.addUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/user/role/add")
    public ResponseEntity<Object> addRealmRole(@RequestBody RoleDto role) {
        userService.addRealmRole(role);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/user/role/{userId}")
    public ResponseEntity<Object> getUserRoleById(@PathVariable String userId) {
        return userService.getUserRolesById(userId);
    }

    @PutMapping("/user/role/update")
    public ResponseEntity<Object> updateRole(@RequestBody RoleUpdateDto roleUpdateDto) {
        return userService.updateRole(roleUpdateDto);
    }

    @PostMapping("/user/status/change")
    public ResponseEntity<Object> changeUserStatus(@RequestBody UserUpdateDto userUpdateDto) {
        return userService.updateUserStatus(userUpdateDto);
    }
}
