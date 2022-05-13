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

    @Autowired
    HttpServletRequest request;

    Keycloak keycloak = null;
    public static final String SERVER_URL = "http://192.1.200.197:8180/auth";
    public static final String CLIENT_SECRET = "b5a37bde-8d54-4837-a8dc-12e1f808e26e";
    public static final String CLIENT_ID = "emcare";
    public static final String REALM = "emcare";
    public static final String USER_NAME = "emcare_admin";
    public static final String PASSWORD = "argusadmin";
    public static final String MASTER_USER_ID = "emcare@gmail.com";
    public static final String MASTER_USER_PASSWORD = "argusadmin";

    public Keycloak getInstance() {
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
        return keycloak;
    }

    public Keycloak getInstanceByAuth() {
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        return KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(REALM)
                .authorization(context.getTokenString())
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(20).build())
                .build();
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
        String url = SERVER_URL + "/realms/" + REALM + "/protocol/openid-connect/token";
        String token = null;
        AccessTokenForUser accessToken = restTemplate.postForObject(url, entity, AccessTokenForUser.class);
        if (accessToken != null) {
            token = accessToken.getAccess_token();
        }
        return token;
    }

    public Keycloak getInsideInstance() {
        String token = getAccessToken();
        return KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(REALM)
                .authorization(token)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(20).build())
                .build();
    }
}
