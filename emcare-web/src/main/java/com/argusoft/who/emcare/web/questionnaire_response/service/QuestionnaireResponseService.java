package com.argusoft.who.emcare.web.questionnaire_response.service;

import com.argusoft.who.emcare.web.questionnaire_response.dto.QuestionnaireResponseRequestDto;
import com.argusoft.who.emcare.web.questionnaire_response.model.QuestionnaireResponse;

import java.util.List;

public interface QuestionnaireResponseService {

    public QuestionnaireResponse saveOrUpdateQuestionnaireResponse(QuestionnaireResponseRequestDto questionnaireResponseRequestDto);

    public List<QuestionnaireResponse> getQuestionnaireResponseByUserLocation();
}
