package com.argusoft.who.emcare.web.fhir.mapper;

import com.argusoft.who.emcare.web.fhir.dto.PatientDto;
import com.argusoft.who.emcare.web.fhir.dto.QuestionnaireDto;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Questionnaire;

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
}
