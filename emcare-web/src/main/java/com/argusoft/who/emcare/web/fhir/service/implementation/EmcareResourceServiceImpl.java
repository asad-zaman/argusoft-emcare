package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.EmcareResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.PatientDto;
import com.argusoft.who.emcare.web.fhir.mapper.EmcareResourceMapper;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import com.argusoft.who.emcare.web.location.dao.LocationMasterDao;
import com.argusoft.who.emcare.web.location.model.LocationMaster;
import com.argusoft.who.emcare.web.location.service.LocationService;
import com.argusoft.who.emcare.web.secuirty.EmCareSecurityUser;
import com.argusoft.who.emcare.web.userLocationMapping.dao.UserLocationMappingRepository;
import com.argusoft.who.emcare.web.userLocationMapping.model.UserLocationMapping;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private LocationService locationService;

    @Autowired
    EmCareSecurityUser emCareSecurityUser;

    @Autowired
    UserLocationMappingRepository userLocationMappingRepository;

    @Override
    public EmcareResource saveResource(EmcareResource emcareResource) {
        return repository.save(emcareResource);
    }

    @Override
    public List<EmcareResource> retrieveResources() {
        return repository.findAll();
    }

    @Override
    public PageDto getPatientsPage(Integer pageNo, String searchString) {
        List<Patient> patientsList = new ArrayList<>();
        List<PatientDto> patientDtosList;
        Integer totalCount = 0;
        List<EmcareResource> resourcesList;
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE);
        if (searchString != null && !searchString.isEmpty()) {
            totalCount = repository.findByTypeContainingAndTextContainingIgnoreCase(CommonConstant.FHIR_PATIENT, searchString).size();
            resourcesList = repository.findByTypeContainingAndTextContainingIgnoreCase(CommonConstant.FHIR_PATIENT, searchString, page);
        } else {
            totalCount = repository.findAllByType(CommonConstant.FHIR_PATIENT).size();
            resourcesList = repository.findAllByType(CommonConstant.FHIR_PATIENT, page);
        }

        for (EmcareResource emcareResource : resourcesList) {
            Patient patient = parser.parseResource(Patient.class, emcareResource.getText());
            patientsList.add(patient);
        }

        patientDtosList = EmcareResourceMapper.patientEntitiesToDtoMapper(patientsList);

        //Converting caregiverId and locationid to name
        for (PatientDto patientDto : patientDtosList) {

            if (patientDto.getCaregiver() != null) {
                EmcareResource caregiverResource = findByResourceId(patientDto.getCaregiver());
                RelatedPerson caregiver = parser.parseResource(RelatedPerson.class, caregiverResource.getText());
                patientDto.setCaregiver(caregiver.getNameFirstRep().getGiven().get(0) + " " + caregiver.getNameFirstRep().getFamily());
            }

            if (patientDto.getLocation() != null) {
                LocationMaster location = locationService.getLocationMasterById(Integer.parseInt(patientDto.getLocation()));
                patientDto.setLocation(location.getName());
            }
        }
        PageDto pageDto = new PageDto();
        pageDto.setList(patientDtosList);
        pageDto.setTotalCount(totalCount.longValue());
        return pageDto;
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
    public PageDto getPatientUnderLocationId(Integer locationId, Integer pageNo) {
        List<Patient> patientsList = new ArrayList<>();
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE);
        Integer totalCount = repository.findAllByType(CommonConstant.FHIR_PATIENT).size();
        List<EmcareResource> resourcesList = repository.findAllByType(CommonConstant.FHIR_PATIENT, page);
        for (EmcareResource emcareResource : resourcesList) {
            Patient patient = parser.parseResource(Patient.class, emcareResource.getText());
            patientsList.add(patient);
        }
        List<PatientDto> patientDtosList = EmcareResourceMapper.patientEntitiesToDtoMapper(patientsList);
        List<Integer> locationIds = locationMasterDao.getAllChildLocationId(locationId);
        List<PatientDto> list = patientDtosList.stream().filter(patient -> locationIds.contains(Integer.parseInt(patient.getLocation()))).collect(Collectors.toList());

        //Converting locationid to name
        for (PatientDto patientDto : list) {
            if (patientDto.getLocation() != null) {
                LocationMaster location = locationService.getLocationMasterById(Integer.parseInt(patientDto.getLocation()));
                patientDto.setLocation(location.getName());
            }
        }

        PageDto pageDto = new PageDto();
        pageDto.setList(list);
        pageDto.setTotalCount(totalCount.longValue());
        return pageDto;
    }

    @Override
    public List<PatientDto> getAllPatients() {
        List<Patient> patientsList = new ArrayList<>();
        List<PatientDto> patientDtosList;

        List<EmcareResource> resourcesList = retrieveResourcesByType("PATIENT");

        for (EmcareResource emcareResource : resourcesList) {
            Patient patient = parser.parseResource(Patient.class, emcareResource.getText());
            patientsList.add(patient);
        }

        String loggedInUserId = emCareSecurityUser.getLoggedInUserId();
        List<UserLocationMapping> userLocationMapping = userLocationMappingRepository.findByUserId(loggedInUserId);
        if (!userLocationMapping.isEmpty()) {
            List<Integer> assignedlocationId = userLocationMapping.stream().map(UserLocationMapping::getLocationId).collect(Collectors.toList());
            List<Integer> childLocations = locationMasterDao.getAllChildLocationIdWithMultipalLocationId(assignedlocationId);
            patientsList = patientsList.stream().filter(e -> childLocations.contains(Integer.parseInt(((Identifier) e.getExtension().get(0).getValue()).getValue()))).collect(Collectors.toList());
        }

//        patientsList =
        patientDtosList = EmcareResourceMapper.patientEntitiesToDtoMapper(patientsList);

        //Converting caregiverId and locationid to name
        for (PatientDto patientDto : patientDtosList) {

            if (patientDto.getCaregiver() != null) {
                EmcareResource caregiverResource = findByResourceId(patientDto.getCaregiver());
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

}