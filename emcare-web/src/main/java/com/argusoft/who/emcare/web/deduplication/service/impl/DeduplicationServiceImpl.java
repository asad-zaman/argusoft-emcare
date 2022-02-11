package com.argusoft.who.emcare.web.deduplication.service.impl;

import com.argusoft.who.emcare.web.deduplication.service.DeduplicationService;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

@Service
public class DeduplicationServiceImpl implements DeduplicationService {

    @Override
    public Boolean comparePatients(Patient p1, Patient p2) {

     
        Boolean isDuplicateFlag;
        Boolean withoutNameFlag = !p1.hasName() || !p2.hasName();
        Boolean withoutAddressFlag = !p1.hasAddress() || !p2.hasAddress();
        Boolean withoutContactFlag = !p1.hasContact() || !p2.hasContact();
        Boolean withoutBirthDateFlag = !p1.hasBirthDate() || !p2.hasBirthDate();
        Boolean withoutGenderFlag = !p1.hasGender() || !p2.hasGender();
        Boolean sameNameFlag = !withoutNameFlag && p1.getNameFirstRep().equalsDeep(p2.getNameFirstRep());
        Boolean sameAddressFlag = !withoutAddressFlag && p1.getAddressFirstRep().equalsDeep(p2.getAddressFirstRep());
        Boolean sameContactFlag = !withoutContactFlag  && p1.getContactFirstRep().equalsDeep(p2.getContactFirstRep());
        Boolean sameBirthDateFlag = !withoutBirthDateFlag && p1.getBirthDate().equals(p2.getBirthDate());
        Boolean sameGenderFlag = !withoutGenderFlag && p1.getGender().equals(p2.getGender());

        //Tests
        if(withoutBirthDateFlag || (withoutAddressFlag && withoutContactFlag)) {
            isDuplicateFlag = null;
        } else {
            isDuplicateFlag = ((withoutNameFlag || sameNameFlag) && (withoutGenderFlag || sameGenderFlag) && (withoutAddressFlag || sameAddressFlag) && (withoutContactFlag || sameContactFlag) && sameBirthDateFlag);
        }

        return isDuplicateFlag;
    }

    @Override
    public Boolean checkPatientDuplicates(Patient p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
