package com.argusoft.who.emcare.web.fhir.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dto.PatientDto;
import com.argusoft.who.emcare.web.fhir.dto.QuestionnaireDto;
import com.argusoft.who.emcare.web.fhir.mapper.EmcareResourceMapper;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import com.argusoft.who.emcare.web.fhir.model.QuestionnaireMaster;
import com.argusoft.who.emcare.web.fhir.service.*;
import com.argusoft.who.emcare.web.location.model.LocationMaster;
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
            patientDto.setCaregiver(caregiver.getNameFirstRep().getGiven().get(0) + " " + caregiver.getNameFirstRep().getFamily());
        }

        if (patientDto.getLocation() != null) {
            LocationMaster location = locationService.getLocationMasterById(Integer.parseInt(patientDto.getLocation()));
            patientDto.setLocation(location.getName());
        }

        return patientDto;
    }

    @GetMapping("/questionnaire")
    public List<QuestionnaireDto> getAllQuestionnaires() {
        List<QuestionnaireMaster> questionnaireMasters = questionnaireMasterService.retrieveAllQuestionnaires();
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
                                       @Nullable @RequiredParam(name = "search") String searchString) {
        return structureMapResourceService.getStructureMapPage(pageNo, searchString);
    }

    @GetMapping("/structure-definition")
    public PageDto getStructureDefinitionPage(@RequiredParam(name = "pageNo") Integer pageNo,
                                              @Nullable @RequiredParam(name = "search") String searchString) {
        return structureDefinitionService.getStructureDefinitionPage(pageNo, searchString);
    }

    @GetMapping("/code-system")
    public PageDto getCodeSystemPage(@RequiredParam(name = "pageNo") Integer pageNo,
                                     @Nullable @RequiredParam(name = "search") String searchString) {
        return codeSystemResourceService.getCodeSystemPage(pageNo, searchString);
    }

}
