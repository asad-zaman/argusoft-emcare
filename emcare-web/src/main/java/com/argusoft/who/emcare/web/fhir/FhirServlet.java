package com.argusoft.who.emcare.web.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.openapi.OpenApiInterceptor;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import com.argusoft.who.emcare.web.fhir.resourceprovider.*;
import com.argusoft.who.emcare.web.fhir.resourceprovider.BundleResourceProvider;
import com.argusoft.who.emcare.web.fhir.resourceprovider.LocationResourceProvider;
import com.argusoft.who.emcare.web.fhir.resourceprovider.OrganizationResourceProvider;
import com.argusoft.who.emcare.web.fhir.resourceprovider.PatientResourceProvider;
import com.argusoft.who.emcare.web.fhir.resourceprovider.QuestionnaireResourceProvider;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/*")
public class FhirServlet extends RestfulServer {

    @Autowired
    private PatientResourceProvider patientResourceProvider;

    @Autowired
    private QuestionnaireResourceProvider questionnaireResourceProvider;

    @Autowired
    private LocationResourceProvider locationResourceProvider;

    @Autowired
    private OrganizationResourceProvider organizationResourceProvider;
    
    @Autowired
    private BundleResourceProvider bundleResourceProvider;

    @Autowired
    private PlanDefinitionResourceProvider planDefinitionResourceProvider;

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
        resourceProviders.add(locationResourceProvider);
        resourceProviders.add(organizationResourceProvider);
        resourceProviders.add(bundleResourceProvider);
        resourceProviders.add(planDefinitionResourceProvider);
        setResourceProviders(resourceProviders);
    }

}
