package com.argusoft.who.emcare.web.fhir.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.dto.PatientDto;
import com.argusoft.who.emcare.web.fhir.dto.QuestionnaireDto;
import com.argusoft.who.emcare.web.fhir.mapper.EmcareResourceMapper;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import com.argusoft.who.emcare.web.fhir.model.QuestionnaireMaster;
import com.argusoft.who.emcare.web.fhir.service.*;
import com.argusoft.who.emcare.web.location.service.LocationService;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
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
    private QuestionnaireMasterService questionnaireMasterService;

    @Autowired
    private LocationResourceService locationResourceService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private StructureMapResourceService structureMapResourceService;

    @Autowired
    private StructureDefinitionService structureDefinitionService;

    @Autowired
    private CodeSystemResourceService codeSystemResourceService;

    @Autowired
    private MedicationResourceService medicationResourceService;

    @Autowired
    private ActivityDefinitionResourceService activityDefinitionResourceService;

    @Autowired
    private OrganizationResourceService organizationResourceService;

    @Autowired
    private OperationDefinitionResourceService operationDefinitionResourceService;

    @Autowired
    private LibraryResourceService libraryResourceService;

    @GetMapping("/patient")
    public List<PatientDto> getAllPatients() {
        return emcareResourceService.getAllPatients();
    }

    @GetMapping("/patient/page")
    public PageDto getPatientsPage(@RequestParam(value = "pageNo") Integer pageNo,
                                   @Nullable @RequestParam(value = "search", required = false) String searchString) {
        return emcareResourceService.getPatientsPage(pageNo, searchString);
    }

    @GetMapping("/patient/locationId/{locationId}")
    public PageDto getAllPatientsUnderLocation(@PathVariable(value = "locationId") Integer locationId,
                                               @RequestParam(value = "pageNo") Integer pageNo) {
        return emcareResourceService.getPatientUnderLocationId(locationId, pageNo);
    }

    @GetMapping("/patient/{patientId}")
    public PatientDto getPatientById(@PathVariable String patientId) {
        EmcareResource emcareResource = emcareResourceService.findByResourceId(patientId);
        Patient patient = parser.parseResource(Patient.class, emcareResource.getText());
        PatientDto patientDto = EmcareResourceMapper.patientEntityToDtoMapper(patient);
        if (patientDto.getCaregiver() != null) {
            EmcareResource caregiverResource = emcareResourceService.findByResourceId(patientDto.getCaregiver());
            RelatedPerson caregiver = parser.parseResource(RelatedPerson.class, caregiverResource.getText());
            patientDto.setCaregiver(
                    caregiver.getNameFirstRep().getGiven().get(0) + " " + caregiver.getNameFirstRep().getFamily());
        }

        if (patientDto.getFacility() != null) {
            FacilityDto facilityDto = locationResourceService.getFacilityDto(patientDto.getFacility());
            patientDto.setFacility(facilityDto.getFacilityName());
            patientDto.setOrganizationName(facilityDto.getOrganizationName());
            patientDto.setLocationName(facilityDto.getLocationName());
        }

        return patientDto;
    }

    @GetMapping("/questionnaire")
    public List<QuestionnaireDto> getAllQuestionnaires() {
        List<QuestionnaireMaster> questionnaireMasters = questionnaireMasterService.retrieveAllQuestionnaires(null);
        List<Questionnaire> questionnaires = new ArrayList<>();

        for (QuestionnaireMaster qm : questionnaireMasters) {
            Questionnaire q = parser.parseResource(Questionnaire.class, qm.getText());
            questionnaires.add(q);
        }

        return EmcareResourceMapper.questionnaireEntitiesToDtoMapper(questionnaires);
    }

    @GetMapping("/questionnaire/page")
    public PageDto getQuestionnairesPage(@RequestParam(value = "pageNo") Integer pageNo) {
        return questionnaireMasterService.getQuestionnaireDtosPage(pageNo);
    }

    @GetMapping("/questionnaire/{questionnaireId}")
    public Questionnaire getQuestionnaireById(@PathVariable String questionnaireId) {
        QuestionnaireMaster qm = questionnaireMasterService.retrieveQuestionnaireByResourceId(questionnaireId);
        Questionnaire q = null;
        if (qm != null) {
            q = parser.parseResource(Questionnaire.class, qm.getText());
        }
        return q;
    }

    @GetMapping("/structure-map")
    public PageDto getStructureMapPage(@RequiredParam(name = "pageNo") Integer pageNo,
                                       @Nullable @RequiredParam(name = "search") String search) {
        return structureMapResourceService.getStructureMapPage(pageNo, search);
    }

    @GetMapping("/structure-definition")
    public PageDto getStructureDefinitionPage(@RequiredParam(name = "pageNo") Integer pageNo,
                                              @Nullable @RequiredParam(name = "search") String search) {
        return structureDefinitionService.getStructureDefinitionPage(pageNo, search);
    }

    @GetMapping("/code-system")
    public PageDto getCodeSystemPage(@RequiredParam(name = "pageNo") Integer pageNo,
                                     @Nullable @RequiredParam(name = "search") String search) {
        return codeSystemResourceService.getCodeSystemPage(pageNo, search);
    }

    @GetMapping("/medication")
    public PageDto getMedicationPage(@RequiredParam(name = "pageNo") Integer pageNo,
                                     @Nullable @RequiredParam(name = "search") String search) {
        return medicationResourceService.getMedicationPage(pageNo, search);
    }

    @GetMapping("/activity-definition")
    public PageDto getActivityDefinitionPage(@RequiredParam(name = "pageNo") Integer pageNo,
                                             @Nullable @RequiredParam(name = "search") String search) {
        return activityDefinitionResourceService.getActivityDefinitionPage(pageNo, search);
    }

    @GetMapping("/organization")
    public PageDto getOrganizationPage(@RequiredParam(name = "pageNo") Integer pageNo,
                                       @Nullable @RequiredParam(name = "search") String search) {
        return organizationResourceService.getOrganizationPage(pageNo, search);
    }

    @GetMapping("/facility")
    public PageDto getFacilityPage(@RequiredParam(name = "pageNo") Integer pageNo,
                                   @Nullable @RequiredParam(name = "search") String search) {
        return locationResourceService.getEmCareLocationResourcePage(pageNo, search);
    }

    @GetMapping("/library")
    public PageDto getLibraryPage(@RequiredParam(name = "pageNo") Integer pageNo,
                                  @Nullable @RequiredParam(name = "search") String search) {
        return libraryResourceService.getLibraryPage(pageNo, search);
    }

    @GetMapping("/operation-definition")
    public PageDto getOperationDefinitionPage(@RequiredParam(name = "pageNo") Integer pageNo,
                                              @Nullable @RequiredParam(name = "search") String search) {
        return operationDefinitionResourceService.getOperationDefinitionPage(pageNo, search);
    }

    @GetMapping("active/facility")
    public List<FacilityDto> getActiveFacility() {
        return locationResourceService.getActiveFacility();
    }

}
