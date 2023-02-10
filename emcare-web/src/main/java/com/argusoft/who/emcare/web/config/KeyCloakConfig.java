package com.argusoft.who.emcare.web.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author jay
 */
@Component
public class KeyCloakConfig {

    @Value("${keycloak.auth-server-url}")
    public String serverURL;
    @Value("${keycloak.credentials.secret}")
    public String clientSecret;
    @Value("${config.keycloak.clientId}")
    public String clientId;
    @Value("${config.keycloak.username}")
    public String username;
    @Value("${config.keycloak.password}")
    public String password;
    @Value("${config.keycloak.masterUserId}")
    public String masterUserId;
    @Value("${config.keycloak.masterUserPassword}")
    public String masterUserPassword;
    @Value("${keycloak.realm}")
    public String realm;
    @Autowired
    HttpServletRequest request;


    Keycloak keycloak = null;

    public String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", masterUserId);
        map.add("password", masterUserPassword);
        map.add("grant_type", "password");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        String url = serverURL + "/realms/" + realm + "/protocol/openid-connect/token";
        String token = null;
        Map<String, Object> accessToken = restTemplate.postForObject(url, entity, Map.class);
        if (accessToken != null) {
            token = accessToken.get("access_token").toString();
        }
        return token;
    }

    public Keycloak getInstance() {
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        keycloak = KeycloakBuilder.builder()
                .serverUrl(serverURL)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .username(username)
                .password(password)
                .clientId(clientId)
                .authorization(context.getTokenString())
                .clientSecret(clientSecret)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();
        return keycloak;
    }

    public Keycloak getInstanceByAuth() {
        KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        return KeycloakBuilder.builder()
                .serverUrl(serverURL)
                .realm(realm)
                .authorization(context.getTokenString())
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(20).build())
                .build();
    }

    public Keycloak getInsideInstance() {
        String token = getAccessToken();
        return KeycloakBuilder.builder()
                .serverUrl(serverURL)
                .realm(realm)
                .authorization(token)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(20).build())
                .build();
    }

    public Keycloak getKeyCloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(serverURL)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .username(username)
                .password(password)
                .clientId(clientId)
                .authorization(getAccessToken())
                .clientSecret(clientSecret)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();
    }
}
