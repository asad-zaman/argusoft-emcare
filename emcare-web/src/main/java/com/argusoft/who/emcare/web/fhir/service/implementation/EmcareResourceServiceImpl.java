package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.*;
import com.argusoft.who.emcare.web.fhir.dto.EmcareResourceDto;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.dto.PatientDto;
import com.argusoft.who.emcare.web.fhir.mapper.EmcareResourceMapper;
import com.argusoft.who.emcare.web.fhir.model.*;
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
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
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
    private String searchString;

    String query = "WITH MAX_CONSULTATION_DATE AS\n" +
        "(SELECT PATIENT_ID,\n" +
        "MAX(CONSULTATION_DATE) as cnslDate\n" +
        "FROM QUESTIONNAIRE_RESPONSE\n" +
        "GROUP BY PATIENT_ID)\n" +
        "SELECT\n" +
        "CONCAT('Patient',' ',row_number() over (ORDER BY emcare_resources.id)) as \"key\",\n" +
        "EMCARE_RESOURCES.resource_id, \n" +
        "cast((EMCARE_RESOURCES.text) as json) -> cast('identifier' as text) -> 0 ->> cast('value' as text) as \"identifier\",\n" +
        "CONCAT(cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 0, ' ', cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 1) as \"givenName\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 ->> cast('family' as text) as \"familyName\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) ->> cast('gender' as text) as \"gender\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) ->> cast('birthDate' as text) as \"birthDate\",\n" +
        "cast((LOCATION_RESOURCES.text) as json) ->> cast('name' as text) as \"facilityName\",\n" +
        "cast((LOCATION_RESOURCES.text) as json) -> cast('address' as text) -> cast('line' as text) ->> 0 as \"addressLine\",\n" +
        "(LOCATION_RESOURCES.organization_name) as \"organizationName\",\n" +
        "(LOCATION_RESOURCES.location_name) as \"locationName\",\n" +
        "MAX_CONSULTATION_DATE.cnslDate as \"consultationDate\"\n" +
        "FROM EMCARE_RESOURCES\n" +
        "LEFT OUTER JOIN MAX_CONSULTATION_DATE ON EMCARE_RESOURCES.RESOURCE_ID = MAX_CONSULTATION_DATE.PATIENT_ID\n" +
        "LEFT JOIN LOCATION_RESOURCES ON EMCARE_RESOURCES.facility_id = LOCATION_RESOURCES.resource_id ORDER BY EMCARE_RESOURCES.created_on DESC limit 10";


    String queryForAll = "WITH MAX_CONSULTATION_DATE AS\n" +
        "(SELECT PATIENT_ID,\n" +
        "MAX(CONSULTATION_DATE) as cnslDate\n" +
        "FROM QUESTIONNAIRE_RESPONSE\n" +
        "GROUP BY PATIENT_ID)\n" +
        "SELECT\n" +
        "CONCAT('Patient',' ',row_number() over (ORDER BY emcare_resources.id)) as \"key\",\n" +
        "EMCARE_RESOURCES.resource_id, \n" +
        "cast((EMCARE_RESOURCES.text) as json) -> cast('identifier' as text) -> 0 ->> cast('value' as text) as \"identifier\",\n" +
        "CONCAT(cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 0, ' ', cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 1) as \"givenName\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 ->> cast('family' as text) as \"familyName\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) ->> cast('gender' as text) as \"gender\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) ->> cast('birthDate' as text) as \"birthDate\",\n" +
        "cast((LOCATION_RESOURCES.text) as json) ->> cast('name' as text) as \"facilityName\",\n" +
        "cast((LOCATION_RESOURCES.text) as json) -> cast('address' as text) -> cast('line' as text) ->> 0 as \"addressLine\",\n" +
        "(LOCATION_RESOURCES.organization_name) as \"organizationName\",\n" +
        "(LOCATION_RESOURCES.location_name) as \"locationName\",\n" +
        "MAX_CONSULTATION_DATE.cnslDate as \"consultationDate\"\n" +
        "FROM EMCARE_RESOURCES\n" +
        "LEFT OUTER JOIN MAX_CONSULTATION_DATE ON EMCARE_RESOURCES.RESOURCE_ID = MAX_CONSULTATION_DATE.PATIENT_ID\n" +
        "LEFT JOIN LOCATION_RESOURCES ON EMCARE_RESOURCES.facility_id = LOCATION_RESOURCES.resource_id ORDER BY EMCARE_RESOURCES.created_on DESC";

    @Autowired
    EmcareResourceRepository repository;

    @Autowired
    EmcareResourceCustomRepository customRepository;

    @Autowired
    LocationMasterDao locationMasterDao;
    @Autowired
    EmCareSecurityUser emCareSecurityUser;
    @Autowired
    UserLocationMappingRepository userLocationMappingRepository;
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
    @Autowired
    ConditionResourceService conditionResourceService;
    @Autowired
    EncounterResourceRepository encounterResourceRepository;
    @Autowired
    ObservationResourceRepository observationResourceRepository;
    @Autowired
    BinaryResourceService binaryResourceService;

    @Autowired
    AuditEventResourceService auditEventResourceService;

    @Autowired
    private LocationService locationService;
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
            case CommonConstant.CONDITION:
                Condition condition = conditionResourceService.getResourceById(resourceId);
                if (condition != null) {
                    conditionResourceService.updateConditionResource(resource.getIdElement(), parser.parseResource(Condition.class, resourceString));
                } else {
                    conditionResourceService.saveResource(parser.parseResource(Condition.class, resourceString));
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
            case CommonConstant.BINARY_TYPE_STRING:
                Binary binary = binaryResourceService.getResourceById(resourceId);
                if (binary != null) {
                    binaryResourceService.updateBinaryResource(resource.getIdElement(), binary);
                } else {
                    binaryResourceService.saveResource(parser.parseResource(Binary.class, resourceString));
                }
                break;
            case CommonConstant.AUDITEVENT_TYPE_STRING:
                AuditEvent auditEvent = auditEventResourceService.getResourceById(resourceId);
                if (auditEvent != null) {
                    auditEventResourceService.updateAuditEventResource(resource.getIdElement(), auditEvent);
                } else {
                    auditEventResourceService.saveResource(parser.parseResource(AuditEvent.class, resourceString));
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

        Integer totalCount = 0;
        List<Map<String, Object>> resourcesList;
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE);
        this.searchString = searchString;
        PageDto pageDto = new PageDto();

        if (searchString != null && !searchString.isEmpty()) {
            totalCount = repository.getCountOfPatientsByTypeContainingAndTextContainingIgnoreCase(searchString);
            List<EmcareResourceDto> patients = repository.getPatientsByTypeContainingAndTextContainingIgnoreCase(searchString, page);
            pageDto.setList(patients);
            pageDto.setTotalCount(totalCount.longValue());
        } else {
            totalCount = repository.getCountOfPatients();
            resourcesList = customRepository.getPatientsList(query + " offset " + page.getOffset(), pageNo);
            pageDto.setList(resourcesList);
            pageDto.setTotalCount(totalCount.longValue());
        }

        return pageDto;
    }

    @Override
    public PageDto getPatientsAllDataByFilter(String searchString, Object locationId) {

        Integer totalCount = 0;
        List<Map<String, Object>> resourcesList;
        Pageable page = PageRequest.ofSize(CommonConstant.PAGE_SIZE);
        this.searchString = searchString;
        PageDto pageDto = new PageDto();
        pageDto.setTotalCount(totalCount.longValue());
        if (searchString != null && !searchString.isEmpty()) {
            List<EmcareResourceDto> patients = repository.getPatientForExportWithSearch(searchString);
            pageDto.setList(patients);
        } else {
            resourcesList = customRepository.getPatientsList(queryForAll);
            pageDto.setList(resourcesList);
            pageDto.setTotalCount(totalCount.longValue());
        }

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
    public PageDto getPatientUnderLocationId(Object locationId,String searchString, Integer pageNo, String sDate, String eDate) {
 
        Long offSet = pageNo.longValue() * 10;
        List<Integer> locationIds;
        List<String> childFacilityIds = new ArrayList<>();
        if (Objects.nonNull(locationId)) {
            if (isNumeric(locationId.toString())) {
                locationIds = locationMasterDao.getAllChildLocationId(Integer.parseInt(locationId.toString()));
                childFacilityIds = locationResourceRepository.findResourceIdIn(locationIds);
            } else {
                childFacilityIds.add(locationId.toString());
            }
        }
        Date startDate = null;
        Date endDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (Objects.isNull(sDate) || sDate.isEmpty()) {
                String sDate1 = "1998-12-31";
                sDate = sdf.format(sdf.parse(sDate1));
            }
            if (Objects.isNull(eDate) || eDate.isEmpty()) {
                eDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            startDate = simpleDateFormat.parse(sDate);
            endDate = simpleDateFormat.parse(eDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Long totalCount = 0L;
        List<Map<String, Object>> resourcesList = new ArrayList<>();
        if (Objects.isNull(locationId) || locationId.toString().isEmpty()) {
            if (searchString != null && !searchString.isEmpty()){
                totalCount = Long.valueOf(repository.getFilteredDateAndSearchStringOnlyCount(searchString,startDate,endDate).size());
                resourcesList = repository.getFilteredDateAndSearchString(searchString,startDate,endDate,offSet);
            }
            else {
                totalCount = Long.valueOf(repository.getFilteredDateOnlyCount(startDate, endDate).size());
                resourcesList = repository.getFilteredDateOnly(startDate, endDate, offSet);
            }
        } else {
            if (searchString != null && !searchString.isEmpty()){
                totalCount = Long.valueOf(repository.getFilteredPatientsInAndSearchStringCount(childFacilityIds,searchString,startDate,endDate).size());
                resourcesList = repository.getFilteredPatientsInAndSearchString(childFacilityIds,searchString,startDate,endDate,offSet);

            }else {
                totalCount = Long.valueOf(repository.getFilteredPatientsInCount(childFacilityIds, startDate, endDate).size());
                resourcesList = repository.getFilteredPatientsIn(childFacilityIds, startDate, endDate, offSet);
            }
        }
        PageDto pageDto = new PageDto();
        pageDto.setList(resourcesList);
        pageDto.setTotalCount(totalCount);
        return pageDto;
    }

    @Override
    public List<String> getPatientIdsUnderFacility(String facilityId) {
        List<String> facilityIds = new ArrayList<>();
        facilityIds = locationResourceService.getAllChildFacilityIds(facilityId);
        List<EmcareResource> emcareResources = repository.findByFacilityIdIn(facilityIds);
        return emcareResources.stream().map(EmcareResource::getResourceId).collect(Collectors.toList());
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
            List<Integer> assignedLocationIds = locationResourceRepository.findAllLocationId(assignedFacilityIds).stream().mapToInt(Long::intValue).boxed().collect(Collectors.toList());
            List<Integer> childLocations = locationMasterDao.getAllChildLocationIdWithMultipalLocationId(assignedLocationIds);
            List<String> childFacilityIds = locationResourceRepository.findResourceIdIn(childLocations);
            patientsList = patientsList.stream().filter(e -> childFacilityIds.contains(((Identifier) e.getExtension().get(0).getValue()).getValue())).collect(Collectors.toList());
        }

        patientDtosList = EmcareResourceMapper.patientEntitiesToDtoMapper(patientsList);

        //Converting caregiverId and locationid to name
        for (PatientDto patientDto : patientDtosList) {

            if (patientDto.getCaregiver() != null) {
                RelatedPerson caregiver = relatedPersonResourceService.getResourceById(patientDto.getCaregiver());
                if (caregiver != null) {
                    patientDto.setCaregiver(caregiver.getNameFirstRep().getGiven().get(0) + " " + caregiver.getNameFirstRep().getFamily());
                } else {
                    patientDto.setCaregiver(null);
                }
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
                if (caregiver != null) {
                    patientDto.setCaregiver(caregiver.getNameFirstRep().getGiven().get(0) + " " + caregiver.getNameFirstRep().getFamily());
                } else {
                    patientDto.setCaregiver(null);
                }
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
    public Map<String, Object> getPatientAgeGroupCount() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> maps = repository.getPieChartDataBasedOnAgeGroup();
        for (Map<String, Object> map1 : maps) {
            map.put(map1.get("key").toString(), map1.get("value"));
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
                if (caregiver != null) {
                    patientDto.setCaregiver(caregiver.getNameFirstRep().getGiven().get(0) + " " + caregiver.getNameFirstRep().getFamily());
                } else {
                    patientDto.setCaregiver(null);
                }
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

    /**
     * @param theId Patient Id
     * @return Bundle resource
     */
    @Override
    public Bundle getPatientBundle(String theId) {

        Bundle bundle = new Bundle();
//        GET PATIENT DETAILS
        EmcareResource emcareResource = repository.findByResourceId(theId);
        Patient patient = null;
        if (emcareResource != null) {
            patient = parser.parseResource(Patient.class, emcareResource.getText());
        }
        bundle.addEntry(new Bundle.BundleEntryComponent().setResource(patient));

//        GET AND ADD ENCOUNTER OF PATIENT IN BUNDLE
        List<EncounterResource> encounterResources = encounterResourceRepository.findByPatientId(theId);
        for (EncounterResource encounterResource : encounterResources) {
            Encounter encounter = parser.parseResource(Encounter.class, encounterResource.getText());
            bundle.addEntry(new Bundle.BundleEntryComponent().setResource(encounter));
        }

//        GET AND ADD OBSERVATION OF PATIENT IN BUNDLE
        List<ObservationResource> observationResources = observationResourceRepository.findBySubjectIdAndSubjectType(theId, "Patient");
        for (ObservationResource observationResource : observationResources) {
            Observation observation = parser.parseResource(Observation.class, observationResource.getText());
            bundle.addEntry(new Bundle.BundleEntryComponent().setResource(observation));
        }
        return bundle;
    }

    @Override
    public Bundle getPatientCountBasedOnDate(String summaryType, DateParam theDate, String theId) {
        List<String> facilityIds = new ArrayList<>();
        if (!theId.isEmpty()) {
            facilityIds = locationResourceService.getAllChildFacilityIds(theId);
        }
        Long count = 0l;
        if (summaryType.equalsIgnoreCase(CommonConstant.SUMMARY_TYPE_COUNT)) {
            if (Objects.isNull(theDate)) {
                if (theId.isEmpty()) {
                    count = repository.getCount();
                } else {
                    count = repository.getCountWithFacilityId(facilityIds);
                }
            } else {
                if (Objects.isNull(theId)) {
                    count = repository.getCountBasedOnDate(theDate.getValue());
                } else {
                    count = repository.getCountBasedOnDateWithFacilityId(theDate.getValue(), facilityIds);
                }
            }
        } else {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.setTotal(count.intValue());
        return bundle;

    }

    private List<PatientDto> getAllPatientsForChart() {
        List<Patient> patientsList = new ArrayList<>();
        List<PatientDto> patientDtosList;

        List<EmcareResource> resourcesList = retrieveResourcesByType("PATIENT", null, null);

        for (EmcareResource emcareResource : resourcesList) {
            Patient patient = parser.parseResource(Patient.class, emcareResource.getText());
            patientsList.add(patient);
        }

        patientDtosList = EmcareResourceMapper.patientEntitiesToDtoMapper(patientsList);
        return patientDtosList;
    }

    private Integer calculateAge(Date dob) {
        LocalDate curDate = LocalDate.now();
        LocalDate date = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Period.between(date, curDate).getYears();
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
