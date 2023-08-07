package com.argusoft.who.emcare.web.questionnaireresponse.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class QuestionnaireResponseRequestDtoTest {

    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testGettersAndSetters() {
        QuestionnaireResponseRequestDto requestDto = new QuestionnaireResponseRequestDto();

        requestDto.setId("CAD8693d-7d6b-4911");
        requestDto.setQuestionnaireResponseText("Response to Health Questionnaire");
        requestDto.setPatientId("e5e8e435-e45a-4cb5");
        requestDto.setConsultationStage("MEASUREMENTS");
        requestDto.setIsActive(true);
        requestDto.setEncounterId("1b651e7e-6219-4202");
        requestDto.setStructureMapId("emcare.b12.measurements");
        requestDto.setQuestionnaireId("emcare.b12.measurements");

        Date consultationDate = new Date();
        requestDto.setConsultationDate(consultationDate);

        assertEquals("CAD8693d-7d6b-4911", requestDto.getId());
        assertEquals("Response to Health Questionnaire", requestDto.getQuestionnaireResponseText());
        assertEquals("e5e8e435-e45a-4cb5", requestDto.getPatientId());
        assertEquals("MEASUREMENTS", requestDto.getConsultationStage());
        assertEquals(true, requestDto.getIsActive());
        assertEquals("1b651e7e-6219-4202", requestDto.getEncounterId());
        assertEquals("emcare.b12.measurements", requestDto.getStructureMapId());
        assertEquals("emcare.b12.measurements", requestDto.getQuestionnaireId());
        assertEquals(consultationDate, requestDto.getConsultationDate());
    }

    @Test
    void testDefaultValues() {
        QuestionnaireResponseRequestDto requestDto = new QuestionnaireResponseRequestDto();

        assertNull(requestDto.getId());
        assertNull(requestDto.getQuestionnaireResponseText());
        assertNull(requestDto.getPatientId());
        assertNull(requestDto.getConsultationStage());
        assertFalse(requestDto.getIsActive());
        assertNull(requestDto.getEncounterId());
        assertNull(requestDto.getStructureMapId());
        assertNull(requestDto.getQuestionnaireId());
        assertNull(requestDto.getConsultationDate());
    }
}