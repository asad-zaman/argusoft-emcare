package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.EmcareResourceRepository;
import com.argusoft.who.emcare.web.fhir.dao.LocationResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.dto.PatientDto;
import com.argusoft.who.emcare.web.fhir.mapper.EmcareResourceMapper;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import com.argusoft.who.emcare.web.fhir.service.LocationResourceService;
import com.argusoft.who.emcare.web.location.dao.LocationMasterDao;
import com.argusoft.who.emcare.web.location.service.LocationService;
import com.argusoft.who.emcare.web.secuirty.EmCareSecurityUser;
import com.argusoft.who.emcare.web.userLocationMapping.dao.UserLocationMappingRepository;
import com.argusoft.who.emcare.web.userLocationMapping.model.UserLocationMapping;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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

    @Autowired
    private LocationResourceService locationResourceService;

    @Autowired
    private LocationResourceRepository locationResourceRepository;

    @Override
    public EmcareResource saveResource(EmcareResource emcareResource) {
        return repository.save(emcareResource);
    }

    @Override
    public String saveOrUpdateResourceByRequestType(Resource resource, String resourceType, String requestType) {
        //Saving meta
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());

        Integer versionId = 1;

        if (resource.getMeta() != null && resource.getMeta().getVersionId() != null) {
            versionId = Integer.parseInt(resource.getMeta().getVersionId()) + 1;
            m.setVersionId(String.valueOf(versionId));
        }

        resource.setMeta(m);

        //Setting Resource ID
        String resourceId;
        if (resource.getId() != null) {
            resourceId = resource.getIdElement().getIdPart();
        } else {
            resourceId = UUID.randomUUID().toString();
            resource.setId(resourceId);
        }

        String resourceString = parser.encodeResourceToString(resource);

        EmcareResource emcareResource = findByResourceId(resourceId);
        String facilityId = null;
        if (requestType.toUpperCase().equals(CommonConstant.FHIR_PATIENT)) {
            Patient patient = parser.parseResource(Patient.class, resourceString);
            Extension facilityExtension = patient.getExtension().get(0);
            facilityId = ((Identifier) facilityExtension.getValue()).getValue();
        }


        if (emcareResource == null) {
            emcareResource = new EmcareResource();
        }

        emcareResource.setText(resourceString);
        emcareResource.setResourceId(resourceId);
        emcareResource.setType(resourceType.toUpperCase());
        emcareResource.setFacilityId(facilityId);

        saveResource(emcareResource);

        return resourceId;

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

            if (patientDto.getFacility() != null) {
                FacilityDto facilityDto = locationResourceService.getFacilityDto(patientDto.getFacility());
                if (facilityDto != null) {
                    patientDto.setFacility(facilityDto.getFacilityName());
                    patientDto.setOrganizationName(facilityDto.getOrganizationName());
                    patientDto.setLocationName(facilityDto.getLocationName());
                }
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

        List<Integer> locationIds = locationMasterDao.getAllChildLocationId(locationId);
        List<String> childFacilityIds = locationResourceRepository.findResourceIdIn(locationIds);


        Long totalCount = Long.valueOf(repository.findByFacilityIdIn(childFacilityIds).size());
        List<EmcareResource> resourcesList = repository.findByFacilityIdIn(childFacilityIds, page);
        for (EmcareResource emcareResource : resourcesList) {
            Patient patient = parser.parseResource(Patient.class, emcareResource.getText());
            patientsList.add(patient);
        }

        List<PatientDto> patientDtosList = EmcareResourceMapper.patientEntitiesToDtoMapper(patientsList);
        for (PatientDto patientDto : patientDtosList) {

            if (patientDto.getCaregiver() != null) {
                EmcareResource caregiverResource = findByResourceId(patientDto.getCaregiver());
                RelatedPerson caregiver = parser.parseResource(RelatedPerson.class, caregiverResource.getText());
                patientDto.setCaregiver(caregiver.getNameFirstRep().getGiven().get(0) + " " + caregiver.getNameFirstRep().getFamily());
            }

            if (patientDto.getFacility() != null) {
                FacilityDto facilityDto = locationResourceService.getFacilityDto(patientDto.getFacility());
                patientDto.setFacility(facilityDto.getFacilityName());
                patientDto.setOrganizationName(facilityDto.getOrganizationName());
                patientDto.setLocationName(facilityDto.getLocationName());
            }
        }

        PageDto pageDto = new PageDto();
        pageDto.setList(patientDtosList);
        pageDto.setTotalCount(totalCount);
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
            List<String> assignedFacilityIds = userLocationMapping.stream().map(UserLocationMapping::getFacilityId).collect(Collectors.toList());
            List<Integer> assignedLocationIds = locationResourceRepository.findAllLocationId(assignedFacilityIds).stream()
                    .mapToInt(Long::intValue)
                    .boxed()
                    .collect(Collectors.toList());
            List<Integer> childLocations = locationMasterDao.getAllChildLocationIdWithMultipalLocationId(assignedLocationIds);
            List<String> childFacilityIds = locationResourceRepository.findResourceIdIn(childLocations);
            patientsList = patientsList.stream().filter(e -> childFacilityIds.contains(((Identifier) e.getExtension().get(0).getValue()).getValue())).collect(Collectors.toList());
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

            if (patientDto.getFacility() != null) {
                FacilityDto facilityDto = locationResourceService.getFacilityDto(patientDto.getFacility());
                patientDto.setFacility(facilityDto.getFacilityName());
                patientDto.setOrganizationName(facilityDto.getOrganizationName());
                patientDto.setLocationName(facilityDto.getLocationName());
            }
        }

        return patientDtosList;
    }

}