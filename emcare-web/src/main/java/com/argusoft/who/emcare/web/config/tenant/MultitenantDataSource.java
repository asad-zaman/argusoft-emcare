package com.argusoft.who.emcare.web.config.tenant;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 08/02/23  2:03 pm
 */
public class MultitenantDataSource extends AbstractRoutingDataSource {

    @Override
    protected String determineCurrentLookupKey() {
        System.out.println(TenantContext.getCurrentTenant() + "---------> TenantId");
        return TenantContext.getCurrentTenant();
    }
}
