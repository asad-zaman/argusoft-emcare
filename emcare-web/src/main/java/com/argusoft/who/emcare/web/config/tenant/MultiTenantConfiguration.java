package com.argusoft.who.emcare.web.config.tenant;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
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

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Bean
    @Primary
    public DataSource dataSource() {
        Map<Object, Object> resolvedDataSources = new HashMap<>();

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        String tenantId = defaultTenant;
        dataSourceBuilder.driverClassName(CommonConstant.POSTGRESQL_DRIVER);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(databasePassword);
        dataSourceBuilder.url(databaseUrl);
        resolvedDataSources.put(tenantId, dataSourceBuilder.build());

        AbstractRoutingDataSource dataSource = new MultitenantDataSource();
        dataSource.setDefaultTargetDataSource(resolvedDataSources.get(defaultTenant));
        dataSource.setTargetDataSources(resolvedDataSources);
        dataSource.afterPropertiesSet();
        return dataSource;
    }


}
