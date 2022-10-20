package com.argusoft.who.emcare.web.questionnaire_response.service.impl;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.EmcareResourceRepository;
import com.argusoft.who.emcare.web.fhir.dao.LocationResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.dto.PatientDto;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import com.argusoft.who.emcare.web.location.dao.LocationMasterDao;
import com.argusoft.who.emcare.web.questionnaire_response.dto.MiniPatient;
import com.argusoft.who.emcare.web.questionnaire_response.dto.QuestionnaireResponseRequestDto;
import com.argusoft.who.emcare.web.questionnaire_response.mapper.QuestionnaireResponseMapper;
import com.argusoft.who.emcare.web.questionnaire_response.model.QuestionnaireResponse;
import com.argusoft.who.emcare.web.questionnaire_response.respository.QuestionnaireResponseRepository;
import com.argusoft.who.emcare.web.questionnaire_response.service.QuestionnaireResponseService;
import com.argusoft.who.emcare.web.user.dto.UserMasterDto;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    EmcareResourceService emcareResourceService;

    @Autowired
    LocationMasterDao locationMasterDao;

    @Autowired
    LocationResourceRepository locationResourceRepository;


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
    public List<QuestionnaireResponse> getQuestionnaireResponseByUserLocation() {
        UserMasterDto userMasterDto = userService.getCurrentUser();

//        NEED TO CHANGE AFTER PILOT
//        List<Integer> locationIds = userMasterDto.getFacilities().stream().map(FacilityDto::getLocationId).collect(Collectors.toList()).stream().map(Long::intValue).collect(Collectors.toList());
//        locationIds = locationMasterDao.getAllChildLocationIdWithMultipalLocationId(locationIds);
        List<String> facilityIds = userMasterDto.getFacilities().stream().map(FacilityDto::getFacilityId).collect(Collectors.toList());
        List<EmcareResource> patientList = emcareResourceRepository.findByFacilityIdIn(facilityIds);
        List<String> patientIds = patientList.stream().map(EmcareResource::getResourceId).collect(Collectors.toList());

        return questionnaireResponseRepository.findByPatientIdIn(patientIds);
    }

    @Override
    public PageDto getQuestionnaireResponsePage(Integer pageNo, String searchString) {
        List<QuestionnaireResponse> questionnaireResponses;
        Sort sort = Sort.by("createdOn").descending();
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE);
        List<EmcareResource> resourcesList;
        Integer totalCount = 0;
        if (searchString != null && !searchString.isEmpty()) {
            resourcesList = emcareResourceRepository.findByTypeContainingAndTextContainingIgnoreCaseOrderByCreatedOnDesc(CommonConstant.FHIR_PATIENT, searchString);
        } else {
            resourcesList = emcareResourceRepository.findAllByType(CommonConstant.FHIR_PATIENT);
        }
        List<String> resourceIds = resourcesList.stream().map(EmcareResource::getResourceId).collect(Collectors.toList());
        List<MiniPatient> responseList = questionnaireResponseRepository.findDistinctByPatientIdIn(
                resourceIds,
                page);
        List<String> patientIds = responseList.stream().map(MiniPatient::getPatientId).collect(Collectors.toList());
        totalCount = questionnaireResponseRepository.findDistinctByPatientIdIn(resourceIds).size();
        List<PatientDto> patientList = emcareResourceService.getPatientDtoByIds(patientIds);
        PageDto pageDto = new PageDto();
        pageDto.setList(patientList);
        pageDto.setTotalCount(totalCount.longValue());
        return pageDto;
    }

    @Override
    public Map<String, List<QuestionnaireResponse>> getQuestionnaireResponseByPatientId(String patientId) {
        List<QuestionnaireResponse> questionnaireResponses = questionnaireResponseRepository.findByPatientId(patientId);
        List<List<QuestionnaireResponse>> encounterList;

        Map<String, List<QuestionnaireResponse>> byEncounter =
                questionnaireResponses.stream().collect(Collectors.groupingBy(QuestionnaireResponse::getEncounterId));

        return byEncounter;
    }
}
