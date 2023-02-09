package com.argusoft.who.emcare.web.config.tenant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 08/02/23  2:06 pm
 */
@Configuration
public class MultitenantConfiguration {

    @Autowired
    private ApplicationContext context;


    @Value("${defaultTenant}")
    private String defaultTenant;

//    public static AbstractRoutingDataSource dataSource = new MultitenantDataSource();


    @Bean
    @Primary
//    @RefreshScope
    public DataSource dataSource() {
        System.out.println("line number 43");
        Map<Object, Object> resolvedDataSources = new HashMap<>();

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        String tenantId = defaultTenant;
        dataSourceBuilder.driverClassName("org.postgresql.Driver");
        dataSourceBuilder.username("postgres");
        dataSourceBuilder.password("argusadmin");
        dataSourceBuilder.url("jdbc:postgresql://192.1.200.197:5432/emcare");
        resolvedDataSources.put(tenantId, dataSourceBuilder.build());

//        List<TenantConfig> tenantConfigList = context.getBean(TenantConfigMaster.class).getAll();
//
//        for (TenantConfig tenantConfig : tenantConfigList) {
//            DataSourceBuilder dataSourceBuilder1 = DataSourceBuilder.create();
//            dataSourceBuilder1.driverClassName("org.postgresql.Driver");
//            dataSourceBuilder1.username(tenantConfig.getUsername());
//            dataSourceBuilder1.password(tenantConfig.getPassword());
//            dataSourceBuilder1.url(tenantConfig.getUrl());
//            resolvedDataSources.put(tenantConfig.getTenantId(), dataSourceBuilder1.build());
//        }

//        DataSourceBuilder dataSourceBuilder1 = DataSourceBuilder.create();
//        String tenantId2 = "tenantId2";
//        dataSourceBuilder1.driverClassName("org.postgresql.Driver");
//        dataSourceBuilder1.username("postgres");
//        dataSourceBuilder1.password("argusadmin");
//        dataSourceBuilder1.url("jdbc:postgresql://192.1.200.197:5432/emcare-dev");
//        resolvedDataSources.put(tenantId2, dataSourceBuilder1.build());

        AbstractRoutingDataSource dataSource = new MultitenantDataSource();
        dataSource.setDefaultTargetDataSource(resolvedDataSources.get(defaultTenant));
        dataSource.setTargetDataSources(resolvedDataSources);
        dataSource.afterPropertiesSet();
        return dataSource;
    }


}
