package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.ActivityDefinitionResourceRepository;
import com.argusoft.who.emcare.web.fhir.dao.EmcareResourceRepository;
import com.argusoft.who.emcare.web.fhir.dao.LocationResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.dto.PatientDto;
import com.argusoft.who.emcare.web.fhir.mapper.EmcareResourceMapper;
import com.argusoft.who.emcare.web.fhir.model.ActivityDefinitionResource;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import com.argusoft.who.emcare.web.fhir.model.QuestionnaireMaster;
import com.argusoft.who.emcare.web.fhir.model.RelatedPersonResource;
import com.argusoft.who.emcare.web.fhir.resourceprovider.QuestionnaireResourceProvider;
import com.argusoft.who.emcare.web.fhir.service.*;
import com.argusoft.who.emcare.web.location.dao.LocationMasterDao;
import com.argusoft.who.emcare.web.location.service.LocationService;
import com.argusoft.who.emcare.web.secuirty.EmCareSecurityUser;
import com.argusoft.who.emcare.web.userlocationmapping.dao.UserLocationMappingRepository;
import com.argusoft.who.emcare.web.userlocationmapping.model.UserLocationMapping;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
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

    @Autowired
    ActivityDefinitionResourceService activityDefinitionResourceService;

    @Autowired
    ActivityDefinitionResourceRepository activityDefinitionResourceRepository;

    @Autowired
    CodeSystemResourceService codeSystemResourceService;

    @Autowired
    LibraryResourceService libraryResourceService;

    @Autowired
    OperationDefinitionResourceService operationDefinitionResourceService;

    @Autowired
    PlanDefinitionResourceService planDefinitionResourceService;

    @Autowired
    QuestionnaireMasterService questionnaireMasterService;

    @Autowired
    QuestionnaireResourceProvider questionnaireResourceProvider;

    @Autowired
    StructureDefinitionService structureDefinitionService;

    @Autowired
    ValueSetResourceService valueSetResourceService;

    @Autowired
    EncounterResourceService encounterResourceService;

    @Autowired
    StructureMapResourceService structureMapResourceService;

    @Autowired
    ObservationResourceService observationResourceService;

    @Autowired
    RelatedPersonResourceService relatedPersonResourceService;


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
        switch (resourceType.toUpperCase()) {
            case CommonConstant.FHIR_PATIENT:
                EmcareResource emcareResource = findByResourceId(resourceId);
                String facilityId = null;
                if (resourceType.equalsIgnoreCase(CommonConstant.FHIR_PATIENT)) {
                    Patient patient = parser.parseResource(Patient.class, resourceString);
                    Extension facilityExtension = patient.getExtensionByUrl(CommonConstant.LOCATION_EXTENSION_URL);
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
                break;
            case CommonConstant.ACTIVITY_DEFINITION:
                ActivityDefinitionResource activityDefinitionResource = activityDefinitionResourceRepository.findByResourceId(resourceId);
                if (activityDefinitionResource != null) {
                    activityDefinitionResourceService.updateActivityDefinitionResource(resource.getIdElement(), parser.parseResource(ActivityDefinition.class, resourceString));
                } else {
                    activityDefinitionResourceService.saveResource(parser.parseResource(ActivityDefinition.class, resourceString));
                }
                break;
            case CommonConstant.CODE_SYSTEM:
                CodeSystem codeSystem = codeSystemResourceService.getResourceById(resourceId);
                if (codeSystem != null) {
                    codeSystemResourceService.updateCodeSystem(resource.getIdElement(), parser.parseResource(CodeSystem.class, resourceString));
                } else {
                    codeSystemResourceService.saveResource(parser.parseResource(CodeSystem.class, resourceString));
                }
                break;
            case CommonConstant.LIBRARY:
                Library library = libraryResourceService.getResourceById(resourceId);
                if (library != null) {
                    libraryResourceService.updateLibraryResource(resource.getIdElement(), parser.parseResource(Library.class, resourceString));
                } else {
                    libraryResourceService.saveResource(parser.parseResource(Library.class, resourceString));
                }
                break;
            case CommonConstant.OPERATION_DEFINITION:
                OperationDefinition operationDefinition = operationDefinitionResourceService.getResourceById(resourceId);
                if (operationDefinition != null) {
                    operationDefinitionResourceService.updateOperationDefinitionResource(resource.getIdElement(), parser.parseResource(OperationDefinition.class, resourceString));
                } else {
                    operationDefinitionResourceService.saveResource(parser.parseResource(OperationDefinition.class, resourceString));
                }
                break;
            case CommonConstant.PLANDEFINITION_TYPE_STRING:
                PlanDefinition planDefinition = planDefinitionResourceService.getByResourceId(resourceId);
                if (planDefinition != null) {
                    planDefinitionResourceService.updateLocationResource(resource.getIdElement(), parser.parseResource(PlanDefinition.class, resourceString));
                } else {
                    planDefinitionResourceService.saveResource(parser.parseResource(PlanDefinition.class, resourceString));
                }
                break;
            case CommonConstant.QUESTIONNAIRE:
                QuestionnaireMaster questionnaireMaster = questionnaireMasterService.retrieveQuestionnaireByResourceId(resourceId);
                if (questionnaireMaster != null) {
                    questionnaireMasterService.updateQuestionnaireResource(resource.getIdElement(), parser.parseResource(Questionnaire.class, resourceString));
                } else {
                    questionnaireResourceProvider.createQuestionnaire(parser.parseResource(Questionnaire.class, resourceString));
                }
                break;
            case CommonConstant.STRUCTURE_DEFINITION:
                StructureDefinition structureDefinition = structureDefinitionService.getResourceById(resourceId);
                if (structureDefinition != null) {
                    structureDefinitionService.updateStructureDefinition(resource.getIdElement(), parser.parseResource(StructureDefinition.class, resourceString));
                } else {
                    structureDefinitionService.saveResource(parser.parseResource(StructureDefinition.class, resourceString));
                }
                break;
            case CommonConstant.VALUESET_TYPE_STRING:
                ValueSet valueSet = valueSetResourceService.getByResourceId(resourceId);
                if (valueSet != null) {
                    valueSetResourceService.updateValueSetResource(resource.getIdElement(), parser.parseResource(ValueSet.class, resourceString));
                } else {
                    valueSetResourceService.saveResource(parser.parseResource(ValueSet.class, resourceString));
                }
                break;
            case CommonConstant.ENCOUNTER:
                Encounter encounter = encounterResourceService.getResourceById(resourceId);
                if (encounter != null) {
                    encounterResourceService.updateEncounterResource(resource.getIdElement(), parser.parseResource(Encounter.class, resourceString));
                } else {
                    encounterResourceService.saveResource(parser.parseResource(Encounter.class, resourceString));
                }
                break;
            case CommonConstant.OBSERVATION:
                Observation observation = observationResourceService.getResourceById(resourceId);
                if (observation != null) {
                    observationResourceService.updateObservationResource(resource.getIdElement(), parser.parseResource(Observation.class, resourceString));
                } else {
                    observationResourceService.saveResource(parser.parseResource(Observation.class, resourceString));
                }
                break;
            case CommonConstant.RELATED_PERSON:
                RelatedPerson relatedPerson = relatedPersonResourceService.getResourceById(resourceId);
                if (relatedPerson != null) {
                    relatedPersonResourceService.updateRelatedPersonResource(resource.getIdElement(), parser.parseResource(RelatedPerson.class, resourceString));
                } else {
                    relatedPersonResourceService.saveResource(parser.parseResource(RelatedPerson.class, resourceString));
                }
                break;
            case CommonConstant.STRUCTURE_MAP:
                StructureMap structureMap = structureMapResourceService.getResourceById(resourceId);
                if (structureMap != null) {
                    structureMapResourceService.updateStructureMapResource(resource.getIdElement(), parser.parseResource(StructureMap.class, resourceString));
                } else {
                    structureMapResourceService.saveResource(parser.parseResource(StructureMap.class, resourceString));
                }
                break;
            default:
                break;
        }

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
        Sort sort = Sort.by("createdOn").descending();
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE, sort);
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
                RelatedPerson caregiver = relatedPersonResourceService.getResourceById(patientDto.getCaregiver());
//                EmcareResource caregiverResource = findByResourceId(patientDto.getCaregiver());
//                RelatedPerson caregiver = parser.parseResource(RelatedPerson.class, caregiverResource.getText());
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
    public List<EmcareResource> retrieveResourcesByType(String type, DateParam theDate, IdType theId) {
        List<String> childFacilityIds = new ArrayList<>();
        if (theId != null) {
            FacilityDto facilityDto = locationResourceService.getFacilityDto(theId.getIdPart());
            List<Integer> locationIds = locationMasterDao.getAllChildLocationId(facilityDto.getLocationId().intValue());
            childFacilityIds = locationResourceRepository.findResourceIdIn(locationIds);
        }

        if (theDate == null && theId == null) {
            return repository.findAllByType(type);
        } else if (theDate != null && theId == null) {
            return repository.getByDateAndType(theDate.getValue(), type);
        } else if (theDate == null) {
            return repository.findByFacilityIdIn(childFacilityIds);
        } else {
            return repository.findByTypeAndModifiedOnGreaterThanOrCreatedOnGreaterThanAndFacilityIdIn(type, theDate.getValue(), theDate.getValue(), childFacilityIds);
        }
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
                RelatedPerson caregiver = relatedPersonResourceService.getResourceById(patientDto.getCaregiver());
//                EmcareResource caregiverResource = findByResourceId(patientDto.getCaregiver());
//                RelatedPerson caregiver = parser.parseResource(RelatedPerson.class, caregiverResource.getText());
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

        List<EmcareResource> resourcesList = retrieveResourcesByType(CommonConstant.FHIR_PATIENT, null, null);

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
                RelatedPerson caregiver = relatedPersonResourceService.getResourceById(patientDto.getCaregiver());
//                EmcareResource caregiverResource = findByResourceId(patientDto.getCaregiver());
//                RelatedPerson caregiver = parser.parseResource(RelatedPerson.class, caregiverResource.getText());
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

    @Override
    public List<Patient> getAllPatientResources() {
        List<Patient> patientsList = new ArrayList<>();

        List<EmcareResource> resourcesList = retrieveResourcesByType("PATIENT", null, null);

        for (EmcareResource emcareResource : resourcesList) {
            Patient patient = parser.parseResource(Patient.class, emcareResource.getText());
            patientsList.add(patient);
        }
        return patientsList;
    }

    @Override
    public List<PatientDto> getPatientDtoByPatient(List<Patient> patient) {
        List<PatientDto> patientDtosList;

        patientDtosList = EmcareResourceMapper.patientEntitiesToDtoMapper(patient);

        //Converting caregiverId and locationid to name
        for (PatientDto patientDto : patientDtosList) {

            if (patientDto.getCaregiver() != null) {
                RelatedPerson caregiver = relatedPersonResourceService.getResourceById(patientDto.getCaregiver());
//                EmcareResource caregiverResource = findByResourceId(patientDto.getCaregiver());
//                RelatedPerson caregiver = parser.parseResource(RelatedPerson.class, caregiverResource.getText());
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

    @Override
    public Map<String, Integer> getPatientAgeGroupCount() {
        Map<String, Integer> map = new HashMap<>();

        List<PatientDto> patientDtos = getAllPatientsForChart();
        for (PatientDto patientDto : patientDtos) {
            if (patientDto.getDob() != null) {
                Integer age = calculateAge(patientDto.getDob());
                String key = age.toString() + " to " + (age + 1) + " Years";
                if (map.get(key) != null) {
                    map.put(key, map.get(key) + 1);
                } else {
                    map.put(key, 1);
                }
            }
        }
        return map;
    }

    @Override
    public List<PatientDto> getPatientDtoByIds(List<String> ids) {
        List<Patient> patientsList = new ArrayList<>();
        List<PatientDto> patientDtosList;
        List<EmcareResource> resourcesList;
        resourcesList = repository.findByResourceIdIn(ids);

        for (EmcareResource emcareResource : resourcesList) {
            Patient patient = parser.parseResource(Patient.class, emcareResource.getText());
            patientsList.add(patient);
        }

        patientDtosList = EmcareResourceMapper.patientEntitiesToDtoMapper(patientsList);

        //Converting caregiverId and locationid to name
        for (PatientDto patientDto : patientDtosList) {

            if (patientDto.getCaregiver() != null) {
                RelatedPerson caregiver = relatedPersonResourceService.getResourceById(patientDto.getCaregiver());
//                EmcareResource caregiverResource = findByResourceId(patientDto.getCaregiver());
//                RelatedPerson caregiver = parser.parseResource(RelatedPerson.class, caregiverResource.getText());
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
        return patientDtosList;
    }

    private List<PatientDto> getAllPatientsForChart() {
        List<Patient> patientsList = new ArrayList<>();
        List<PatientDto> patientDtosList;

        List<EmcareResource> resourcesList = retrieveResourcesByType("PATIENT", null, null);

        for (EmcareResource emcareResource : resourcesList) {
            Patient patient = parser.parseResource(Patient.class, emcareResource.getText());
            patientsList.add(patient);
        }

        String loggedInUserId = emCareSecurityUser.getLoggedInUserId();
        userLocationMappingRepository.findByUserId(loggedInUserId);
        patientDtosList = EmcareResourceMapper.patientEntitiesToDtoMapper(patientsList);

        //Converting caregiverId and locationid to name
        for (PatientDto patientDto : patientDtosList) {

            if (patientDto.getCaregiver() != null) {
                RelatedPerson caregiver = relatedPersonResourceService.getResourceById(patientDto.getCaregiver());
//                EmcareResource caregiverResource = findByResourceId(patientDto.getCaregiver());
//                RelatedPerson caregiver = parser.parseResource(RelatedPerson.class, caregiverResource.getText());
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

    private Integer calculateAge(Date dob) {
        LocalDate curDate = LocalDate.now();
        LocalDate date = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Period.between(date, curDate).getYears();
    }

}