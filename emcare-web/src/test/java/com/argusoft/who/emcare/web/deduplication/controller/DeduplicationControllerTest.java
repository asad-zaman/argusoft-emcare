package com.argusoft.who.emcare.web.deduplication.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.argusoft.who.emcare.web.deduplication.service.DeduplicationService;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DeduplicationControllerTest {
    @Mock
    private DeduplicationService deduplicationService;

    @InjectMocks
    private DeduplicationController deduplicationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    String patient1 = "Patient1";
    String patient2 = "Patient2";
    String patient3 = "Patient3";

    @Test
    void testComparePatients() throws IOException {
        String patientData1 = getPatientData(patient1);
        String patientData3 = getPatientData(patient3);

        when(deduplicationService.comparePatients(any(Patient.class), any(Patient.class))).thenReturn(true);

        Boolean result = deduplicationController.comparePatients(new String[]{patientData1, patientData3});

        // Verify the result
        assertTrue(result);
    }

    @Test
    void testCheckPatientDuplicates() throws IOException {
        Patient patientData1 = getPatientDataToPatient(patient1);

        when(deduplicationService.checkPatientDuplicates(any(Patient.class))).thenReturn(true);

        Boolean result = deduplicationController.checkPatientDuplicates(patientData1);

        // Verify the result
        assertTrue(result);
    }

    @Test
    void testGetAllDuplicatePatientEntry() {
        String expectedResult = "{\"result\": \"some data\"}";
        when(deduplicationService.getAllDuplicatePatientRecords()).thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Object> response = deduplicationController.getAllDuplicatePatientEntry();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());
    }

    public String getPatientData(String patientPath) throws IOException {
        File file = new File("src/test/resources/mockdata/Deduplication/"+patientPath+".json");
        InputStream fileInputStream = new FileInputStream(file);
        String jsonString = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8);
        return jsonString;
    }

    public  Patient getPatientDataToPatient(String patientPath) throws IOException {
        String jsonString = getPatientData(patientPath);
        FhirContext fhirContext = FhirContext.forR4();
        IParser parser = fhirContext.newJsonParser();
        Patient patient = parser.parseResource(Patient.class, jsonString);
        return patient;
    }

}