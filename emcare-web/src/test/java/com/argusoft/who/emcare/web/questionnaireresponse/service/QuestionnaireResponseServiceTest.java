package com.argusoft.who.emcare.web.questionnaireresponse.service;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.EmcareResourceRepository;
import com.argusoft.who.emcare.web.fhir.dao.EncounterResourceRepository;
import com.argusoft.who.emcare.web.fhir.dao.LocationResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import com.argusoft.who.emcare.web.fhir.model.EncounterResource;
import com.argusoft.who.emcare.web.location.dao.LocationMasterDao;
import com.argusoft.who.emcare.web.questionnaireresponse.dto.QuestionnaireResponseRequestDto;
import com.argusoft.who.emcare.web.questionnaireresponse.model.QuestionnaireResponse;
import com.argusoft.who.emcare.web.questionnaireresponse.model.UserSyncLog;
import com.argusoft.who.emcare.web.questionnaireresponse.respository.QuestionnaireResponseRepository;
import com.argusoft.who.emcare.web.questionnaireresponse.respository.UserSyncLogRepository;
import com.argusoft.who.emcare.web.questionnaireresponse.service.impl.QuestionnaireResponseServiceImpl;
import com.argusoft.who.emcare.web.user.dto.UserMasterDto;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
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

    @Mock
    private LocationMasterDao locationMasterDao;

    @Mock
    private LocationResourceRepository locationResourceRepository;

    @Mock
    private UserSyncLogRepository userSyncLogRepository;

    @InjectMocks
    private QuestionnaireResponseServiceImpl questionnaireResponseService;


    AutoCloseable autoCloseable;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
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
    public void testSaveOrUpdateQuestionnaireResponse() {
        // test data
        List<QuestionnaireResponseRequestDto> requestDtoList = new ArrayList<>();
        QuestionnaireResponseRequestDto requestDto1 = new QuestionnaireResponseRequestDto();
        requestDto1.setQuestionnaireResponseText("Response 1");
        QuestionnaireResponseRequestDto requestDto2 = new QuestionnaireResponseRequestDto();
        requestDto2.setQuestionnaireResponseText("Response 2");
        requestDtoList.add(requestDto1);
        requestDtoList.add(requestDto2);

        //expected behavior of the mock repository
        QuestionnaireResponse savedResponse1 = new QuestionnaireResponse();
        savedResponse1.setId(UUID.randomUUID().toString());
        savedResponse1.setQuestionnaireResponseText("Response 1"); // Set the expected response text for the first DTO
        QuestionnaireResponse savedResponse2 = new QuestionnaireResponse();
        savedResponse2.setId(UUID.randomUUID().toString());
        savedResponse2.setQuestionnaireResponseText("Response 2"); // Set the expected response text for the second DTO
        when(questionnaireResponseRepository.save(any(QuestionnaireResponse.class))).thenReturn(savedResponse1, savedResponse2);

        List<QuestionnaireResponse> result = questionnaireResponseService.saveOrUpdateQuestionnaireResponse(requestDtoList);

        verify(questionnaireResponseRepository, times(2)).save(any(QuestionnaireResponse.class));
        assertEquals(requestDtoList.size(), result.size());
        assertNotNull(result.get(0).getId());
        assertNotNull(result.get(1).getId());
        assertEquals("Response 1", result.get(0).getQuestionnaireResponseText());
        assertEquals("Response 2", result.get(1).getQuestionnaireResponseText());
    }

    @Test
    void getQuestionnaireResponseByUserLocationWithLaterDate() throws ParseException {
        UserMasterDto userMasterDto = new UserMasterDto();
        userMasterDto.setFacilities(getMockFacilities());
        when(userService.getCurrentUser()).thenReturn(ResponseEntity.ok().body(userMasterDto));
        when(
                emcareResourceRepository.findByFacilityIdInAndCreatedOnGreaterThan(anyList(), any(Date.class))
        ).thenAnswer(i -> getMockEmcareResourceByFacilityIdInAndCreatedOnGreaterThan(i.getArgument(0), i.getArgument(1)));

        when(
                questionnaireResponseRepository.findByPatientIdInAndConsultationDateGreaterThan(anyList(), any(Date.class))
        ).thenAnswer(i -> getMockQuestionnaireResponse(i.getArgument(0), i.getArgument(1)));

        when(questionnaireResponseRepository.findByPatientIdIn(anyList())).thenAnswer(i -> getMockQuestionnaireResponse(i.getArgument(0), null));

        List<QuestionnaireResponse> actualQuestionnaireResponse = questionnaireResponseService.getQuestionnaireResponseByUserLocation(new SimpleDateFormat("dd/MM/yyyy").parse("01/09/2023"));

        assertNotNull(actualQuestionnaireResponse);
        assertEquals(0, actualQuestionnaireResponse.size());
    }

    @Test
    void getQuestionnaireResponseByUserLocationWithMidDate() throws ParseException {
        UserMasterDto userMasterDto = new UserMasterDto();
        userMasterDto.setFacilities(getMockFacilities());
        when(userService.getCurrentUser()).thenReturn(ResponseEntity.ok().body(userMasterDto));
        when(
                emcareResourceRepository.findByFacilityIdInAndCreatedOnGreaterThan(anyList(), any(Date.class))
        ).thenAnswer(i -> getMockEmcareResourceByFacilityIdInAndCreatedOnGreaterThan(i.getArgument(0), i.getArgument(1)));

        when(
                questionnaireResponseRepository.findByPatientIdInAndConsultationDateGreaterThan(anyList(), any(Date.class))
        ).thenAnswer(i -> getMockQuestionnaireResponse(i.getArgument(0), i.getArgument(1)));

        when(questionnaireResponseRepository.findByPatientIdIn(anyList())).thenAnswer(i -> getMockQuestionnaireResponse(i.getArgument(0), null));

        List<QuestionnaireResponse> actualQuestionnaireResponse = questionnaireResponseService.getQuestionnaireResponseByUserLocation(new SimpleDateFormat("dd/MM/yyyy").parse("02/08/2023"));

        assertNotNull(actualQuestionnaireResponse);
        assertEquals(2, actualQuestionnaireResponse.size());
    }

    @Test
    void getQuestionnaireResponseByUserLocationWithEarlyDate() throws ParseException {
        UserMasterDto userMasterDto = new UserMasterDto();
        userMasterDto.setFacilities(getMockFacilities());
        when(userService.getCurrentUser()).thenReturn(ResponseEntity.ok().body(userMasterDto));
        when(
                emcareResourceRepository.findByFacilityIdInAndCreatedOnGreaterThan(anyList(), any(Date.class))
        ).thenAnswer(i -> getMockEmcareResourceByFacilityIdInAndCreatedOnGreaterThan(i.getArgument(0), i.getArgument(1)));

        when(
                questionnaireResponseRepository.findByPatientIdInAndConsultationDateGreaterThan(anyList(), any(Date.class))
        ).thenAnswer(i -> getMockQuestionnaireResponse(i.getArgument(0), i.getArgument(1)));

        when(questionnaireResponseRepository.findByPatientIdIn(anyList())).thenAnswer(i -> getMockQuestionnaireResponse(i.getArgument(0), null));

        List<QuestionnaireResponse> actualQuestionnaireResponse = questionnaireResponseService.getQuestionnaireResponseByUserLocation(new SimpleDateFormat("dd/MM/yyyy").parse("01/07/2023"));

        assertNotNull(actualQuestionnaireResponse);
        assertEquals(4, actualQuestionnaireResponse.size());
    }

    @Test
    void getQuestionnaireResponseByUserLocationWithoutDate() throws ParseException {
        UserMasterDto userMasterDto = new UserMasterDto();
        userMasterDto.setFacilities(getMockFacilities());
        when(userService.getCurrentUser()).thenReturn(ResponseEntity.ok().body(userMasterDto));
        when(
                emcareResourceRepository.findByFacilityIdInAndCreatedOnGreaterThan(anyList(), any(Date.class))
        ).thenAnswer(i -> getMockEmcareResourceByFacilityIdInAndCreatedOnGreaterThan(i.getArgument(0), i.getArgument(1)));

        when(
                questionnaireResponseRepository.findByPatientIdInAndConsultationDateGreaterThan(anyList(), any(Date.class))
        ).thenAnswer(i -> getMockQuestionnaireResponse(i.getArgument(0), i.getArgument(1)));

        when(questionnaireResponseRepository.findByPatientIdIn(anyList())).thenAnswer(i -> getMockQuestionnaireResponse(i.getArgument(0), null));

        List<QuestionnaireResponse> actualQuestionnaireResponse = questionnaireResponseService.getQuestionnaireResponseByUserLocation(null);

        assertNotNull(actualQuestionnaireResponse);
        assertEquals(4, actualQuestionnaireResponse.size());
    }

    @Test
    public void testSaveOrUpdateQuestionnaireResponse_WithExistingId() {
        // test data with an existing ID
        List<QuestionnaireResponseRequestDto> requestDtoList = new ArrayList<>();
        QuestionnaireResponseRequestDto requestDto1 = new QuestionnaireResponseRequestDto();
        String existingId = UUID.randomUUID().toString();
        requestDto1.setId(existingId);
        requestDto1.setQuestionnaireResponseText("Response with Existing ID");
        requestDtoList.add(requestDto1);

        //expected behavior of the mock repository
        QuestionnaireResponse savedResponse1 = new QuestionnaireResponse();
        savedResponse1.setId(existingId); // Set the same ID as in the DTO
        savedResponse1.setQuestionnaireResponseText("Response with Existing ID"); // Set the expected response text
        when(questionnaireResponseRepository.save(any(QuestionnaireResponse.class))).thenReturn(savedResponse1);

        List<QuestionnaireResponse> result = questionnaireResponseService.saveOrUpdateQuestionnaireResponse(requestDtoList);

        verify(questionnaireResponseRepository, times(1)).save(any(QuestionnaireResponse.class));
        assertEquals(1, result.size());
        assertEquals(existingId, result.get(0).getId());
        assertEquals("Response with Existing ID", result.get(0).getQuestionnaireResponseText());
    }

    @Test
    public void testSaveOrUpdateQuestionnaireResponse_WithNullValues() {
        //test data with null values
        List<QuestionnaireResponseRequestDto> requestDtoList = new ArrayList<>();
        QuestionnaireResponseRequestDto requestDto1 = new QuestionnaireResponseRequestDto();

        List<QuestionnaireResponse> result = questionnaireResponseService.saveOrUpdateQuestionnaireResponse(requestDtoList);

        verify(questionnaireResponseRepository, times(0)).save(any(QuestionnaireResponse.class));
        assertEquals(0, result.size());
    }

    @Test
    void testGetDataForExport() {
        // Arrange
        String patientId = "123";
        List<QuestionnaireResponse> questionnaireResponses = Arrays.asList(
                new QuestionnaireResponse("1", patientId, "Response 1"),
                new QuestionnaireResponse("2", patientId, "Response 2")
        );
        // Mocking the behavior of the repository method to return the desired questionnaireResponses
        when(questionnaireResponseRepository.findByPatientId(patientId)).thenReturn(questionnaireResponses);

        List<String> result = questionnaireResponseService.getDataForExport(patientId);

        verify(questionnaireResponseRepository, times(1)).findByPatientId(patientId);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetAllDataForExport() {
        // Arrange
        String patientId1 = "123";
        String patientId2 = "456";

        List<QuestionnaireResponse> questionnaireResponses1 = Arrays.asList(
                new QuestionnaireResponse("1", patientId1, "Response 1"),
                new QuestionnaireResponse("2", patientId1, "Response 2")
        );
        List<QuestionnaireResponse> questionnaireResponses2 = Arrays.asList(
                new QuestionnaireResponse("3", patientId2, "Response 3"),
                new QuestionnaireResponse("4", patientId2, "Response 4")
        );

        when(questionnaireResponseRepository.findDistinctPatientIdd()).thenReturn(Arrays.asList(patientId1, patientId2));
        when(questionnaireResponseRepository.findByPatientId(patientId1)).thenReturn(questionnaireResponses1);
        when(questionnaireResponseRepository.findByPatientId(patientId2)).thenReturn(questionnaireResponses2);

        Map<String, Object> result = questionnaireResponseService.getAllDataForExport();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(result.containsKey(patientId1));
        List<String> patientData1 = (List<String>) result.get(patientId1);
        assertEquals(2, patientData1.size());

        assertTrue(result.containsKey(patientId2));
        List<String> patientData2 = (List<String>) result.get(patientId2);
        assertEquals(2, patientData2.size());
    }

    @Test
    void testGetAllDataForExport_NoData() {
        when(questionnaireResponseRepository.findDistinctPatientIdd()).thenReturn(Collections.emptyList());

        Map<String, Object> result = questionnaireResponseService.getAllDataForExport();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
    
    @Test
    void getConsultationsUnderLocationId() {
    }

    @Test
    public void testLogSyncAttempt() {
        // Create a mock UserMasterDto
        UserMasterDto userMasterDto = new UserMasterDto();
        userMasterDto.setUserName("testuser");

        when(userService.getCurrentUser()).thenReturn(ResponseEntity.ok(userMasterDto));

        questionnaireResponseService.logSyncAttempt();

        ArgumentCaptor<UserSyncLog> userSyncLogCaptor = ArgumentCaptor.forClass(UserSyncLog.class);
        verify(userSyncLogRepository).save(userSyncLogCaptor.capture());

        UserSyncLog capturedUserSyncLog = userSyncLogCaptor.getValue();
        assertNotNull(capturedUserSyncLog.getSyncAttemptTime());
        assertEquals("testuser", capturedUserSyncLog.getUsername());
    }

    // =============================================================
    // ======================== Mock Data Fn =======================
    // =============================================================

    List<FacilityDto> getMockFacilities() {
        FacilityDto f1 = new FacilityDto();
        FacilityDto f2 = new FacilityDto();
        FacilityDto f3 = new FacilityDto();
        f1.setFacilityId("f1");
        f2.setFacilityId("f2");
        f3.setFacilityId("f3");
        return List.of(f1, f2, f3);
    }

    List<EmcareResource> getMockEmcareResourceByFacilityIdInAndCreatedOnGreaterThan(List<String> facilityIds, Date d) throws ParseException {
        EmcareResource e1 = new EmcareResource();
        e1.setResourceId("r1");
        e1.setFacilityId("f1");
        e1.setCreatedOn(new SimpleDateFormat("dd/MM/yyyy").parse("01/08/2023"));

        EmcareResource e2 = new EmcareResource();
        e2.setResourceId("r2");
        e2.setFacilityId("f1");
        e2.setCreatedOn(new SimpleDateFormat("dd/MM/yyyy").parse("02/08/2023"));

        EmcareResource e3 = new EmcareResource();
        e3.setResourceId("r3");
        e3.setFacilityId("f2");
        e3.setCreatedOn(new SimpleDateFormat("dd/MM/yyyy").parse("03/08/2023"));

        EmcareResource e4 = new EmcareResource();
        e4.setResourceId("r4");
        e4.setFacilityId("f2");
        e4.setCreatedOn(new SimpleDateFormat("dd/MM/yyyy").parse("04/08/2023"));

        EmcareResource e5 = new EmcareResource();
        e5.setResourceId("r5");
        e5.setFacilityId("f5");
        e5.setCreatedOn(new SimpleDateFormat("dd/MM/yyyy").parse("05/08/2023"));

        List<EmcareResource> emcareResourceList = List.of(e1, e2, e3, e4, e5);

        List<EmcareResource> emcareResourceListFiltered = new ArrayList<>();


        for(EmcareResource em: emcareResourceList) {
            if(d != null && !em.getCreatedOn().after(d)) continue;
            for(String facilityId: facilityIds) {
                if(em.getFacilityId() == facilityId) {
                    emcareResourceListFiltered.add(em);
                }
            }
        }
        return  emcareResourceListFiltered;
    }

    List<QuestionnaireResponse> getMockQuestionnaireResponse(List<String> patientIds, Date d) throws ParseException {
        QuestionnaireResponse q1 = new QuestionnaireResponse();
        q1.setPatientId("r1");
        q1.setConsultationDate(new SimpleDateFormat("dd/MM/yyyy").parse("01/08/2023"));

        QuestionnaireResponse q2 = new QuestionnaireResponse();
        q2.setPatientId("r1");
        q2.setConsultationDate(new SimpleDateFormat("dd/MM/yyyy").parse("02/08/2023"));

        QuestionnaireResponse q3 = new QuestionnaireResponse();
        q3.setPatientId("r2");
        q3.setConsultationDate(new SimpleDateFormat("dd/MM/yyyy").parse("03/08/2023"));

        QuestionnaireResponse q4 = new QuestionnaireResponse();
        q4.setPatientId("r3");
        q4.setConsultationDate(new SimpleDateFormat("dd/MM/yyyy").parse("04/08/2023"));

        QuestionnaireResponse q5 = new QuestionnaireResponse();
        q5.setPatientId("r5");
        q5.setConsultationDate(new SimpleDateFormat("dd/MM/yyyy").parse("05/08/2023"));

        List<QuestionnaireResponse> questionnaireResponseList = List.of(q1, q2, q3, q4, q5);

        List<QuestionnaireResponse> questionnaireResponseListFiltered = new ArrayList<>();

        for(QuestionnaireResponse qr: questionnaireResponseList) {
            if(d != null && !qr.getConsultationDate().after(d)) continue;
            for(String patientId: patientIds) {
                if(qr.getPatientId() == patientId) questionnaireResponseListFiltered.add(qr);
            }
        }

        return  questionnaireResponseListFiltered;
    }
}