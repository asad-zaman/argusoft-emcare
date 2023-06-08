package com.argusoft.who.emcare.web.questionnaireresponse.service.impl;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.EmcareResourceRepository;
import com.argusoft.who.emcare.web.fhir.dao.EncounterResourceRepository;
import com.argusoft.who.emcare.web.fhir.dao.LocationResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import com.argusoft.who.emcare.web.fhir.model.EncounterResource;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import com.argusoft.who.emcare.web.location.dao.LocationMasterDao;
import com.argusoft.who.emcare.web.questionnaireresponse.dto.QuestionnaireResponseRequestDto;
import com.argusoft.who.emcare.web.questionnaireresponse.mapper.QuestionnaireResponseMapper;
import com.argusoft.who.emcare.web.questionnaireresponse.model.QuestionnaireResponse;
import com.argusoft.who.emcare.web.questionnaireresponse.respository.QuestionnaireResponseRepository;
import com.argusoft.who.emcare.web.questionnaireresponse.service.QuestionnaireResponseService;
import com.argusoft.who.emcare.web.user.dto.UserMasterDto;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
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
    EmcareResourceService emcareResourceService;

    @Autowired
    LocationMasterDao locationMasterDao;

    @Autowired
    LocationResourceRepository locationResourceRepository;

    @Autowired
    EncounterResourceRepository encounterResourceRepository;


    @Override
    public List<QuestionnaireResponse> saveOrUpdateQuestionnaireResponse(List<QuestionnaireResponseRequestDto> questionnaireResponseRequestDto) {
        List<QuestionnaireResponse> questionnaireResponses = new ArrayList<>();
        for (QuestionnaireResponseRequestDto responseRequestDto : questionnaireResponseRequestDto) {
            QuestionnaireResponse questionnaireResponse = QuestionnaireResponseMapper.getQuestionnaireResponse(responseRequestDto);
            if (questionnaireResponse.getId() == null) {
                String id = UUID.randomUUID().toString();
                questionnaireResponse.setId(id);
            }
            questionnaireResponse = questionnaireResponseRepository.save(questionnaireResponse);
            questionnaireResponses.add(questionnaireResponse);
        }

        return questionnaireResponses;
    }

    @Override
    public List<QuestionnaireResponse> getQuestionnaireResponseByUserLocation(Date theDate) {
        UserMasterDto userMasterDto = (UserMasterDto) userService.getCurrentUser().getBody();
        List<String> facilityIds = userMasterDto.getFacilities().stream().map(FacilityDto::getFacilityId).collect(Collectors.toList());
        List<EmcareResource> patientList = emcareResourceRepository.findByFacilityIdIn(facilityIds);
        List<String> patientIds = patientList.stream().map(EmcareResource::getResourceId).collect(Collectors.toList());
        List<QuestionnaireResponse> questionnaireResponses;
        if (Objects.nonNull(theDate)) {
            questionnaireResponses = questionnaireResponseRepository.findByPatientIdInAndConsultationDateGreaterThan(patientIds, theDate);
        } else {
            questionnaireResponses = questionnaireResponseRepository.findByPatientIdIn(patientIds);
        }
        return questionnaireResponses;
    }

    @Override
    public PageDto getQuestionnaireResponsePage(Integer pageNo, String searchString) {
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE);
//        List<EmcareResource> resourcesList;
        List<Map<String,Object>> consultations;
        Integer totalCount = 0;
        PageDto pageDto = new PageDto();

        if (searchString != null && !searchString.isEmpty()) {
            consultations = emcareResourceRepository.findConsultationsBySearch(searchString);
        } else {
            consultations = emcareResourceRepository.findAllConsultations();
        }

        pageDto.setList(consultations);
        pageDto.setTotalCount(totalCount.longValue());
        return pageDto;
    }

    @Override
    public Map<String, Object> getQuestionnaireResponseByPatientId(String patientId) {
        List<QuestionnaireResponse> questionnaireResponses = questionnaireResponseRepository.findByPatientId(patientId);
        Map<String, List<QuestionnaireResponse>> responses;
        responses = questionnaireResponses.stream().collect(Collectors.groupingBy(QuestionnaireResponse::getEncounterId));
        Map<String, Object> responsesWithEncounter = new HashMap<>();
        for (Map.Entry<String, List<QuestionnaireResponse>> key : responses.entrySet()) {
            EncounterResource encounterResource = encounterResourceRepository.findByResourceId(key.getKey());
            responsesWithEncounter.put(encounterResource.getCreatedOn().toString(), responses.get(key.getKey()));
        }
        return responsesWithEncounter;
    }

    @Override
    public List<String> getDataForExport(String patientId) {
        List<QuestionnaireResponse> questionnaireResponses = questionnaireResponseRepository.findByPatientId(patientId);
        return questionnaireResponses.stream().map(QuestionnaireResponse::getQuestionnaireResponseText).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getAllDataForExport() {
        List<String> patients = questionnaireResponseRepository.findDistinctPatientIdd();
        Map<String, Object> map = new HashMap<>();
        for (String patient : patients) {
            map.put(patient, questionnaireResponseRepository.findByPatientId(patient).stream().map(QuestionnaireResponse::getQuestionnaireResponseText).collect(Collectors.toList()));
        }
        return map;
    }

    @Override
    public void logSyncAttempt() {
        UserMasterDto userMasterDto = (UserMasterDto) userService.getCurrentUser().getBody();
        questionnaireResponseRepository.logSyncAttempt(userMasterDto.getUserId());
    }
}
