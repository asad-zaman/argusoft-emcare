package com.argusoft.who.emcare.web.user.controller;

import com.argusoft.who.emcare.web.config.KeyCloakConfig;
import com.argusoft.who.emcare.web.secuirty.EmCareSecurityUser;
import com.argusoft.who.emcare.web.user.dto.UserDto;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.argusoft.who.emcare.web.user.service.UserService;

/**
 *
 * @author jay
 */
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

    /**
     *
     * @param user
     * @return
     */
    @PostMapping("/add")
    public ResponseEntity<Object> addUser(@RequestBody UserDto user) {
        UsersResource usersResource = KeyCloakConfig.getInstance().realm(KeyCloakConfig.realm).users();
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());

        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(user.getEmail());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setFirstName(user.getFirstName());
        kcUser.setLastName(user.getLastName());
        kcUser.setEmail(user.getEmail());
        kcUser.setEnabled(true);
//        kcUser.setRealmRoles(realmRoles);
        kcUser.setEmailVerified(false);
        usersResource.create(kcUser);
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
