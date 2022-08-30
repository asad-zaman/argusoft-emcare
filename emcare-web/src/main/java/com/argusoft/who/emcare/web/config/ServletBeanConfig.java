package com.argusoft.who.emcare.web.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import com.argusoft.who.emcare.web.fhir.FhirServlet;
import org.hl7.fhir.r4.model.PlanDefinition;
import org.opencds.cqf.ruler.external.annotations.OnR4Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ServletBeanConfig {

    @Autowired
    AutowireCapableBeanFactory beanFactory;

    @Bean
    public FhirServlet fhirServletBean() {
        return new FhirServlet();
    }

    @Bean
    public ServletRegistrationBean<FhirServlet> servletRegistrationBean() {
        ServletRegistrationBean<FhirServlet> registration =
                new ServletRegistrationBean<>(fhirServletBean(), "/fhir/*");
        registration.setName("FhirServlet");
        return registration;
    }

    @Bean
    public FhirJpaServlet fhirJpaServletBean() {
        return new FhirJpaServlet();
    }

    @Bean
    public ServletRegistrationBean<FhirJpaServlet> jpaServletRegistrationBean() {
        ServletRegistrationBean<FhirJpaServlet> registration =
                new ServletRegistrationBean<>(fhirJpaServletBean(), "/fhir/cqf/*");
        registration.setName("FhirJpaServlet");
        return registration;
    }

    @Bean
    @Conditional(OnR4Condition.class)
    public ServletRegistrationBean<FhirJpaServlet> hapiServletRegistration() {
        ServletRegistrationBean<FhirJpaServlet> servletRegistrationBean = new ServletRegistrationBean<>();
        FhirJpaServlet server = new FhirJpaServlet();
        beanFactory.autowireBean(server);
        servletRegistrationBean.setName("fhir servlet");
        servletRegistrationBean.setServlet(server);
        servletRegistrationBean.addUrlMappings("/fhir/*");
        servletRegistrationBean.setLoadOnStartup(1);

        return servletRegistrationBean;
    }

//    @Bean
//    public DaoRegistry setDaoRegistry() {
//        DaoRegistry daoRegistry = new DaoRegistry();
//        return daoRegistry;
//    }
}
