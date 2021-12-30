package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.argusoft.who.emcare.web.fhir.dao.EmcareResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.PatientDto;
import com.argusoft.who.emcare.web.fhir.mapper.EmcareResourceMapper;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import com.argusoft.who.emcare.web.location.dao.LocationMasterDao;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class EmcareResourceServiceImpl implements EmcareResourceService {

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);

    @Autowired
    EmcareResourceRepository repository;

    @Autowired
    LocationMasterDao locationMasterDao;

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

    @Override
    public List<PatientDto> getPatientUnderLocationId(Integer locationId) {
        List<Patient> patientsList = new ArrayList<>();
        List<EmcareResource> resourcesList = retrieveResourcesByType("PATIENT");
        for (EmcareResource emcareResource : resourcesList) {
            Patient patient = parser.parseResource(Patient.class, emcareResource.getText());
            patientsList.add(patient);
        }
        List<PatientDto> patientDtosList = EmcareResourceMapper.entitiesToDtoMapper(patientsList);
        List<Integer> locationIds = locationMasterDao.getAllChildLocationId(locationId);
        return patientDtosList.stream().filter(patient -> locationIds.contains(Integer.parseInt(patient.getLocation()))).collect(Collectors.toList());
    }

}