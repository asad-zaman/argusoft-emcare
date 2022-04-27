package com.argusoft.who.emcare.web.fhir.service;

import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dto.PatientDto;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;

import java.util.List;
import org.hl7.fhir.r4.model.Resource;

public interface EmcareResourceService {

    public EmcareResource saveResource(EmcareResource emcareResource);
    
    public String saveOrUpdateResourceByRequestType(Resource resource, String resourceType, String requestType);

    public List<EmcareResource> retrieveResources();

    public PageDto getPatientsPage(Integer pageNo, String searchString);

    public List<EmcareResource> retrieveResourcesByType(String type);

    public EmcareResource findByResourceId(String resourceId);

    public void remove(EmcareResource emcareResource);

    public PageDto getPatientUnderLocationId(Integer locationId, Integer pageNo);

    public List<PatientDto> getAllPatients();

}