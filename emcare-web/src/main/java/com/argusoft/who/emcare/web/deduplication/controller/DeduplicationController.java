package com.argusoft.who.emcare.web.deduplication.controller;

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
            @RequestBody Patient[] p) {
        return deduplicationService.comparePatients(p[0], p[1]);
    }
    
    @PostMapping("/check")
    public Boolean checkPatientDuplicates(
            @RequestBody Patient p) {
        return deduplicationService.checkPatientDuplicates(p);
    }
    
}
