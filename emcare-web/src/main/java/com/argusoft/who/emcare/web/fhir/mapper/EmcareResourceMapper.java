package com.argusoft.who.emcare.web.fhir.mapper;

import com.argusoft.who.emcare.web.fhir.dto.*;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author parth
 */
public class EmcareResourceMapper {

    private EmcareResourceMapper() {
    }

    public static PatientDto patientEntityToDtoMapper(Patient p) {
        PatientDto pDto = new PatientDto();

        pDto.setId(p.getIdElement().getIdPart());
        pDto.setIdentifier(p.getIdentifier().get(0).getValue());
        if (p.hasName()) {
            if (p.getNameFirstRep().hasGiven()) {
                pDto.setGivenName(p.getNameFirstRep().getGivenAsSingleString());
            }
            if (p.getNameFirstRep().hasFamily()) {
                pDto.setFamilyName(p.getNameFirstRep().getFamily());
            }
        }
        if (p.hasGender()) {
            pDto.setGender(p.getGender().getDisplay());
        }
        pDto.setDob(p.getBirthDate());

        //Caregiver
        if (p.hasLink()) {
            pDto.setCaregiver(p.getLinkFirstRep().getOther().getIdentifier().getValue());
        }

        //Location
        if (p.hasExtension()) {
            Extension locationExtension = p.getExtension().get(0);
            String locationId = ((Identifier) locationExtension.getValue()).getValue();
            pDto.setLocation(locationId);
        }

        //Address
        if (p.hasAddress()) {
            if (p.getAddressFirstRep().hasLine()) {
                pDto.setAddressLine(p.getAddressFirstRep().getLine().get(0).toString());
            }
            pDto.setAddressCity(p.getAddressFirstRep().getCity());
            pDto.setAddressCountry(p.getAddressFirstRep().getCountry());
            pDto.setAddressPostalCode(p.getAddressFirstRep().getPostalCode());
        }

        return pDto;
    }

    public static List<PatientDto> patientEntitiesToDtoMapper(List<Patient> patients) {
        List<PatientDto> patientDtos = new ArrayList<>();

        for (Patient p : patients) {
            patientDtos.add(patientEntityToDtoMapper(p));
        }
        return patientDtos;
    }

    public static QuestionnaireDto questionnaireEntityToDtoMapper(Questionnaire q) {
        QuestionnaireDto qDto = new QuestionnaireDto();

        qDto.setId(q.getIdElement().getIdPart());
        qDto.setName(q.getName());
        qDto.setTitle(q.getTitle());
        qDto.setDescription(q.getDescription());

        return qDto;
    }

    public static List<QuestionnaireDto> questionnaireEntitiesToDtoMapper(List<Questionnaire> questionnaires) {
        List<QuestionnaireDto> questionnaireDtos = new ArrayList<>();

        for (Questionnaire q : questionnaires) {
            questionnaireDtos.add(questionnaireEntityToDtoMapper(q));
        }
        return questionnaireDtos;
    }

    public static StructureMapDto getStructureMapDto(StructureMap map) {
        StructureMapDto mapDto = new StructureMapDto();

        mapDto.setId(map.getIdElement().getIdPart());
        mapDto.setName(map.getName());
        mapDto.setTitle(map.getTitle());
        mapDto.setDescription(map.getDescription());
        mapDto.setPublisher(map.getPublisher());

        return mapDto;
    }

    public static StructureDefinitionDto getStructureDefinitionDto(StructureDefinition definition) {
        StructureDefinitionDto dto = new StructureDefinitionDto();

        dto.setId(definition.getIdElement().getIdPart());
        dto.setName(definition.getName());
        dto.setTitle(definition.getTitle());
        dto.setDescription(definition.getDescription());
        dto.setPublisher(definition.getPublisher());

        return dto;
    }

    public static CodeSystemDto getCodeSystemDto(CodeSystem codeSystem) {
        CodeSystemDto dto = new CodeSystemDto();

        dto.setId(codeSystem.getIdElement().getIdPart());
        dto.setName(codeSystem.getName());
        dto.setTitle(codeSystem.getTitle());
        dto.setDescription(codeSystem.getDescription());
        dto.setPublisher(codeSystem.getPublisher());

        return dto;
    }

    public static MedicationDto getMedicationDto(Medication medication) {
        MedicationDto dto = new MedicationDto();

        dto.setId(medication.getIdElement().getIdPart());
        dto.setStatus(medication.getStatus() != null ? medication.getStatus().getDisplay() : "NA/NP");
        dto.setCode(getMedicationCodeDtoList(medication.getCode().getCoding()));
        dto.setForm(getMedicationCodeDtoList(medication.getForm().getCoding()));
        return dto;
    }

    public static List<MedicationCodeDto> getMedicationCodeDtoList(List<Coding> codings) {
        List<MedicationCodeDto> code = new ArrayList<>();
        for (Coding coding : codings) {
            MedicationCodeDto medicationCodeDto = new MedicationCodeDto();
            medicationCodeDto.setCode(coding.getCode());
            medicationCodeDto.setDisplay(coding.getDisplay());
            code.add(medicationCodeDto);
        }
        return code;
    }

    public static FacilityDto getFacilityDto(Location location,String id) {
        FacilityDto dto = new FacilityDto();
        dto.setFacilityName(location.getName());
        dto.setFacilityId(id);
        dto.setAddress(location.getAddress().getLine().get(0).getValue());
        dto.setOrganizationId(location.getManagingOrganization().getId());
        dto.setOrganizationName(location.getManagingOrganization().getDisplay());
        return dto;
    }
}
