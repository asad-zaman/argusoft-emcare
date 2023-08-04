package com.argusoft.who.emcare.web.questionnaireresponse.service;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.EmcareResourceRepository;
import com.argusoft.who.emcare.web.fhir.dao.EncounterResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import com.argusoft.who.emcare.web.fhir.model.EncounterResource;
import com.argusoft.who.emcare.web.questionnaireresponse.model.QuestionnaireResponse;
import com.argusoft.who.emcare.web.questionnaireresponse.respository.QuestionnaireResponseRepository;
import com.argusoft.who.emcare.web.questionnaireresponse.service.impl.QuestionnaireResponseServiceImpl;
import com.argusoft.who.emcare.web.user.dto.UserMasterDto;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {QuestionnaireResponseService.class})
@RunWith(SpringJUnit4ClassRunner.class)
class QuestionnaireResponseServiceTest {

    @Mock
    private EmcareResourceRepository emcareResourceRepository;

    @Mock
    private QuestionnaireResponseRepository questionnaireResponseRepository;

    @Mock
    private UserService userService;

    @Mock
    private EncounterResourceRepository encounterResourceRepository;

    @InjectMocks
    private QuestionnaireResponseServiceImpl questionnaireResponseService;

    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void testGetQuestionnaireResponseByUserLocation() throws ParseException {

        Date theDate = new Date();
        String prodDateString = "31/05/2023";
        Date prodDate = new SimpleDateFormat("dd/MM/yyyy").parse(prodDateString);

        List<QuestionnaireResponse> questionnaireResponseList = new ArrayList<>();


//        Date prodDate = new Date();
//        try {
//            String prodDateString = "31/05/2023";
//            prodDate = new SimpleDateFormat("dd/MM/yyyy").parse(prodDateString);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        UserMasterDto userMasterDto = (UserMasterDto) userService.getCurrentUser().getBody();
//        List<String> facilityIds = userMasterDto.getFacilities().stream().map(FacilityDto::getFacilityId).collect(Collectors.toList());
//        List<EmcareResource> patientList = emcareResourceRepository.findByFacilityIdInAndCreatedOnGreaterThan(facilityIds, prodDate);
//        List<String> patientIds = patientList.stream().map(EmcareResource::getResourceId).collect(Collectors.toList());
//        List<QuestionnaireResponse> questionnaireResponses;
//        if (Objects.nonNull(theDate)) {
//            questionnaireResponses = questionnaireResponseRepository.findByPatientIdInAndConsultationDateGreaterThan(patientIds, theDate);
//        } else {
//            questionnaireResponses = questionnaireResponseRepository.findByPatientIdIn(patientIds);
//        }
//        return questionnaireResponses;
    }

    @Test
    public void testGetQuestionnaireResponsePageWithSearchString() {
        int pageNo = 0;
        String searchString = "test";
        List<Map<String, Object>> consultations = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("name1","test1");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("name2","test2");

        consultations.add(map1);
        consultations.add(map2);

        int totalCount = 2;

        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE);
        when(emcareResourceRepository.findConsultationsBySearch(searchString, page)).thenReturn(consultations);
        when(emcareResourceRepository.findConsultationsBySearchCount(searchString)).thenReturn(consultations);

        PageDto result = questionnaireResponseService.getQuestionnaireResponsePage(pageNo, searchString);

        assertEquals(consultations, result.getList());
        assertEquals(totalCount, result.getTotalCount());
    }

    @Test
    public void testGetQuestionnaireResponsePageWithoutSearchString() {
        Integer pageNo = 0;
        String searchString = null;
        List<Map<String, Object>> consultations = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("name1","name");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("name2","name");

        consultations.add(map1);
        consultations.add(map2);

        int totalCount = 2;

        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE);
        when(emcareResourceRepository.findAllConsultations(page)).thenReturn(consultations);
        when(emcareResourceRepository.findAllConsultationsCount()).thenReturn(consultations);

        PageDto result = questionnaireResponseService.getQuestionnaireResponsePage(pageNo, searchString);

        assertEquals(consultations, result.getList());
        assertEquals(totalCount, result.getTotalCount());
    }

    @Test
    public void testGetQuestionnaireResponseByPatientId() {
        String patientId = "1";

        List<QuestionnaireResponse> questionnaireResponses = new ArrayList<>();
        QuestionnaireResponse questionnaireResponse1 = new QuestionnaireResponse();
        questionnaireResponse1.setPatientId(patientId);
        questionnaireResponse1.setEncounterId("1");
        questionnaireResponses.add(questionnaireResponse1);

        when(questionnaireResponseRepository.findByPatientId(patientId)).thenReturn(questionnaireResponses);


        String encounterId1 = "1";
        EncounterResource encounterResource1 = new EncounterResource();
        encounterResource1.setId(1L);
        encounterResource1.setCreatedOn(new Date());


        when(encounterResourceRepository.findByResourceId(encounterId1)).thenReturn(encounterResource1);

        Map<String, Object> result = questionnaireResponseService.getQuestionnaireResponseByPatientId(patientId);

        assertNotNull(result);
        assertEquals(1, result.size());

        for (Map.Entry<String, Object> entry : result.entrySet()) {
            assertTrue(entry.getValue() instanceof List);
            assertNotNull(entry.getKey());
        }
    }

    @Test
    void getDataForExport() {
    }

    @Test
    void getAllDataForExport() {
    }

    @Test
    void getConsultationsUnderLocationId() {
    }

    @Test
    void logSyncAttempt() {
    }
}