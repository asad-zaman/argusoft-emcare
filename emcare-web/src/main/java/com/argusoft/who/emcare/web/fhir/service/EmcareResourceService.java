package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dto.PatientDto;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;

import java.util.List;
import java.util.Map;

public interface EmcareResourceService {

    public EmcareResource saveResource(EmcareResource emcareResource);

    public String saveOrUpdateResourceByRequestType(Resource resource, String resourceType, String requestType);

    public List<EmcareResource> retrieveResources();

    public PageDto getPatientsPage(Integer pageNo, String searchString);

    public List<EmcareResource> retrieveResourcesByType(String type, DateParam theDate, IdType theId);

    public EmcareResource findByResourceId(String resourceId);

    public void remove(EmcareResource emcareResource);

    public PageDto getPatientUnderLocationId(Object locationId, Integer pageNo);

    public List<PatientDto> getAllPatients();

    public List<Patient> getAllPatientResources();

    public List<PatientDto> getPatientDtoByPatient(List<Patient> patient);

    public Map<String, Integer> getPatientAgeGroupCount();

    public List<PatientDto> getPatientDtoByIds(List<String> ids);

    public Bundle getPatientBundle(String theId);

}