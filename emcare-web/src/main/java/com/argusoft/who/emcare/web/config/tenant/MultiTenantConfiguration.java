package com.argusoft.who.emcare.web.config.tenant;

import com.argusoft.who.emcare.web.tenant.entity.TenantConfig;
import com.argusoft.who.emcare.web.tenant.repository.TenantConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 06/02/23  11:56 am
 */
@Configuration
public class MultiTenantConfiguration {

    @Autowired
    TenantConfigRepository tenantConfigRepository;
    @Value("${defaultTenant}")
    private String defaultTenant;

    @Bean
    @ConfigurationProperties(prefix = "tenants")
    public DataSource dataSource() {
        Map<Object, Object> resolvedDataSources = new HashMap<>();

        List<TenantConfig> tenantConfigs = tenantConfigRepository.findAll();
        for (TenantConfig tenantConfig : tenantConfigs) {
            DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
            String tenantId = tenantConfig.getTenantId();
            dataSourceBuilder.driverClassName("org.postgresql.Driver");
            dataSourceBuilder.username(tenantConfig.getUsername());
            dataSourceBuilder.password(tenantConfig.getPassword());
            dataSourceBuilder.url(tenantConfig.getUrl());
            resolvedDataSources.put(tenantId, dataSourceBuilder.build());
        }

        AbstractRoutingDataSource dataSource = new MultiTenantDataSource();
        dataSource.setDefaultTargetDataSource(resolvedDataSources.get(defaultTenant));
        dataSource.setTargetDataSources(resolvedDataSources);

        dataSource.afterPropertiesSet();
        return dataSource;
    }
}
