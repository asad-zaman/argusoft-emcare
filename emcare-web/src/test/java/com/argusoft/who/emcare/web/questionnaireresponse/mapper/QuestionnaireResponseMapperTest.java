package com.argusoft.who.emcare.web.questionnaireresponse.mapper;

import com.argusoft.who.emcare.web.questionnaireresponse.dto.QuestionnaireResponseRequestDto;
import com.argusoft.who.emcare.web.questionnaireresponse.model.QuestionnaireResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class QuestionnaireResponseMapperTest {

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
    void getQuestionnaireResponse() {

        QuestionnaireResponseRequestDto requestDto = new QuestionnaireResponseRequestDto();

        LocalDate localDate = LocalDate.of(2023, 7, 15);
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        requestDto.setId("BD7CDAE4-55EQ6-44F3");
        requestDto.setQuestionnaireResponseText("Response to Health Questionnaire");
        requestDto.setPatientId("45d2as-8f8a-4e2233");
        requestDto.setConsultationStage("SYMPTOMS");
        requestDto.setIsActive(true);
        requestDto.setEncounterId("FC12451-49c5-9d70");
        requestDto.setStructureMapId("emcare.b18-21.measurements.2m");
        requestDto.setQuestionnaireId("emcare.b18-21.measurements.2m");
        QuestionnaireResponse response = QuestionnaireResponseMapper.getQuestionnaireResponse(requestDto);

        assertEquals(requestDto.getId(), response.getId());
        assertEquals(requestDto.getQuestionnaireId(), response.getQuestionnaireId());
        assertEquals(requestDto.getQuestionnaireResponseText(), response.getQuestionnaireResponseText());
        assertEquals(requestDto.getIsActive(), response.getIsActive());
        assertEquals(requestDto.getEncounterId(), response.getEncounterId());
        assertEquals(requestDto.getConsultationStage(), response.getConsultationStage());
        assertEquals(requestDto.getStructureMapId(), response.getStructureMapId());
        assertEquals(requestDto.getPatientId(), response.getPatientId());
        assertEquals(requestDto.getConsultationDate(), response.getConsultationDate());
    }
}