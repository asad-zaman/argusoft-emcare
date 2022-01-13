package com.argusoft.who.emcare.web.config;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;

@EnableJpaAuditing(auditorAwareRef = "createAuditorProvider")
@Configuration
public class EntityAuditConfig {
    @Bean
    public AuditorAware<String> createAuditorProvider() {
        return new SecurityAuditor();
    }

    @Bean
    public AuditingEntityListener createAuditingListener() {
        return new AuditingEntityListener();
    }

    public class SecurityAuditor implements AuditorAware<String> {

        @Autowired
        private HttpServletRequest request;

        @Override
        public Optional<String> getCurrentAuditor() {
            KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
            KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
            AccessToken accessToken = session.getToken();
            return Optional.ofNullable(accessToken.getSubject());
        }
    }
}
