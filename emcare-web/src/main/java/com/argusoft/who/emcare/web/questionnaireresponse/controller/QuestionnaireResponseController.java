package com.argusoft.who.emcare.web.questionnaireresponse.controller;

import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.questionnaireresponse.dto.QuestionnaireResponseRequestDto;
import com.argusoft.who.emcare.web.questionnaireresponse.model.QuestionnaireResponse;
import com.argusoft.who.emcare.web.questionnaireresponse.service.QuestionnaireResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;


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
    public ResponseEntity<Object> getQuestionnaireResponseByUserLocation(@Nullable @RequestParam(value = CommonConstant.RESOURCE_LAST_UPDATED_AT) DateParam theDate) {
        List<QuestionnaireResponse> questionnaireResponses = questionnaireResponseService.getQuestionnaireResponseByUserLocation(Objects.nonNull(theDate) ? theDate.getValue() : null);
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
