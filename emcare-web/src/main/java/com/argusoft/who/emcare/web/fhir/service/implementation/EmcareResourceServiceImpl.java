package com.argusoft.who.emcare.web.fhir.service.implementation;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.argusoft.who.emcare.web.fhir.dao.EmcareResourceRepository;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;

@Service
public class EmcareResourceServiceImpl implements EmcareResourceService{

	@Autowired
	EmcareResourceRepository repository;
	
	@Override
	@Transactional
	public EmcareResource saveResource(EmcareResource emcareResource) {
		return repository.save(emcareResource);
	}
	
}
