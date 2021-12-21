package com.argusoft.who.emcare.web.config;

import com.argusoft.who.emcare.web.fhir.FhirServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletBeanConfig {

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
}
