package com.argusoft.who.emcare.web.common.service.impl;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.service.CommonService;
import com.argusoft.who.emcare.web.tenant.entity.TenantConfig;
import com.argusoft.who.emcare.web.tenant.repository.TenantConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 17/02/23  11:33 am
 */
@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    TenantConfigRepository tenantConfigRepository;


    @Override
    public String getDomainFormUrl(String url, String uri) {
        String domain = url.replace(uri, "");
        domain = domain.replace(CommonConstant.HTTPS, "");
        domain = domain.replace(CommonConstant.HTTP, "");
        return domain;
    }

    @Override
    public String getTenantIdFromURL(String url, String uri) {
        String domain = url.replace(uri, "");
        domain = domain.replace(CommonConstant.HTTPS, "");
        domain = domain.replace(CommonConstant.HTTP, "");
        Optional<TenantConfig> tenantConfig = tenantConfigRepository.findByDomain(domain);
        if (tenantConfig.isPresent()) {
            return tenantConfig.get().getTenantId();
        }
        return CommonConstant.DEFAULT_TENANT_ID;
    }
}
