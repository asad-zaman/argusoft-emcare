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
import org.junit.jupiter.api.*;
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
        map1.put("name1", "test1");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("name2", "test2");

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
        map1.put("name1", "name");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("name2", "name");

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

    @Nested
    class TestGetConsultationsUnderLocationId {
        @BeforeEach
        void setup() {
            when(locationMasterDao.getAllChildLocationId(anyInt()))
                    .thenAnswer(i -> getAllMockChildLocationIds(i.getArgument(0)));

            when(locationResourceRepository.findResourceIdIn(anyList()))
                    .thenAnswer(i -> getAllMockResourceIdsByLocationIds(i.getArgument(0)));

            when(questionnaireResponseRepository.getFilteredDateWithSearchCount(anyString(), any(Date.class), any(Date.class)))
                    .thenAnswer(i -> getMockResourceDataWithFilters(i.getArgument(0), null, i.getArgument(1), i.getArgument(2)));
            when(questionnaireResponseRepository.getFilteredDateWithSearch(anyString(), any(Date.class), any(Date.class), anyLong()))
                    .thenAnswer(i -> {
                        List<Map<String, Object>> ans = new ArrayList<>();
                        List<Map<String, Object>> data = getMockResourceDataWithFilters(i.getArgument(0), null, i.getArgument(1), i.getArgument(2));
                        Long x = i.getArgument(3);
                        long limit = x + 10;
                        while ((x < data.size()) && (x < limit)) {
                            ans.add(data.get(x.intValue()));
                            ++x;
                        }
                        return ans;
                    });

            when(questionnaireResponseRepository.getFilteredDateOnlyCount(any(Date.class), any(Date.class)))
                    .thenAnswer(i -> getMockResourceDataWithFilters(null, null, i.getArgument(0), i.getArgument(1)));
            when(questionnaireResponseRepository.getFilteredDateOnly(any(Date.class), any(Date.class), anyLong()))
                    .thenAnswer(i -> {
                        List<Map<String, Object>> ans = new ArrayList<>();
                        List<Map<String, Object>> data = getMockResourceDataWithFilters(null, null, i.getArgument(0), i.getArgument(1));
                        Long x = i.getArgument(2);
                        long limit = x + 10;
                        while ((x < data.size()) && (x < limit)) {
                            ans.add(data.get(x.intValue()));
                            ++x;
                        }
                        return ans;
                    });

            when(questionnaireResponseRepository.getFilteredConsultationWithSearchCount(anyString(), anyList(), any(Date.class), any(Date.class)))
                    .thenAnswer(i -> getMockResourceDataWithFilters(i.getArgument(0), i.getArgument(1), i.getArgument(2), i.getArgument(3)));
            when(questionnaireResponseRepository.getFilteredConsultationWithSearch(anyString(), anyList(), any(Date.class), any(Date.class), anyLong()))
                    .thenAnswer(i -> {
                        List<Map<String, Object>> ans = new ArrayList<>();
                        List<Map<String, Object>> data = getMockResourceDataWithFilters(i.getArgument(0), i.getArgument(1), i.getArgument(2), i.getArgument(3));
                        Long x = i.getArgument(4);
                        long limit = x + 10;
                        while ((x < data.size()) && (x < limit)) {
                            ans.add(data.get(x.intValue()));
                            ++x;
                        }
                        return ans;
                    });

            when(questionnaireResponseRepository.getFilteredConsultationsInCount(anyList(), any(Date.class), any(Date.class)))
                    .thenAnswer(i -> getMockResourceDataWithFilters(null, i.getArgument(0), i.getArgument(1), i.getArgument(2)));
            when(questionnaireResponseRepository.getFilteredConsultationsIn(anyList(), any(Date.class), any(Date.class), anyLong()))
                    .thenAnswer(i -> {
                        List<Map<String, Object>> ans = new ArrayList<>();
                        List<Map<String, Object>> data = getMockResourceDataWithFilters(null, i.getArgument(0), i.getArgument(1), i.getArgument(2));
                        Long x = i.getArgument(3);
                        long limit = x + 10;
                        while ((x < data.size()) && (x < limit)) {
                            ans.add(data.get(x.intValue()));
                            ++x;
                        }
                        return ans;
                    });
        }

        @Test
        void onlyLocationId() {
            PageDto actualPage = questionnaireResponseService.getConsultationsUnderLocationId(1, 0, null, null, null);
            assertNotNull(actualPage);
            assertEquals(9, actualPage.getTotalCount());
            assertEquals(9, actualPage.getList().size());
        }

        @Test
        void withLocationIdAndSearch() {
            PageDto actualPage = questionnaireResponseService.getConsultationsUnderLocationId(1, 0, null, null, "JOHN");
            assertNotNull(actualPage);
            assertEquals(1, actualPage.getTotalCount());
            assertEquals(1, actualPage.getList().size());
        }

        @Test
        void withLocationIdAndDates() throws ParseException {
            PageDto actualPage = questionnaireResponseService.getConsultationsUnderLocationId(
                    2,
                    0,
                    "2023-08-03",
                    "2023-08-05",
                    null
            );
            assertNotNull(actualPage);
            assertEquals(1, actualPage.getTotalCount());
            assertEquals(1, actualPage.getList().size());
        }

        @Test
        void withLocationIdAndSearchAndDates() throws ParseException {
            PageDto actualPage = questionnaireResponseService.getConsultationsUnderLocationId(
                    1,
                    0,
                    "2023-07-01",
                    "2023-08-03",
                    "BRAD"
            );
            assertNotNull(actualPage);
            assertEquals(1, actualPage.getTotalCount());
            assertEquals(1, actualPage.getList().size());
        }

        @Test
        void onlyPageNo() throws ParseException {
            PageDto actualPage = questionnaireResponseService.getConsultationsUnderLocationId(
                    "",
                    1,
                    null,
                    null,
                    null
            );
            assertNotNull(actualPage);
            assertEquals(10, actualPage.getTotalCount());
            assertEquals(0, actualPage.getList().size());
        }

        @Test
        void onlyBadSearch() throws ParseException {
            PageDto actualPage = questionnaireResponseService.getConsultationsUnderLocationId(
                    "",
                    0,
                    null,
                    null,
                    "NOT A GOOD SEARCH"
            );
            assertNotNull(actualPage);
            assertEquals(0, actualPage.getTotalCount());
            assertEquals(0, actualPage.getList().size());
        }

        @Test
        void onlyLocationAndEndDate() throws ParseException {
            PageDto actualPage = questionnaireResponseService.getConsultationsUnderLocationId(
                    "2",
                    0,
                    null,
                    "2023-08-02",
                    null
            );
            assertNotNull(actualPage);
            assertEquals(0, actualPage.getTotalCount());
            assertEquals(0, actualPage.getList().size());
        }

        @Test
        void onlyLocationAndEndDateCheckForInclusive() throws ParseException {
            PageDto actualPage = questionnaireResponseService.getConsultationsUnderLocationId(
                    "2",
                    0,
                    null,
                    "2023-08-03",
                    null
            );
            assertNotNull(actualPage);
            assertEquals(1, actualPage.getTotalCount());
            assertEquals(1, actualPage.getList().size());
        }

        @Test
        void onlyStartDate() throws ParseException {
            PageDto actualPage = questionnaireResponseService.getConsultationsUnderLocationId(
                    "",
                    0,
                    "2023-08-04",
                    null,
                    null
            );
            assertNotNull(actualPage);
            assertEquals(2, actualPage.getTotalCount());
            assertEquals(2, actualPage.getList().size());
        }
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


        for (EmcareResource em : emcareResourceList) {
            if (d != null && !em.getCreatedOn().after(d)) continue;
            for (String facilityId : facilityIds) {
                if (em.getFacilityId() == facilityId) {
                    emcareResourceListFiltered.add(em);
                }
            }
        }
        return emcareResourceListFiltered;
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

        for (QuestionnaireResponse qr : questionnaireResponseList) {
            if (d != null && !qr.getConsultationDate().after(d)) continue;
            for (String patientId : patientIds) {
                if (qr.getPatientId() == patientId) questionnaireResponseListFiltered.add(qr);
            }
        }

        return questionnaireResponseListFiltered;
    }

    List<Integer> getAllMockChildLocationIds(Integer i) {
        if (i == 1) return List.of(1, 2, 3);
        if (i == 2) return List.of(4, 5);
        return List.of();
    }

    List<String> getAllMockResourceIdsByLocationIds(List<Integer> locationIds) {
        Map<Integer, List<String>> locationResourceMap = new HashMap<>();
        locationResourceMap.put(1, List.of("f1", "f2", "f3"));
        locationResourceMap.put(2, List.of());
        locationResourceMap.put(3, List.of("f4", "f5"));
        locationResourceMap.put(4, List.of("f6"));
        locationResourceMap.put(5, List.of("f7"));
        List<String> rs = new ArrayList<>();
        for (Integer locationId : locationIds) {
            rs.addAll(locationResourceMap.get(locationId));
        }
        return rs;
    }

    List<Map<String, Object>> getMockResourceDataWithFilters(String searchString, List<String> facilityIds, Date startDate, Date endDate) throws ParseException {
        Map<String, Object> er0 = new HashMap<>();
        Map<String, Object> er1 = new HashMap<>();
        Map<String, Object> er2 = new HashMap<>();
        Map<String, Object> er3 = new HashMap<>();
        Map<String, Object> er4 = new HashMap<>();
        Map<String, Object> er5 = new HashMap<>();
        Map<String, Object> er6 = new HashMap<>();
        Map<String, Object> er7 = new HashMap<>();
        Map<String, Object> er8 = new HashMap<>();
        Map<String, Object> er9 = new HashMap<>();

        er0.put("name", "JOHN");
        er1.put("name", "BRAD");
        er2.put("name", "PHIL");
        er3.put("name", "ESTY");
        er4.put("name", "LUCK");
        er5.put("name", "THOR");
        er6.put("name", "SAIF");
        er7.put("name", "PARK");
        er8.put("name", "LOST");
        er9.put("name", "TANG");

        er0.put("facilityId", "f1");
        er1.put("facilityId", "f2");
        er2.put("facilityId", "f3");
        er3.put("facilityId", "f7");
        er4.put("facilityId", "f2");
        er5.put("facilityId", "f4");
        er6.put("facilityId", "f5");
        er7.put("facilityId", "f2");
        er8.put("facilityId", "f3");
        er9.put("facilityId", "f1");

        er0.put("consultationDate", new SimpleDateFormat("dd/MM/yyyy").parse("01/08/2023"));
        er1.put("consultationDate", new SimpleDateFormat("dd/MM/yyyy").parse("02/08/2023"));
        er2.put("consultationDate", new SimpleDateFormat("dd/MM/yyyy").parse("01/08/2023"));
        er3.put("consultationDate", new SimpleDateFormat("dd/MM/yyyy").parse("03/08/2023"));
        er4.put("consultationDate", new SimpleDateFormat("dd/MM/yyyy").parse("01/08/2023"));
        er5.put("consultationDate", new SimpleDateFormat("dd/MM/yyyy").parse("04/08/2023"));
        er6.put("consultationDate", new SimpleDateFormat("dd/MM/yyyy").parse("02/08/2023"));
        er7.put("consultationDate", new SimpleDateFormat("dd/MM/yyyy").parse("03/08/2023"));
        er8.put("consultationDate", new SimpleDateFormat("dd/MM/yyyy").parse("01/08/2023"));
        er9.put("consultationDate", new SimpleDateFormat("dd/MM/yyyy").parse("05/08/2023"));

        List<Map<String, Object>> emcareResources = List.of(er0, er1, er2, er3, er4, er5, er6, er7, er8, er9);
        List<Map<String, Object>> filteredEmcareResources = new ArrayList<>();
        Set<String> facilitySet = new HashSet<>();
        if (facilityIds != null) facilitySet.addAll(facilityIds);

        for (Map<String, Object> emcareResource : emcareResources) {
            if (searchString != null && !searchString.isEmpty() && !((String) emcareResource.get("name")).contains(searchString))
                continue;
            if (facilityIds != null && !facilitySet.contains(emcareResource.get("facilityId").toString())) continue;
            if (startDate.after((Date) emcareResource.get("consultationDate")) || endDate.before((Date) emcareResource.get("consultationDate")))
                continue;
            filteredEmcareResources.add(emcareResource);
        }
        return filteredEmcareResources;
    }

    // 1. SettingDto, Settings Entity
    // 2. UserPasswordDto, OTP Entity
    // 3. DeviceWithUserDetails, DeviceDto, DeviceMapper, DeviceMaster Entity
    // 4. MailDto, EmailContent Entity

    // 5. IndicatorDenominatorEquationDto, IndicatorDto, IndicatorFilterDto, IndicatorNumeratorEquationDto, IndicatorMapper
    // 6. Indicator Entity, IndicatorDenominatorEquation Entity, IndicatorNumeratorEquation Entity
    // 7. LanguageAddDto, LanguageDto, LanguageMapper

    // 8. LoginRequestDto, MultiLocationUserListDto, RoleDto, RoleUpdateDto, UserDto, UserListDto, UserMasterDto, UserUpdateDto
    // 9. RoleEntity, UserMapper
}