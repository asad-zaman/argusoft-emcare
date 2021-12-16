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

    public static List<PatientDto> entitiesToDtoMapper(List<Patient> patients) {
        List<PatientDto> patientDtos = new ArrayList<>();
        
        for (Patient p : patients) {
            PatientDto pDto = new PatientDto();
            
            pDto.setId(p.getIdElement().getIdPart());
            pDto.setIdentifier(p.getIdentifier().get(0).getValue());
            pDto.setGivenName(p.getName().get(0).getGiven().get(0).asStringValue());
            pDto.setFamilyName(p.getName().get(0).getFamily());
            pDto.setGender(p.getGender().getDisplay());
            pDto.setDob(p.getBirthDate());
            //Caregiver
            if(p.hasLink()){
                pDto.setCaregiver(p.getLinkFirstRep().getOther().getIdentifier().getValue());
            }
            //Location
            if(p.hasExtension()){
                Extension locationExtension = p.getExtension().get(0);
                String locationId = ((Identifier) locationExtension.getValue()).getValue();
                pDto.setLocation(locationId);
            }
            patientDtos.add(pDto);
        }
        return patientDtos;
    }
}
