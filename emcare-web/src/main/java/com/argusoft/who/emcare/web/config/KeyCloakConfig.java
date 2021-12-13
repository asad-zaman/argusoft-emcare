package com.argusoft.who.emcare.web.config;

import javax.servlet.http.HttpServletRequest;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.KeycloakSecurityContext;
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
    public final static String clientId = "emcare";
    public final static String clientSecret = "bd536aad-e5dc-456f-86c5-90b8ea5ae04d";
    public final static String userName = "emcare";
    public final static String password = "argusadmin";

    public static Keycloak getInstance(HttpServletRequest request) {
        if (keycloak == null) {
            KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(KeyCloakConfig.serverUrl)
                    .realm(KeyCloakConfig.realm)
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(KeyCloakConfig.userName)
                    .password(KeyCloakConfig.password)
                    .clientId(KeyCloakConfig.clientId)
                    .authorization(context.getTokenString())
                    .clientSecret(KeyCloakConfig.clientSecret)
                    .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                    .build();
        }
        return keycloak;
    }

    public static Keycloak getInstanceByAuth(HttpServletRequest request) {
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());

        Keycloak keycloakInstance = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .authorization(context.getTokenString())
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(20).build())
                .build();
        return keycloakInstance;
    }
}
