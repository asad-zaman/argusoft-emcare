package com.argusoft.who.emcare.web.fhir.service;

import org.springframework.stereotype.Service;

import com.argusoft.who.emcare.web.fhir.model.EmcareResource;

public interface EmcareResourceService {
	
	public EmcareResource saveResource(EmcareResource emcareResource);

}
