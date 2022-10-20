package com.argusoft.who.emcare.web.questionnaire_response.service;

import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.questionnaire_response.dto.QuestionnaireResponseRequestDto;
import com.argusoft.who.emcare.web.questionnaire_response.model.QuestionnaireResponse;

import java.util.List;
import java.util.Map;

public interface QuestionnaireResponseService {

    public List<QuestionnaireResponse> saveOrUpdateQuestionnaireResponse(List<QuestionnaireResponseRequestDto> questionnaireResponseRequestDto);

    public List<QuestionnaireResponse> getQuestionnaireResponseByUserLocation();

    public PageDto getQuestionnaireResponsePage(Integer pageNo, String searchString);

    public Map<String, List<QuestionnaireResponse>> getQuestionnaireResponseByPatientId(String patientId);
}
