package com.argusoft.who.emcare.web.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import com.argusoft.who.emcare.web.fhir.resourceprovider.PatientResourceProvider;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/*")
public class FhirServlet extends RestfulServer {

    @Override
    protected void initialize() throws ServletException {
        super.initialize();
        setFhirContext(FhirContext.forR4());
    }

    public FhirServlet() {
        List<IResourceProvider> resourceProviders = new ArrayList<IResourceProvider>();
        resourceProviders.add(new PatientResourceProvider());
        setResourceProviders(resourceProviders);
    }
}
