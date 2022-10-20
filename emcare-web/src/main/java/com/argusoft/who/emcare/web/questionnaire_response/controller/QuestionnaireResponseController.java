package com.argusoft.who.emcare.web.questionnaire_response.controller;

import com.argusoft.who.emcare.web.questionnaire_response.dto.QuestionnaireResponseRequestDto;
import com.argusoft.who.emcare.web.questionnaire_response.model.QuestionnaireResponse;
import com.argusoft.who.emcare.web.questionnaire_response.service.QuestionnaireResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
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

    @GetMapping("/page")
    public ResponseEntity<Object> getQuestionnaireResponsePage(@RequestParam(value = "pageNo") Integer pageNo,
                                                               @Nullable @RequestParam(value = "search", required = false) String searchString) {
        return ResponseEntity.ok().body(questionnaireResponseService.getQuestionnaireResponsePage(pageNo, searchString));
    }

    @GetMapping("/byPatient")
    public ResponseEntity<Object> getQuestionnaireResponseByPatientId(@RequestParam(value = "patientId") String patientId) {
        return ResponseEntity.ok().body(questionnaireResponseService.getQuestionnaireResponseByPatientId(patientId));
    }


}
