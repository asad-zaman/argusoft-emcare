package com.argusoft.who.emcare.web.fhir.service;

import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import java.util.List;

public interface EmcareResourceService {
	
	public EmcareResource saveResource(EmcareResource emcareResource);
        
        public List<EmcareResource> retrieveResources();
        
        public List<EmcareResource> retrieveResourcesByType(String type);
        
        public EmcareResource findByResourceId(String resourceId);
        
        public void remove(EmcareResource emcareResource);

}