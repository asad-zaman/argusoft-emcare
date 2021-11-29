package com.argusoft.who.emcare.web.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import com.argusoft.who.emcare.web.fhir.resourceprovider.PatientResourceProvider;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import com.argusoft.who.emcare.web.fhir.service.implementation.EmcareResourceServiceImpl;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@WebServlet("/*")
public class FhirServlet extends RestfulServer {
	
	private final List<IResourceProvider> resourceProviders = new ArrayList<>();
	
	@Autowired
	PatientResourceProvider patientResourceProvider;
	
    @Override
    protected void initialize() throws ServletException {
        super.initialize();
        setFhirContext(FhirContext.forR4());
    }
    
    @PostConstruct
    public void setPostResourceProviders() {
    	List<IResourceProvider> resourceProviders = new ArrayList<>();
    	resourceProviders.add(patientResourceProvider);
    	setResourceProviders(resourceProviders);
    }
    public FhirServlet() {
    }
    

}
