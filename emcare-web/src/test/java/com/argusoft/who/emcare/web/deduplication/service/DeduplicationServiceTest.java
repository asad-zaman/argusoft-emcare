package com.argusoft.who.emcare.web.deduplication.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.argusoft.who.emcare.web.deduplication.service.impl.DeduplicationServiceImpl;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

    public final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
    }

    String patient1 = "Patient1";
    String patient2 = "Patient2";
    String patient3 = "Patient3";


    @Test
    void comparePatients() throws IOException {
        Patient patientData1 = getPatientData(patient1);
        Patient patientData2 = getPatientData(patient2);
        Patient patientData3 = getPatientData(patient3);

        assertTrue(deduplicationService.comparePatients(patientData1, patientData1));
        assertFalse(deduplicationService.comparePatients(patientData1,patientData2));
        assertTrue(deduplicationService.comparePatients(patientData1,patientData3));
        assertFalse(deduplicationService.comparePatients(patientData1,patientData2));
        assertFalse(deduplicationService.comparePatients(patientData2,patientData3));
        assertTrue(deduplicationService.comparePatients(patientData3,patientData3));
    }


    @Test
    void testCheckPatientDuplicates() throws IOException {

        Patient patientData1 = getPatientData(patient1);
        Patient patientData2 = getPatientData(patient2);
        assertThrows(UnsupportedOperationException.class, () -> deduplicationService.checkPatientDuplicates(patientData1));
    }

    @Test
    void testGetAllDuplicatePatientRecords() throws IOException {
        Patient patientData1 = getPatientData(patient1);
        Patient patientData2 = getPatientData(patient2);
        Patient patientData3 = getPatientData(patient3);

        // Mock the getAllPatientResources method to return the test patients
        List<Patient> patients = new ArrayList<>();
        patients.add(patientData1);
        patients.add(patientData2);
        patients.add(patientData3);
        when(emcareResourceService.getAllPatientResources()).thenReturn(patients);

        // Call the method under test
        ResponseEntity<Object> response = deduplicationService.getAllDuplicatePatientRecords();
        System.out.println(response);
        assertNotNull(response);
    }

    private Patient getPatientData(String patientPath) throws IOException {
        File file = new File("src/test/resources/mockdata/Deduplication/"+patientPath+".json");
        InputStream fileInputStream = new FileInputStream(file);
        String jsonString = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8);
        return convertJsonToPatient(jsonString);
    }
    public static Patient convertJsonToPatient(String jsonString) {
        FhirContext fhirContext = FhirContext.forR4();
        IParser parser = fhirContext.newJsonParser();
        Patient patient = parser.parseResource(Patient.class, jsonString);
        return patient;
    }

}