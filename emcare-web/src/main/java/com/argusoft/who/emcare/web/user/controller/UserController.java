package com.argusoft.who.emcare.web.user.controller;

import com.argusoft.who.emcare.web.config.KeyCloakConfig;
import com.argusoft.who.emcare.web.secuirty.EmCareSecurityUser;
import com.argusoft.who.emcare.web.user.dto.UserDto;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author jay
 */
@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    EmCareSecurityUser emCareSecurityUser;

    @Autowired
    UserService userService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Object> getCurrentLoggedInUser() {
        return ResponseEntity.ok(emCareSecurityUser.getLoggedInUser());
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllUser(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getAllUserResource(request).list());
    }

    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllRoles(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getAllRoles(request).list());
    }

    /**
     * @param user
     * @return
     */
    @PostMapping("/add")
    public ResponseEntity<Object> addUser(@RequestBody UserDto user, HttpServletRequest request) {

        RealmResource realmResource = KeyCloakConfig.getInstanceByAuth(request).realm(KeyCloakConfig.realm);
        UsersResource usersResource = KeyCloakConfig.getInstanceByAuth(request).realm(KeyCloakConfig.realm).users();
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(user.getEmail());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setFirstName(user.getFirstName());
        kcUser.setLastName(user.getLastName());
        kcUser.setEmail(user.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);
        javax.ws.rs.core.Response response = usersResource.create(kcUser);
        String userId = CreatedResponseUtil.getCreatedId(response);

        RoleRepresentation testerRealmRole = realmResource.roles()//
                .get("user").toRepresentation();
//
//        // Assign realm role tester to user
        UserResource userResource = usersResource.get(userId);
        userResource.roles().realmLevel() //
                .add(Arrays.asList(testerRealmRole));

//        ClientRepresentation app1Client = realmResource.clients() //
//                .findByClientId("login-app").get(0);
////
//        // Get client level role (requires view-clients role)
//        RoleRepresentation userClientRole = realmResource.clients().get("login-app") //
//                .roles().get("user").toRepresentation();
////
////        // Assign client level role to user
//        userResource.roles() //
//                .clientLevel("login-app").add(Arrays.asList(userClientRole));
        return ResponseEntity.ok("Success");
    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }
}
