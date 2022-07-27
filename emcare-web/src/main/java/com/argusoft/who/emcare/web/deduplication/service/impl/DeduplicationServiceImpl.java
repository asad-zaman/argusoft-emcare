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

@Service
public class DeduplicationServiceImpl implements DeduplicationService {

    @Autowired
    EmcareResourceService emcareResourceService;

    @Override
    public Boolean comparePatients(Patient p1, Patient p2) {


        Boolean isDuplicateFlag;
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

        //Tests
//        if (withoutBirthDateFlag || (withoutAddressFlag && withoutContactFlag)) {
//            isDuplicateFlag = null;
//        } else {
//            isDuplicateFlag = ((withoutNameFlag || sameNameFlag) && (withoutGenderFlag || sameGenderFlag) && (withoutAddressFlag || sameAddressFlag) && (withoutContactFlag || sameContactFlag) && sameBirthDateFlag);
//        }

        if (withoutBirthDateFlag && sameNameFlag && sameContactFlag && sameAddressFlag && sameGenderFlag && sameCareGiver) {
            return true;
        } else if ((withoutAddressFlag && withoutContactFlag) && (sameNameFlag && sameGenderFlag && sameCareGiver)) {
            return true;
        } else {
            return ((withoutNameFlag || sameNameFlag) && (sameGenderFlag) && (withoutAddressFlag || sameAddressFlag) && (withoutContactFlag || sameContactFlag) && (sameBirthDateFlag) && (withoutCareGiver || sameCareGiver));
        }
//        return isDuplicateFlag;
    }

    @Override
    public Boolean checkPatientDuplicates(Patient p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ResponseEntity<Object> getAllDuplicatePatientRecords() {
        List<List<Patient>> duplicateEntries = new ArrayList<>();
        List<List<PatientDto>> duplicateDtoEntries = new ArrayList<>();

        List<Patient> patients = emcareResourceService.getAllPatientResources();
        List<Patient> patientDuplicates = new ArrayList<>(patients);

        for (Patient p1 : patients) {
            List<Patient> duplicate = new ArrayList<>();
            for (Patient p2 : patientDuplicates) {
                if (p1.getIdElement().getIdPart() != p2.getIdElement().getIdPart() && comparePatients(p1, p2)) {
                    duplicate.add(p2);
                }
            }
            if (!duplicate.isEmpty()) {
                duplicate.add(p1);
                patientDuplicates.removeAll(duplicate);
                duplicateEntries.add(duplicate);
            }
        }

        for (List<Patient> entry : duplicateEntries) {
            duplicateDtoEntries.add(emcareResourceService.getPatientDtoByPatient(entry));
        }

        return ResponseEntity.ok().body(duplicateDtoEntries);
    }

}
