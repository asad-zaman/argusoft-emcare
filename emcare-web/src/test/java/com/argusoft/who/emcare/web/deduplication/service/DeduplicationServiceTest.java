package com.argusoft.who.emcare.web.deduplication.service;

import com.argusoft.who.emcare.web.deduplication.service.impl.DeduplicationServiceImpl;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import org.hl7.fhir.Id;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.argusoft.who.emcare.web.deduplication.service.DeduplicationService;
import com.argusoft.who.emcare.web.fhir.dto.PatientDto;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DeduplicationServiceTest {

    @InjectMocks
    private DeduplicationServiceImpl deduplicationService;

    @Mock
    private EmcareResourceService emcareResourceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void comparePatients() {
    }


    @Test
    void testCheckPatientDuplicates() {
        Patient p1 = createTestPatient("John", "Doe",Enumerations.AdministrativeGender.MALE);
        assertThrows(UnsupportedOperationException.class, () -> deduplicationService.checkPatientDuplicates(p1));
    }

    @Test
    void testGetAllDuplicatePatientRecords() {
        //test patients
        Patient p1 = createTestPatient("John", "Doe",Enumerations.AdministrativeGender.MALE);
        Patient p2 = createTestPatient("Jane", "Doe",Enumerations.AdministrativeGender.FEMALE);
        Patient p3 = createTestPatient("John", "Doe",Enumerations.AdministrativeGender.MALE);

        // Mock the getAllPatientResources method to return the test patients
        List<Patient> patients = new ArrayList<>();
        patients.add(p1);
        patients.add(p2);
        patients.add(p3);
        when(emcareResourceService.getAllPatientResources()).thenReturn(patients);

        // Call the method under test
        ResponseEntity<Object> response = deduplicationService.getAllDuplicatePatientRecords();
        assertNotNull(response);
    }

    // Helper method to create test patients
    private Patient createTestPatient(String firstName, String lastName, Enumerations.AdministrativeGender gender) {
        Patient patient = new Patient();
        patient.addName().setFamily(lastName).addGiven(firstName);
        patient.setGender(gender);
        patient.setBirthDate(new Date());
        // Set other patient attributes if needed for your test cases
        return patient;
    }
}