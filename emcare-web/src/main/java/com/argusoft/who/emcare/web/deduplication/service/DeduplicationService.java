package com.argusoft.who.emcare.web.deduplication.service;

import org.hl7.fhir.r4.model.Patient;
import org.springframework.http.ResponseEntity;


public interface DeduplicationService {

    public Boolean comparePatients(Patient p1, Patient p2);

    public Boolean checkPatientDuplicates(Patient p);

    public ResponseEntity<Object> getAllDuplicatePatientRecords();

}
