package com.argusoft.who.emcare.web;

import com.argusoft.who.emcare.web.fhir.FhirServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EmcareWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmcareWebApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean ServletRegistrationBean() {
        ServletRegistrationBean registration =
                new ServletRegistrationBean(new FhirServlet(), "/fhir/*");
        registration.setName("FhirServlet");
        return registration;
    }

}
