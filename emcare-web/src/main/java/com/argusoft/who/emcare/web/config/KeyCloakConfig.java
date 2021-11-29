package com.argusoft.who.emcare.web.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

/**
 *
 * @author jay
 */
public class KeyCloakConfig {

    static Keycloak keycloak = null;
    public final static String serverUrl = "http://localhost:8180/auth";
    public final static String realm = "emcare";
    public final static String clientId = "login-app";
    public final static String clientSecret = "50fe2579-ea20-4cf2-b0d2-e219f67dfbb4";
    public final static String userName = "jay";
    public final static String password = "argusadmin";

    public static Keycloak getInstance() {
        if (keycloak == null) {

            keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(userName)
                    .password(password)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .resteasyClient(new ResteasyClientBuilder()
                            .connectionPoolSize(10)
                            .build())
                    .build();
        }
        return keycloak;
    }
}
