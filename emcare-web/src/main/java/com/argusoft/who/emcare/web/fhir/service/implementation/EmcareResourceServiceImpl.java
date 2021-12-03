package com.argusoft.who.emcare.web.fhir.service.implementation;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.argusoft.who.emcare.web.fhir.dao.EmcareResourceRepository;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import java.util.List;

@Transactional
@Service
public class EmcareResourceServiceImpl implements EmcareResourceService {

    @Autowired
    EmcareResourceRepository repository;

    @Override
    public EmcareResource saveResource(EmcareResource emcareResource) {
        return repository.save(emcareResource);
    }

    @Override
    public List<EmcareResource> retrieveResources() {
        return repository.findAll();
    }

    @Override
    public List<EmcareResource> retrieveResourcesByType(String type) {
        return repository.findAllByType(type);
    }

    @Override
    public EmcareResource findByResourceId(String resourceId) {
        return repository.findByResourceId(resourceId);
    }

    @Override
    public void remove(EmcareResource emcareResource) {
        repository.delete(emcareResource);
    }

}
