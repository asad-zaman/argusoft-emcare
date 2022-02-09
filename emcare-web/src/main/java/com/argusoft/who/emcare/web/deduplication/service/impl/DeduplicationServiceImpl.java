package com.argusoft.who.emcare.web.deduplication.service.impl;

import com.argusoft.who.emcare.web.deduplication.service.DeduplicationService;
import org.hl7.fhir.r4.model.Patient;


public class DeduplicationServiceImpl implements DeduplicationService{

    @Override
    public Boolean comparePatients(Patient p1, Patient p2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean checkPatientDuplicates(Patient p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
