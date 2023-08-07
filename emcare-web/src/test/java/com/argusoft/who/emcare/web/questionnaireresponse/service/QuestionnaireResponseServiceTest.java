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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

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
    public void testSaveOrUpdateQuestionnaireResponse() {
        // Prepare test data
        List<QuestionnaireResponseRequestDto> requestDtoList = new ArrayList<>();
        QuestionnaireResponseRequestDto requestDto1 = new QuestionnaireResponseRequestDto();
        requestDto1.setQuestionnaireResponseText("Response 1");
        QuestionnaireResponseRequestDto requestDto2 = new QuestionnaireResponseRequestDto();
        requestDto2.setQuestionnaireResponseText("Response 2");
        requestDtoList.add(requestDto1);
        requestDtoList.add(requestDto2);

        // Define the expected behavior of the mock repository
        QuestionnaireResponse savedResponse1 = new QuestionnaireResponse();
        savedResponse1.setId(UUID.randomUUID().toString());
        savedResponse1.setQuestionnaireResponseText("Response 1"); // Set the expected response text for the first DTO
        QuestionnaireResponse savedResponse2 = new QuestionnaireResponse();
        savedResponse2.setId(UUID.randomUUID().toString());
        savedResponse2.setQuestionnaireResponseText("Response 2"); // Set the expected response text for the second DTO
        when(questionnaireResponseRepository.save(any(QuestionnaireResponse.class))).thenReturn(savedResponse1, savedResponse2);

        // Call the service method
        List<QuestionnaireResponse> result = questionnaireResponseService.saveOrUpdateQuestionnaireResponse(requestDtoList);

        // Assertions
        // Verify that the repository save method was called twice with the correct arguments
        verify(questionnaireResponseRepository, times(2)).save(any(QuestionnaireResponse.class));
        // Verify the size of the returned list matches the size of the input list
        assertEquals(requestDtoList.size(), result.size());
        // Verify that the IDs of the saved responses are not null
        assertNotNull(result.get(0).getId());
        assertNotNull(result.get(1).getId());
        // Verify the questionnaire response text for each saved response
        assertEquals("Response 1", result.get(0).getQuestionnaireResponseText());
        assertEquals("Response 2", result.get(1).getQuestionnaireResponseText());
    }

    @Test
    public void testSaveOrUpdateQuestionnaireResponse_WithExistingId() {
        // Prepare test data with an existing ID
        List<QuestionnaireResponseRequestDto> requestDtoList = new ArrayList<>();
        QuestionnaireResponseRequestDto requestDto1 = new QuestionnaireResponseRequestDto();
        String existingId = UUID.randomUUID().toString();
        requestDto1.setId(existingId);
        requestDto1.setQuestionnaireResponseText("Response with Existing ID");
        requestDtoList.add(requestDto1);

        // Define the expected behavior of the mock repository
        QuestionnaireResponse savedResponse1 = new QuestionnaireResponse();
        savedResponse1.setId(existingId); // Set the same ID as in the DTO
        savedResponse1.setQuestionnaireResponseText("Response with Existing ID"); // Set the expected response text
        when(questionnaireResponseRepository.save(any(QuestionnaireResponse.class))).thenReturn(savedResponse1);

        // Call the service method
        List<QuestionnaireResponse> result = questionnaireResponseService.saveOrUpdateQuestionnaireResponse(requestDtoList);

        // Assertions
        // Verify that the repository save method was called once with the correct arguments
        verify(questionnaireResponseRepository, times(1)).save(any(QuestionnaireResponse.class));
        // Verify the size of the returned list is 1
        assertEquals(1, result.size());
        // Verify that the ID of the saved response matches the ID in the DTO
        assertEquals(existingId, result.get(0).getId());
        // Verify the questionnaire response text for the saved response
        assertEquals("Response with Existing ID", result.get(0).getQuestionnaireResponseText());
    }


    @Test
    public void testSaveOrUpdateQuestionnaireResponse_WithNullValues() {
        // Prepare test data with null values
        List<QuestionnaireResponseRequestDto> requestDtoList = new ArrayList<>();
        QuestionnaireResponseRequestDto requestDto1 = new QuestionnaireResponseRequestDto();
        // Leave all fields in the DTO as null

        // Call the service method
        List<QuestionnaireResponse> result = questionnaireResponseService.saveOrUpdateQuestionnaireResponse(requestDtoList);

        // Assertions
        // Verify that the repository save method was not called
        verify(questionnaireResponseRepository, times(0)).save(any(QuestionnaireResponse.class));
        // Verify the size of the returned list is 0
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

        // Act
        List<String> result = questionnaireResponseService.getDataForExport(patientId);

        // Assert
        // Verifying that the repository method is called with the correct patientId
        verify(questionnaireResponseRepository, times(1)).findByPatientId(patientId);
        // Assertions for the result
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

        // Act
        Map<String, Object> result = questionnaireResponseService.getAllDataForExport();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // Check patientId1 data
        assertTrue(result.containsKey(patientId1));
        List<String> patientData1 = (List<String>) result.get(patientId1);
        assertEquals(2, patientData1.size());

        // Check patientId2 data
        assertTrue(result.containsKey(patientId2));
        List<String> patientData2 = (List<String>) result.get(patientId2);
        assertEquals(2, patientData2.size());
    }

    @Test
    void testGetAllDataForExport_NoData() {
        // Arrange
        when(questionnaireResponseRepository.findDistinctPatientIdd()).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = questionnaireResponseService.getAllDataForExport();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }



    @Test
    void testGetConsultationsUnderLocationId() {
        // Arrange
        Object locationId = 1;
        Integer pageNo = 0;
        String sDate = "2023-08-01";
        String eDate = "2023-08-07";
        String searchString = "search";

        List<Map<String, Object>> resourcesList = Arrays.asList(
                createResourceMap("1", "Patient 1"),
                createResourceMap("2", "Patient 2")
        );
        Long totalCount = 2L;

        when(locationMasterDao.getAllChildLocationId(eq(1))).thenReturn(Arrays.asList(2, 3));
        when(locationResourceRepository.findResourceIdIn(eq(Arrays.asList(2, 3)))).thenReturn(Arrays.asList("facilityId1", "facilityId2"));
        when(questionnaireResponseRepository.getFilteredConsultationWithSearchCount(eq("search"), eq(Arrays.asList("facilityId1", "facilityId2")), any(Date.class), any(Date.class))).thenReturn(Arrays.asList("1", "2"));
        when(questionnaireResponseRepository.getFilteredConsultationWithSearch(eq("search"), eq(Arrays.asList("facilityId1", "facilityId2")), any(Date.class), any(Date.class), anyLong())).thenReturn(resourcesList);

        // Act
        PageDto result = questionnaireResponseService.getConsultationsUnderLocationId(locationId, pageNo, sDate, eDate, searchString);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalCount());
        assertEquals(2, result.getList().size());
    }

    @Test
    void getConsultationsUnderLocationId() {
    }

    @Test
    public void testLogSyncAttempt() {
        // Create a mock UserMasterDto
        UserMasterDto userMasterDto = new UserMasterDto();
        userMasterDto.setUserName("testuser");

        // Mock the behavior of userService.getCurrentUser() to return the mock UserMasterDto
        when(userService.getCurrentUser()).thenReturn(ResponseEntity.ok(userMasterDto));

        // Call the method
        questionnaireResponseService.logSyncAttempt();

        // Verify that save method was called on userSyncLogRepository with the correct UserSyncLog object
        ArgumentCaptor<UserSyncLog> userSyncLogCaptor = ArgumentCaptor.forClass(UserSyncLog.class);
        verify(userSyncLogRepository).save(userSyncLogCaptor.capture());

        // Get the captured UserSyncLog object and check its values
        UserSyncLog capturedUserSyncLog = userSyncLogCaptor.getValue();
        assertNotNull(capturedUserSyncLog.getSyncAttemptTime());
        assertEquals("testuser", capturedUserSyncLog.getUsername());
    }
}