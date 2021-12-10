package com.argusoft.who.emcare.web.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.argusoft.who.emcare.web.fhir.FhirServlet;
import com.argusoft.who.emcare.web.fhir.resourceprovider.PatientResourceProvider;

@Configuration
public class ServletBeanConfig {
	
	@Bean
	public FhirServlet fhirServletBean() {
		return new FhirServlet();
	}
	
	@Bean
    public ServletRegistrationBean ServletRegistrationBean() {
        ServletRegistrationBean registration =
                new ServletRegistrationBean(fhirServletBean(), "/fhir/*");
        registration.setName("FhirServlet");
        return registration;
    }
}
