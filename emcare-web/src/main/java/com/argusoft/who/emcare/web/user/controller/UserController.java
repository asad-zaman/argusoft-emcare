package com.argusoft.who.emcare.web.user.controller;

import com.argusoft.who.emcare.web.config.KeyCloakConfig;
import com.argusoft.who.emcare.web.user.dto.UserDto;
import java.util.Collections;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author jay
 */
@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/user/protected")
    public ResponseEntity<Object> getaAllClient() {
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/public")
    public ResponseEntity<Object> accessPublicAPI() {
        return ResponseEntity.ok("Success");
    }

    /**
     *
     * @param user
     * @return
     */
    @PostMapping("/user/add")
    public ResponseEntity<Object> addUser(@RequestBody UserDto user) {
//        UsersResource usersResource = KeyCloakConfig.getInstance().realm(KeyCloakConfig.realm).users();
//        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());
//
//        UserRepresentation kcUser = new UserRepresentation();
//        kcUser.setUsername(user.getEmail());
//        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
//        kcUser.setFirstName(user.getFirstName());
//        kcUser.setLastName(user.getLastName());
//        kcUser.setEmail(user.getEmail());
//        kcUser.setEnabled(true);
//        kcUser.setEmailVerified(false);
//        usersResource.create(kcUser);
        return ResponseEntity.ok("Success");
    }

//    private static CredentialRepresentation createPasswordCredentials(String password) {
//        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
//        passwordCredentials.setTemporary(false);
//        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
//        passwordCredentials.setValue(password);
//        return passwordCredentials;
//    }
}
