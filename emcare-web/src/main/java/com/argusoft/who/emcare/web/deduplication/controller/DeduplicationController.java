package com.argusoft.who.emcare.web.deduplication.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.argusoft.who.emcare.web.deduplication.service.DeduplicationService;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api/deduplication")
public class DeduplicationController {
    
    @Autowired
    DeduplicationService deduplicationService;
    
    @PostMapping("/compare")
    public Boolean comparePatients(
            @RequestBody String[] p) {
        FhirContext fhirCtx = FhirContext.forR4();
        IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);
        Patient p1 = parser.parseResource(Patient.class, p[0]);
        Patient p2 = parser.parseResource(Patient.class,p[1]);
        return deduplicationService.comparePatients(p1, p2);
    }
    
    @PostMapping("/check")
    public Boolean checkPatientDuplicates(
            @RequestBody Patient p) {
        return deduplicationService.checkPatientDuplicates(p);
    }
    
}
