package com.argusoft.who.emcare.web.config.tenant;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.tenant.entity.TenantConfig;
import com.argusoft.who.emcare.web.tenant.repository.TenantConfigRepository;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

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

    private static Boolean runFlyWay = Boolean.TRUE;

    public Boolean getRunFlyWay() {
        return runFlyWay;
    }

    public void setRunFlyWay(Boolean runFlyWay) {
        this.runFlyWay = runFlyWay;
    }

    @Value("${defaultTenant}")
    private String defaultTenant;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Bean
    public DataSource addDataSourceDynamic() {
        List<TenantConfig> tenantConfigList = tenantConfigRepository.findAll();
        Map<Object, Object> resolvedDataSources = new HashMap<>();

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        String tenantId = defaultTenant;
        dataSourceBuilder.driverClassName(CommonConstant.POSTGRESQL_DRIVER);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(databasePassword);
        dataSourceBuilder.url(databaseUrl);
        resolvedDataSources.put(tenantId, dataSourceBuilder.build());
        for (TenantConfig tenantConfig : tenantConfigList) {
            DataSourceBuilder dataSourceBuilder1 = DataSourceBuilder.create();
            dataSourceBuilder1.driverClassName(CommonConstant.POSTGRESQL_DRIVER);
            dataSourceBuilder1.username(tenantConfig.getUsername());
            dataSourceBuilder1.password(tenantConfig.getPassword());
            dataSourceBuilder1.url(CommonConstant.URL_PREFIX + tenantConfig.getUrl() + ":" + tenantConfig.getDatabasePort() + "/" + tenantConfig.getDatabaseName());
            resolvedDataSources.put(tenantConfig.getTenantId(), dataSourceBuilder1.build());
        }
        AbstractRoutingDataSource dataSource1 = (AbstractRoutingDataSource) dataSource;
        dataSource1.setDefaultTargetDataSource(resolvedDataSources.get(defaultTenant));
        dataSource1.setTargetDataSources(resolvedDataSources);
        dataSource1.afterPropertiesSet();

        if(runFlyWay){
            for (Map.Entry<Object, DataSource> entry : dataSource1.getResolvedDataSources().entrySet()) {
                DataSource dataSource = entry.getValue();
                Flyway flyway = Flyway.configure()
                        .dataSource(dataSource).schemas("public")
                        .load();
                flyway.migrate();
            }
        }
        return dataSource1;
    }
    public void addDataSourceForNewTenant() {
        AbstractRoutingDataSource dataSource1 = (AbstractRoutingDataSource) dataSource;

        for (Map.Entry<Object, DataSource> entry : dataSource1.getResolvedDataSources().entrySet()) {
            System.out.println(entry.getValue());
            DataSource dataSource = entry.getValue();
            Flyway flyway = Flyway.configure()
                    .baselineOnMigrate(true)
                    .dataSource(dataSource).schemas("public")
                    .load();
            flyway.migrate();
        }
    }
}
