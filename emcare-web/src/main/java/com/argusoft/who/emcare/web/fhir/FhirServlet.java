package com.argusoft.who.emcare.web.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.openapi.OpenApiInterceptor;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import com.argusoft.who.emcare.web.fhir.resourceprovider.*;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@WebServlet("/*")
public class FhirServlet extends RestfulServer {

    @Autowired
    private PatientResourceProvider patientResourceProvider;
    
    @Autowired
    private QuestionnaireResourceProvider questionnaireResourceProvider;

    @Override
    protected void initialize() throws ServletException {
        super.initialize();
        setFhirContext(FhirContext.forR4());
        // Registering OpenApi Interceptor for swagger ui
        OpenApiInterceptor openApiInterceptor = new OpenApiInterceptor();
        registerInterceptor(openApiInterceptor);
    }

    @PostConstruct
    public void setPostResourceProviders() {
        List<IResourceProvider> resourceProviders = new ArrayList<>();
        resourceProviders.add(patientResourceProvider);
        resourceProviders.add(questionnaireResourceProvider);
        setResourceProviders(resourceProviders);
    }

}
