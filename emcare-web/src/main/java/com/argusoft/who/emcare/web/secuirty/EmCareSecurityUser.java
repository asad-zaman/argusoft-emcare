package com.argusoft.who.emcare.web.secuirty;

import com.argusoft.who.emcare.web.config.KeyCloakConfig;
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
    private Set<String> LoggedInUserRole;

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
        return LoggedInUserRole;
    }

    public void setLoggedInUserRole(Set<String> loggedInUserRole) {
        LoggedInUserRole = loggedInUserRole;
    }

    @Autowired
    private HttpServletRequest request;

    public AccessToken getLoggedInUser() {
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
        KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
        AccessToken accessToken = session.getToken();
        setLoggedInUserId(accessToken.getSubject());
        setLoggedInUserName(accessToken.getPreferredUsername());
//        setLoggedInUserRole(accessToken.getResourceAccess().get(KeyCloakConfig.clientId).getRoles());
        return accessToken;
    }
}
