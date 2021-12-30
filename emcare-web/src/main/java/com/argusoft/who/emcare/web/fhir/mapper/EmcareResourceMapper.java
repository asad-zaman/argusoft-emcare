package com.argusoft.who.emcare.web.fhir.mapper;

import com.argusoft.who.emcare.web.fhir.dto.PatientDto;
import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;

/**
 *
 * @author parth
 */
public class EmcareResourceMapper {

    public static PatientDto entityToDtoMapper(Patient p) {
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

    public static List<PatientDto> entitiesToDtoMapper(List<Patient> patients) {
        List<PatientDto> patientDtos = new ArrayList<>();

        for (Patient p : patients) {
            patientDtos.add(entityToDtoMapper(p));
        }
        return patientDtos;
    }
}
