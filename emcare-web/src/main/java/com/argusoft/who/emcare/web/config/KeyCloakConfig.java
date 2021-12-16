package com.argusoft.who.emcare.web.config;

import com.argusoft.who.emcare.web.user.dto.AccessTokenForUser;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jay
 */
@Component
public class KeyCloakConfig {

    static Keycloak keycloak = null;
    public final static String SERVER_URL = "http://localhost:8180/auth";
    public final static String REALM = "emcare";
    public final static String CLIENT_ID = "emcare";
    public final static String CLIENT_SECRET = "4d9c181a-e677-49da-99fa-a1bab142dce5";
    public final static String USER_NAME = "jay";
    public final static String PASSWORD = "argusadmin";
    public final static String MASTER_USER_ID = "j@gmail.com";
    public final static String MASTER_USER_PASSWORD = "argusadmin";

    @Autowired
    HttpServletRequest request;

    public Keycloak getInstance() {
        if (keycloak == null) {
            KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(KeyCloakConfig.SERVER_URL)
                    .realm(KeyCloakConfig.REALM)
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(KeyCloakConfig.USER_NAME)
                    .password(KeyCloakConfig.PASSWORD)
                    .clientId(KeyCloakConfig.CLIENT_ID)
                    .authorization(context.getTokenString())
                    .clientSecret(KeyCloakConfig.CLIENT_SECRET)
                    .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                    .build();
        }
        return keycloak;
    }

    public static Keycloak getInstanceByAuth(HttpServletRequest request) {
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());

        Keycloak keycloakInstance = KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(REALM)
                .authorization(context.getTokenString())
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(20).build())
                .build();
        return keycloakInstance;
    }

    public static String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", MASTER_USER_ID);
        map.add("password", MASTER_USER_PASSWORD);
        map.add("grant_type", "password");
        map.add("client_id", CLIENT_ID);
        map.add("client_secret", CLIENT_SECRET);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        AccessTokenForUser response = restTemplate.postForObject("http://localhost:8180/auth/realms/emcare/protocol/openid-connect/token", entity, AccessTokenForUser.class);
        String token = response.getAccess_token();
        return token;
    }
}
