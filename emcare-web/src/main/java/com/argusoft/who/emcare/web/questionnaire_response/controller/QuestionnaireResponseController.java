package com.argusoft.who.emcare.web.questionnaire_response.controller;

import com.argusoft.who.emcare.web.questionnaire_response.dto.QuestionnaireResponseRequestDto;
import com.argusoft.who.emcare.web.questionnaire_response.model.QuestionnaireResponse;
import com.argusoft.who.emcare.web.questionnaire_response.service.QuestionnaireResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api/questionnaire_response")
public class QuestionnaireResponseController {

    @Autowired
    QuestionnaireResponseService questionnaireResponseService;


    @PostMapping("/createOrUpdate")
    public ResponseEntity<Object> addOrUpdateQuestionnaireResponse(@RequestBody List<QuestionnaireResponseRequestDto> questionnaireResponseRequestDto) {
        List<QuestionnaireResponse> questionnaireResponse = questionnaireResponseService.saveOrUpdateQuestionnaireResponse(questionnaireResponseRequestDto);
        return ResponseEntity.ok().body(questionnaireResponse);
    }

    @GetMapping("/fetch/all")
    public ResponseEntity<Object> getQuestionnaireResponseByUserLocation() {
        List<QuestionnaireResponse> questionnaireResponses = questionnaireResponseService.getQuestionnaireResponseByUserLocation();
        return ResponseEntity.ok().body(questionnaireResponses);
    }
}
