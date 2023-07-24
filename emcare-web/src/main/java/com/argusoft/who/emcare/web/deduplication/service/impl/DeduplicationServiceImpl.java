package com.argusoft.who.emcare.web.deduplication.service.impl;

import com.argusoft.who.emcare.web.deduplication.service.DeduplicationService;
import com.argusoft.who.emcare.web.fhir.dto.PatientDto;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DeduplicationServiceImpl implements DeduplicationService {

    @Autowired
    EmcareResourceService emcareResourceService;

    @Override
    public Boolean comparePatients(Patient p1, Patient p2) {


        Boolean withoutNameFlag = !p1.hasName() || !p2.hasName();
        Boolean withoutAddressFlag = !p1.hasAddress() || !p2.hasAddress();
        Boolean withoutContactFlag = !p1.hasContact() || !p2.hasContact();
        Boolean withoutBirthDateFlag = !p1.hasBirthDate() || !p2.hasBirthDate();
        Boolean withoutGenderFlag = !p1.hasGender() || !p2.hasGender();
        Boolean withoutCareGiver = !p1.hasLink() || !p2.hasLink();
        Boolean sameNameFlag = !withoutNameFlag && p1.getNameFirstRep().getNameAsSingleString().equalsIgnoreCase(p2.getNameFirstRep().getNameAsSingleString());
        Boolean sameAddressFlag = !withoutAddressFlag && (p1.getAddressFirstRep().getCity() + p1.getAddressFirstRep().getDistrict() + p1.getAddressFirstRep().getPostalCode()).equalsIgnoreCase(p1.getAddressFirstRep().getCity() + p1.getAddressFirstRep().getDistrict() + p1.getAddressFirstRep().getPostalCode());
        Boolean sameContactFlag = !withoutContactFlag && p1.getContactFirstRep().equalsDeep(p2.getContactFirstRep());
        Boolean sameBirthDateFlag = !withoutBirthDateFlag && p1.getBirthDate().equals(p2.getBirthDate());
        Boolean sameCareGiver = !withoutCareGiver && p1.getLinkFirstRep().getOther().getIdentifier().getValue().equalsIgnoreCase(p2.getLinkFirstRep().getOther().getIdentifier().getValue());
        Boolean sameGenderFlag = !withoutGenderFlag && p1.getGender().getDisplay().equalsIgnoreCase(p2.getGender().getDisplay());


        if (Boolean.TRUE.equals(withoutBirthDateFlag && sameNameFlag && sameContactFlag && sameAddressFlag && sameGenderFlag) && Boolean.TRUE.equals(sameCareGiver)) {
            return true;
        } else if ((withoutAddressFlag && withoutContactFlag) && (sameNameFlag && sameGenderFlag && sameCareGiver)) {
            return true;
        } else {
            return ((withoutNameFlag || sameNameFlag) && (sameGenderFlag) && (withoutAddressFlag || sameAddressFlag) && (withoutContactFlag || sameContactFlag) && (sameBirthDateFlag) && (withoutCareGiver || sameCareGiver));
        }
    }

    @Override
    public Boolean checkPatientDuplicates(Patient p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ResponseEntity<Object> getAllDuplicatePatientRecords() {
        List<List<PatientDto>> duplicateEntries = new ArrayList<>();

        List<PatientDto> patients = emcareResourceService.getAllPatientsList();

        List<PatientDto> patientDuplicates = new ArrayList<>(patients);

        for (PatientDto p1 : patients) {
            List<PatientDto> duplicate = new ArrayList<>();
            for (PatientDto p2 : patientDuplicates) {

                if (!Objects.equals(p1.getId(), p2.getId()) &&
                    Objects.equals(p1.getIdentifier(),p2.getIdentifier()) &&
                    Objects.equals(p1.getGivenName(),p2.getGivenName()) &&
                    Objects.equals(p1.getFamilyName(),p2.getFamilyName()) &&
                    Objects.equals(p1.getGender(),p2.getGender()) &&
                    Objects.equals(p1.getDob(),p2.getDob()) &&
                    Objects.equals(p1.getFacility(),p2.getFacility())
                ){
                    duplicate.add(p2);
                }
            }
            if (!duplicate.isEmpty()) {
                duplicate.add(p1);
                patientDuplicates.removeAll(duplicate);
                duplicateEntries.add(duplicate);
            }
        }

        return ResponseEntity.ok().body(duplicateEntries);
    }

}
