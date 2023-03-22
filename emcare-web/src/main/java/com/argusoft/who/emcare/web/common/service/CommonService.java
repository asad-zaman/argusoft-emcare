package com.argusoft.who.emcare.web.common.service;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 17/02/23  11:33 am
 */
public interface CommonService {

    public String getDomainFormUrl(String url, String uri);

    public String getTenantIdFromURL(String url, String uri);
}
