package com.argusoft.who.emcare.web.questionnaireresponse.service;

import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.questionnaireresponse.dto.QuestionnaireResponseRequestDto;
import com.argusoft.who.emcare.web.questionnaireresponse.model.QuestionnaireResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface QuestionnaireResponseService {

    public List<QuestionnaireResponse> saveOrUpdateQuestionnaireResponse(List<QuestionnaireResponseRequestDto> questionnaireResponseRequestDto);

    public List<QuestionnaireResponse> getQuestionnaireResponseByUserLocation(Date theDate);

    public PageDto getQuestionnaireResponsePage(Integer pageNo, String searchString);

    public Map<String, Object> getQuestionnaireResponseByPatientId(String patientId);

    public List<String> getDataForExport(String patientId);

    public Map<String, Object> getAllDataForExport();

    public void logSyncAttempt();
}
