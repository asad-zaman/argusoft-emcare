package com.argusoft.who.emcare.web.fhir.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.argusoft.who.emcare.web.fhir.dto.PatientDto;
import com.argusoft.who.emcare.web.fhir.mapper.EmcareResourceMapper;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import com.argusoft.who.emcare.web.location.model.LocationMaster;
import com.argusoft.who.emcare.web.location.service.LocationService;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api/emcare")
public class EmcareResourceController {

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);

    @Autowired
    private EmcareResourceService emcareResourceService;

    @Autowired
    private LocationService locationService;


    @GetMapping("/patient")
    public List<PatientDto> getAllPatients() {

        List<Patient> patientsList = new ArrayList<>();
        List<PatientDto> patientDtosList;

        List<EmcareResource> resourcesList = emcareResourceService.retrieveResourcesByType("PATIENT");

        for (EmcareResource emcareResource : resourcesList) {
            Patient patient = parser.parseResource(Patient.class, emcareResource.getText());
            patientsList.add(patient);
        }

        patientDtosList = EmcareResourceMapper.entitiesToDtoMapper(patientsList);

        //Converting caregiverId and locationid to name
        for (PatientDto patientDto : patientDtosList) {

            if (patientDto.getCaregiver() != null) {
                EmcareResource caregiverResource = emcareResourceService.findByResourceId(patientDto.getCaregiver());
                RelatedPerson caregiver = parser.parseResource(RelatedPerson.class, caregiverResource.getText());
                patientDto.setCaregiver(caregiver.getNameFirstRep().getGiven().get(0) + " " + caregiver.getNameFirstRep().getFamily());
            }

            if (patientDto.getLocation() != null) {
                LocationMaster location = locationService.getLocationMasterById(Integer.parseInt(patientDto.getLocation()));
                patientDto.setLocation(location.getName());
            }
        }

        return patientDtosList;
    }

    @GetMapping("/patient/locationId/{locationId}")
    public List<PatientDto> getAllPatientsUnderLocation(@PathVariable(value = "locationId") Integer locationId) {
        return emcareResourceService.getPatientUnderLocationId(locationId);
    }
}
