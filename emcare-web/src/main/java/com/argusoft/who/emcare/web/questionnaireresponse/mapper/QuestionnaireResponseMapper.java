package com.argusoft.who.emcare.web.questionnaireresponse.mapper;

import com.argusoft.who.emcare.web.questionnaireresponse.dto.QuestionnaireResponseRequestDto;
import com.argusoft.who.emcare.web.questionnaireresponse.model.QuestionnaireResponse;

public class QuestionnaireResponseMapper {

    private QuestionnaireResponseMapper() {
    }

    public static QuestionnaireResponse getQuestionnaireResponse(QuestionnaireResponseRequestDto responseRequestDto) {
        QuestionnaireResponse response = new QuestionnaireResponse();
        response.setId(responseRequestDto.getId());
        response.setQuestionnaireId(responseRequestDto.getQuestionnaireId());
        response.setQuestionnaireResponseText(responseRequestDto.getQuestionnaireResponseText());
        response.setIsActive(responseRequestDto.getIsActive());
        response.setEncounterId(responseRequestDto.getEncounterId());
        response.setConsultationStage(responseRequestDto.getConsultationStage());
        response.setQuestionnaireId(responseRequestDto.getQuestionnaireId());
        response.setStructureMapId(responseRequestDto.getStructureMapId());
        response.setPatientId(responseRequestDto.getPatientId());
        response.setConsultationDate(responseRequestDto.getConsultationDate());
        return response;
    }
}
