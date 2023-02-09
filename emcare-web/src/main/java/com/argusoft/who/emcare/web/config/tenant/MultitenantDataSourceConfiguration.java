package com.argusoft.who.emcare.web.config.tenant;

import com.argusoft.who.emcare.web.tenant.entity.TenantConfig;
import com.argusoft.who.emcare.web.tenant.repository.TenantConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
 * @since - 09/02/23  10:31 am
 */
@Component
public class MultitenantDataSourceConfiguration {

    @Autowired
    TenantConfigRepository tenantConfigRepository;

    @Autowired
    DataSource dataSource;

    @Bean
    public DataSource addDataSourceDynamic() {
        List<TenantConfig> tenantConfigList = tenantConfigRepository.findAll();
        Map<Object, Object> resolvedDataSources = new HashMap<>();

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        String tenantId = "tenantId1";
        dataSourceBuilder.driverClassName("org.postgresql.Driver");
        dataSourceBuilder.username("postgres");
        dataSourceBuilder.password("argusadmin");
        dataSourceBuilder.url("jdbc:postgresql://192.1.200.197:5432/emcare");
        resolvedDataSources.put(tenantId, dataSourceBuilder.build());
        for (TenantConfig tenantConfig : tenantConfigList) {
            DataSourceBuilder dataSourceBuilder1 = DataSourceBuilder.create();
            dataSourceBuilder1.driverClassName("org.postgresql.Driver");
            dataSourceBuilder1.username(tenantConfig.getUsername());
            dataSourceBuilder1.password(tenantConfig.getPassword());
            dataSourceBuilder1.url(tenantConfig.getUrl());
            resolvedDataSources.put(tenantConfig.getTenantId(), dataSourceBuilder1.build());
        }
        AbstractRoutingDataSource dataSource1 = (AbstractRoutingDataSource) dataSource;
        dataSource1.setDefaultTargetDataSource(resolvedDataSources.get("tenantId1"));
        dataSource1.setTargetDataSources(resolvedDataSources);
        return dataSource1;
    }
}
