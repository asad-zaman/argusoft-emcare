package com.argusoft.who.emcare.web.questionnaire_response.mapper;

import com.argusoft.who.emcare.web.questionnaire_response.dto.QuestionnaireResponseRequestDto;
import com.argusoft.who.emcare.web.questionnaire_response.model.QuestionnaireResponse;

public class QuestionnaireResponseMapper {

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
        return response;
    }
}
