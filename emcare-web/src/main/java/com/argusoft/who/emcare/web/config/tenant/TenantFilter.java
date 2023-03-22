package com.argusoft.who.emcare.web.config.tenant;

import com.argusoft.who.emcare.web.common.service.CommonService;
import com.argusoft.who.emcare.web.tenant.entity.TenantConfig;
import com.argusoft.who.emcare.web.tenant.repository.TenantConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 08/02/23  2:05 pm
 */
@Component
@Order(1)
class TenantFilter implements Filter {

    @Autowired
    CommonService commonService;

    @Autowired
    TenantConfigRepository tenantConfigRepository;

    @Value("${defaultTenant}")
    private String defaultTenant;

    @Value("${defaultTenantDomain}")
    private String defaultTenantDomain;

    private Map<String, String> TENANT_ID_MAP = new HashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        System.out.println(req.getRequestURL() + "====" + req.getRequestURI());
        if (TENANT_ID_MAP.isEmpty()) {
            addTenantDetailsInMap();
        }
        String domain = null;
        String tenantId = null;
//        if (req.getHeader("User-Agent").contains("okhttp")) {
//            if (!req.getRequestURI().contains("api/auth/login")) {
//                KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
//
//            }
//        } else {
        domain = commonService.getDomainFormUrl(req.getRequestURL().toString(), req.getRequestURI());
        tenantId = TENANT_ID_MAP.get(domain.trim());
        if (Objects.isNull(tenantId)) {
            addTenantDetailsInMap();
            domain = commonService.getDomainFormUrl(req.getRequestURL().toString(), req.getRequestURI());
            tenantId = TENANT_ID_MAP.get(domain.trim());
        }
//        }

        System.out.println(tenantId + "    Domain Name");
        if (Objects.isNull(tenantId)) {
            throw new DataSourceLookupFailureException(
                    "No DataSource with name '" + domain + "' registered");
        }
        TenantContext.setCurrentTenant(tenantId);
        try {
            chain.doFilter(request, response);
        } catch (Exception ex) {

        } finally {
            TenantContext.clearTenant();
        }
    }

    public void addTenantDetailsInMap() {
        TENANT_ID_MAP.put(defaultTenantDomain, defaultTenant);
        List<TenantConfig> tenantConfigList = tenantConfigRepository.findAll();
        for (TenantConfig tenantConfig : tenantConfigList) {
            TENANT_ID_MAP.put(tenantConfig.getDomain(), tenantConfig.getTenantId());
        }
    }
}
