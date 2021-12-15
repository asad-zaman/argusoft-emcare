package com.argusoft.who.emcare.web.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jay
 */
@Component
public class KeyCloakConfig {

    static Keycloak keycloak = null;
    public final static String serverUrl = "http://localhost:8180/auth";
    public final static String realm = "emcare";
    public final static String clientId = "emcare";
    public final static String clientSecret = "4d9c181a-e677-49da-99fa-a1bab142dce5";
    public final static String userName = "jay";
    public final static String password = "argusadmin";
    public final static String masterUserId = "j@gmail.com";
    public final static String masterUserPassword = "argusadmin";

    @Autowired
    HttpServletRequest request;

    public Keycloak getInstance() {
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
