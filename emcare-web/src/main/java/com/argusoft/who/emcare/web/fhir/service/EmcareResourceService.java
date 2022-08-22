package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dto.PatientDto;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;

import java.util.List;

public interface EmcareResourceService {

    public EmcareResource saveResource(EmcareResource emcareResource);

    public String saveOrUpdateResourceByRequestType(Resource resource, String resourceType, String requestType);

    public List<EmcareResource> retrieveResources();

    public PageDto getPatientsPage(Integer pageNo, String searchString);

    public List<EmcareResource> retrieveResourcesByType(String type, DateParam theDate);

    public EmcareResource findByResourceId(String resourceId);

    public void remove(EmcareResource emcareResource);

    public PageDto getPatientUnderLocationId(Integer locationId, Integer pageNo);

    public List<PatientDto> getAllPatients();

    public List<Patient> getAllPatientResources();

    public List<PatientDto> getPatientDtoByPatient(List<Patient> patient);

}