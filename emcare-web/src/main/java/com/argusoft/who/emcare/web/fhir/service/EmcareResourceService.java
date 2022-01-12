package com.argusoft.who.emcare.web.fhir.service;

import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dto.PatientDto;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;

import java.util.List;

public interface EmcareResourceService {

    public EmcareResource saveResource(EmcareResource emcareResource);

    public List<EmcareResource> retrieveResources();

    public PageDto getPatientsPage(Integer pageNo);

    public List<EmcareResource> retrieveResourcesByType(String type);

    public EmcareResource findByResourceId(String resourceId);

    public void remove(EmcareResource emcareResource);

    public PageDto getPatientUnderLocationId(Integer locationId, Integer pageNo);

}