package com.argusoft.who.emcare.web.questionnaire_response.service.impl;

import com.argusoft.who.emcare.web.fhir.dao.EmcareResourceRepository;
import com.argusoft.who.emcare.web.fhir.dao.LocationResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import com.argusoft.who.emcare.web.location.dao.LocationMasterDao;
import com.argusoft.who.emcare.web.questionnaire_response.dto.QuestionnaireResponseRequestDto;
import com.argusoft.who.emcare.web.questionnaire_response.mapper.QuestionnaireResponseMapper;
import com.argusoft.who.emcare.web.questionnaire_response.model.QuestionnaireResponse;
import com.argusoft.who.emcare.web.questionnaire_response.respository.QuestionnaireResponseRepository;
import com.argusoft.who.emcare.web.questionnaire_response.service.QuestionnaireResponseService;
import com.argusoft.who.emcare.web.user.dto.UserMasterDto;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuestionnaireResponseServiceImpl implements QuestionnaireResponseService {

    @Autowired
    QuestionnaireResponseRepository questionnaireResponseRepository;

    @Autowired
    UserService userService;

    @Autowired
    EmcareResourceRepository emcareResourceRepository;

    @Autowired
    LocationMasterDao locationMasterDao;

    @Autowired
    LocationResourceRepository locationResourceRepository;

    @Override
    public QuestionnaireResponse saveOrUpdateQuestionnaireResponse(QuestionnaireResponseRequestDto questionnaireResponseRequestDto) {

        QuestionnaireResponse questionnaireResponse = QuestionnaireResponseMapper.getQuestionnaireResponse(questionnaireResponseRequestDto);
        if (questionnaireResponse.getId() == null) {
            String id = UUID.randomUUID().toString();
            questionnaireResponse.setId(id);
        }
        questionnaireResponse = questionnaireResponseRepository.save(questionnaireResponse);
        return questionnaireResponse;
    }

    @Override
    public List<QuestionnaireResponse> getQuestionnaireResponseByUserLocation() {
        UserMasterDto userMasterDto = userService.getCurrentUser();
        List<Integer> locationIds = userMasterDto.getFacilities().stream().map(FacilityDto::getLocationId).collect(Collectors.toList()).stream().map(Long::intValue).collect(Collectors.toList());

        locationIds = locationMasterDao.getAllChildLocationIdWithMultipalLocationId(locationIds);
        List<String> facilityIds = locationResourceRepository.findResourceIdIn(locationIds);
        List<EmcareResource> patientList = emcareResourceRepository.findByFacilityIdIn(facilityIds);
        List<String> patientIds = patientList.stream().map(EmcareResource::getResourceId).collect(Collectors.toList());

        return questionnaireResponseRepository.findByPatientIdIn(patientIds);
    }
}
