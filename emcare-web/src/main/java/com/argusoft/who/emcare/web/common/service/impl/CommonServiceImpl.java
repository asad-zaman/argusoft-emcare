package com.argusoft.who.emcare.web.common.service.impl;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.service.CommonService;
import org.springframework.stereotype.Service;

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


    @Override
    public String getDomainFormUrl(String url, String uri) {
        String domain = url.replace(uri, "");
        domain = domain.replace(CommonConstant.HTTPS, "");
        domain = domain.replace(CommonConstant.HTTP, "");
        return domain;
    }
}
