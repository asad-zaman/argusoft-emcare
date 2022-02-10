package com.argusoft.who.emcare.web.deduplication.service.impl;

import com.argusoft.who.emcare.web.deduplication.service.DeduplicationService;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

@Service
public class DeduplicationServiceImpl implements DeduplicationService {

    @Override
    public Boolean comparePatients(Patient p1, Patient p2) {

        //Flags | Note: Age Flag yet to be implemented.
        Boolean isDuplicateFlag = false;
        Boolean sameNameFlag = p1.hasName() && p2.hasName() && p1.getNameFirstRep().equalsDeep(p2.getNameFirstRep());
        Boolean withoutNameFlag = !p1.hasName() && !p2.hasName();
        Boolean sameAddressFlag = p1.hasAddress() && p2.hasAddress() && p1.getAddressFirstRep().equalsDeep(p2.getAddressFirstRep());
        Boolean sameContactFlag = p1.hasContact() && p2.hasContact() && p1.getContactFirstRep().equalsDeep(p2.getContactFirstRep());
        Boolean sameBirthDateFlag = p1.hasBirthDate() && p2.hasBirthDate() && p1.getBirthDate().equals(p2.getBirthDate());

        //Tests
        if (withoutNameFlag) {
            isDuplicateFlag = (sameAddressFlag && sameContactFlag && sameBirthDateFlag);
        } else {
            isDuplicateFlag = (sameNameFlag && sameBirthDateFlag && (sameAddressFlag || sameContactFlag)); //Add age logic here
        }

        return isDuplicateFlag;
    }

    @Override
    public Boolean checkPatientDuplicates(Patient p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
