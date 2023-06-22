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
import com.argusoft.who.emcare.web.questionnaireresponse.model.UserSyncLog;
import com.argusoft.who.emcare.web.questionnaireresponse.respository.QuestionnaireResponseRepository;
import com.argusoft.who.emcare.web.questionnaireresponse.respository.UserSyncLogRepository;
import com.argusoft.who.emcare.web.questionnaireresponse.service.QuestionnaireResponseService;
import com.argusoft.who.emcare.web.user.dto.UserMasterDto;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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

    @Autowired
    UserSyncLogRepository userSyncLogRepository;


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
        Date prodDate = new Date();
        try {
            String prodDateString = "31/05/2023";
            prodDate = new SimpleDateFormat("dd/MM/yyyy").parse(prodDateString);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        UserMasterDto userMasterDto = (UserMasterDto) userService.getCurrentUser().getBody();
        List<String> facilityIds = userMasterDto.getFacilities().stream().map(FacilityDto::getFacilityId).collect(Collectors.toList());
        List<EmcareResource> patientList = emcareResourceRepository.findByFacilityIdInAndCreatedOnGreaterThan(facilityIds, prodDate);
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
        List<Map<String, Object>> consultations;
        Integer totalCount = 0;
        PageDto pageDto = new PageDto();

        if (searchString != null && !searchString.isEmpty()) {
            consultations = emcareResourceRepository.findConsultationsBySearch(searchString, page);
            totalCount = emcareResourceRepository.findConsultationsBySearchCount(searchString).size();
        } else {
            consultations = emcareResourceRepository.findAllConsultations(page);
            totalCount = emcareResourceRepository.findAllConsultationsCount().size();
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
    public PageDto getConsultationsUnderLocationId(Object locationId, Integer pageNo, String sDate, String eDate, String searchString) {
        Long offSet = pageNo.longValue() * 10;
        List<Integer> locationIds;
        List<String> childFacilityIds = new ArrayList<>();
        if (Objects.nonNull(locationId)) {
            if (isNumeric(locationId.toString())) {
                locationIds = locationMasterDao.getAllChildLocationId(Integer.parseInt(locationId.toString()));
                childFacilityIds = locationResourceRepository.findResourceIdIn(locationIds);
            } else {
                childFacilityIds.add(locationId.toString());
            }
        }

        Date startDate = null;
        Date endDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (Objects.isNull(sDate) || sDate.isEmpty()) {
                String sDate1 = "1998-12-31";
                sDate = sdf.format(sdf.parse(sDate1));
            }
            if (Objects.isNull(eDate) || eDate.isEmpty()) {
                eDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            startDate = simpleDateFormat.parse(sDate);
            endDate = simpleDateFormat.parse(eDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Long totalCount = 0L;
        List<Map<String, Object>> resourcesList = new ArrayList<>();
        if (locationId.toString().isEmpty()) {
            if (searchString != null && !searchString.isEmpty()) {
                totalCount = Long.valueOf(questionnaireResponseRepository.getFilteredDateWithSearchCount(searchString, startDate, endDate).size());
                resourcesList = questionnaireResponseRepository.getFilteredDateWithSearch(searchString, startDate, endDate, offSet);
            } else {
                totalCount = Long.valueOf(questionnaireResponseRepository.getFilteredDateOnlyCount(startDate, endDate).size());
                resourcesList = questionnaireResponseRepository.getFilteredDateOnly(startDate, endDate, offSet);
            }
        }
        else {
            if (searchString != null && !searchString.isEmpty()) {
                totalCount = Long.valueOf(questionnaireResponseRepository.getFilteredConsultationWithSearchCount(searchString, childFacilityIds, startDate, endDate).size());
                resourcesList = questionnaireResponseRepository.getFilteredConsultationWithSearch(searchString, childFacilityIds, startDate, endDate, offSet);
            }
            else{
                totalCount = Long.valueOf(questionnaireResponseRepository.getFilteredConsultationsInCount( childFacilityIds, startDate, endDate).size());
                resourcesList = questionnaireResponseRepository.getFilteredConsultationsIn(childFacilityIds, startDate, endDate, offSet);
            }
        }
        PageDto pageDto = new PageDto();
        pageDto.setList(resourcesList);
        pageDto.setTotalCount(totalCount);
        return pageDto;
    }

    @Override
    public void logSyncAttempt() {
        UserMasterDto userMasterDto = (UserMasterDto) userService.getCurrentUser().getBody();
        UserSyncLog userSyncLog = new UserSyncLog();
        userSyncLog.setSyncAttemptTime(new Date());
        userSyncLog.setUsername(userMasterDto.getUserName());
        userSyncLogRepository.save(userSyncLog);
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


}
