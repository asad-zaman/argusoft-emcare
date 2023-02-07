package com.argusoft.who.emcare.web.secuirty;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author jay
 */
@Component
public class EmCareSecurityUser {

    private String loggedInUserId;
    private String loggedInUserName;
    private Set<String> loggedInUserRole;

    public String getLoggedInUserId() {
        getLoggedInUser();
        return loggedInUserId;
    }

    public void setLoggedInUserId(String loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
    }

    public String getLoggedInUserName() {
        getLoggedInUser();
        return loggedInUserName;
    }

    public void setLoggedInUserName(String loggedInUserName) {
        this.loggedInUserName = loggedInUserName;
    }

    public Set<String> getLoggedInUserRole() {
        getLoggedInUser();
        return loggedInUserRole;
    }

    public void setLoggedInUserRole(Set<String> loggedInUserRole) {
        this.loggedInUserRole = loggedInUserRole;
    }

    @Autowired
    private HttpServletRequest request;

    public AccessToken getLoggedInUser() {
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
        @SuppressWarnings("rawtypes") KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
        AccessToken accessToken = session.getToken();
        setLoggedInUserId(accessToken.getSubject());
        setLoggedInUserName(accessToken.getPreferredUsername());
        return accessToken;
    }
}
