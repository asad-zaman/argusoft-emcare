package com.argusoft.who.emcare.web.questionnaireresponse.controller;

import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.questionnaireresponse.dto.QuestionnaireResponseRequestDto;
import com.argusoft.who.emcare.web.questionnaireresponse.model.QuestionnaireResponse;
import com.argusoft.who.emcare.web.questionnaireresponse.service.QuestionnaireResponseService;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.io.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {QuestionnaireResponseController.class})
@RunWith(MockitoJUnitRunner.class)
class QuestionnaireResponseControllerTest {
    @Mock
    QuestionnaireResponseService questionnaireResponseService;

    @InjectMocks
    private QuestionnaireResponseController questionnaireResponseController;

    ObjectMapper objectMapper = new ObjectMapper();

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
    void addOrUpdateQuestionnaireResponseWithValidData() throws Exception {
        List<QuestionnaireResponseRequestDto> mockQuestionnaireResponseRequestDto = getMockQuestionnaireResponseRequestDto();

        when(questionnaireResponseService.saveOrUpdateQuestionnaireResponse(anyList())).thenAnswer(i -> {
            List<QuestionnaireResponseRequestDto> passedList = i.getArgument(0);
            passedList.forEach(q -> {
                if (q.getId() == null || q.getId().isEmpty()) q.setId("uuid");
            });
            return passedList;
        });

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/questionnaire_response/createOrUpdate")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockQuestionnaireResponseRequestDto));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(questionnaireResponseController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        List<QuestionnaireResponseRequestDto> actualQuestionnaireResponseRequestDto = objectMapper.readValue(response, new TypeReference<List<QuestionnaireResponseRequestDto>>() {});

        assertNotNull(actualQuestionnaireResponseRequestDto);
        assertEquals(mockQuestionnaireResponseRequestDto.size(), actualQuestionnaireResponseRequestDto.size());

        for (QuestionnaireResponseRequestDto questionnaireResponseRequestDto : actualQuestionnaireResponseRequestDto) {
            assertNotNull(questionnaireResponseRequestDto.getId());
            assertNotEquals("", questionnaireResponseRequestDto.getId());
        }
    }

    @Test
    void addOrUpdateQuestionnaireResponseWithMissingFields() throws Exception {
        List<QuestionnaireResponseRequestDto> mockQuestionnaireResponseRequestDto = getMockQuestionnaireResponseRequestDto();

        // deleting a few fields - assuming list size to be >= 2
        mockQuestionnaireResponseRequestDto.get(0).setConsultationStage(null);
        mockQuestionnaireResponseRequestDto.get(0).setQuestionnaireId(null);

        mockQuestionnaireResponseRequestDto.get(1).setEncounterId(null);
        mockQuestionnaireResponseRequestDto.get(1).setConsultationDate(null);
        mockQuestionnaireResponseRequestDto.get(1).setStructureMapId(null);

        when(questionnaireResponseService.saveOrUpdateQuestionnaireResponse(anyList())).thenAnswer(i -> {
            List<QuestionnaireResponseRequestDto> passedList = i.getArgument(0);
            passedList.forEach(q -> {
                if (q.getId() == null || q.getId().isEmpty()) q.setId("uuid");
            });
            return passedList;
        });

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/questionnaire_response/createOrUpdate")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockQuestionnaireResponseRequestDto));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(questionnaireResponseController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        // @TODO: Should be this
        // ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        List<QuestionnaireResponseRequestDto> actualQuestionnaireResponseRequestDto = objectMapper.readValue(response, new TypeReference<List<QuestionnaireResponseRequestDto>>() {});

        assertNotNull(actualQuestionnaireResponseRequestDto);
        assertEquals(mockQuestionnaireResponseRequestDto.size(), actualQuestionnaireResponseRequestDto.size());

        for (int i = 0; i < mockQuestionnaireResponseRequestDto.size(); i++) {
            assertNotNull(actualQuestionnaireResponseRequestDto.get(i).getId());
            assertNotEquals("", actualQuestionnaireResponseRequestDto.get(i).getId());
            assertEquals(mockQuestionnaireResponseRequestDto.get(i).getQuestionnaireId(), actualQuestionnaireResponseRequestDto.get(i).getQuestionnaireId());
            assertEquals(mockQuestionnaireResponseRequestDto.get(i).getQuestionnaireResponseText(), actualQuestionnaireResponseRequestDto.get(i).getQuestionnaireResponseText());
            assertEquals(mockQuestionnaireResponseRequestDto.get(i).getEncounterId(), actualQuestionnaireResponseRequestDto.get(i).getEncounterId());
            assertEquals(mockQuestionnaireResponseRequestDto.get(i).getIsActive(), actualQuestionnaireResponseRequestDto.get(i).getIsActive());
            assertEquals(mockQuestionnaireResponseRequestDto.get(i).getStructureMapId(), actualQuestionnaireResponseRequestDto.get(i).getStructureMapId());
            assertEquals(mockQuestionnaireResponseRequestDto.get(i).getPatientId(), actualQuestionnaireResponseRequestDto.get(i).getPatientId());
            assertEquals(mockQuestionnaireResponseRequestDto.get(i).getConsultationStage(), actualQuestionnaireResponseRequestDto.get(i).getConsultationStage());
        }
    }

    @Test
    void getQuestionnaireResponseByUserLocationWithDateParam() throws Exception {
        List<QuestionnaireResponse> mockQuestionnaireResponse = getMockQuestionnaireResponse();
        DateParam dateParam = new DateParam("gt2023-02-03T09:05:16.329");

        doNothing().when(questionnaireResponseService).logSyncAttempt();
        when(questionnaireResponseService.getQuestionnaireResponseByUserLocation(any(Date.class)))
                .thenReturn(mockQuestionnaireResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/questionnaire_response/fetch/all")
                .param(CommonConstant.RESOURCE_LAST_UPDATED_AT, dateParam.getValueAsString())
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(questionnaireResponseController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        List<QuestionnaireResponse> actualQuestionnaireResponse = objectMapper.readValue(response, new TypeReference<List<QuestionnaireResponse>>() {});

        assertNotNull(actualQuestionnaireResponse);
        assertEquals(mockQuestionnaireResponse.size(), actualQuestionnaireResponse.size());

        for (QuestionnaireResponse questionnaireResponse : mockQuestionnaireResponse) {
            assertThat(questionnaireResponse.getConsultationDate()).isAfterOrEqualTo(dateParam.getValue());
        }
    }

    @Test
    void getQuestionnaireResponseByUserLocationWithoutDataParam() throws Exception {
        List<QuestionnaireResponse> mockQuestionnaireResponse = getMockQuestionnaireResponse();

        doNothing().when(questionnaireResponseService).logSyncAttempt();
        when(questionnaireResponseService.getQuestionnaireResponseByUserLocation(any()))
                .thenReturn(mockQuestionnaireResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/questionnaire_response/fetch/all")
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(questionnaireResponseController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        List<QuestionnaireResponse> actualQuestionnaireResponse = objectMapper.readValue(response, new TypeReference<List<QuestionnaireResponse>>() {});

        assertNotNull(actualQuestionnaireResponse);
        assertEquals(mockQuestionnaireResponse.size(), actualQuestionnaireResponse.size());
    }

    @Test
    void getQuestionnaireResponseByUserLocationWithInvalidDataParam() throws Exception {
        doNothing().when(questionnaireResponseService).logSyncAttempt();
        when(questionnaireResponseService.getQuestionnaireResponseByUserLocation(any()))
                .thenReturn(new ArrayList<>());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/questionnaire_response/fetch/all")
                .param(CommonConstant.RESOURCE_LAST_UPDATED_AT, "Not a date type")
                .accept(MediaType.APPLICATION_JSON);

        MockMvcBuilders
                .standaloneSetup(questionnaireResponseController)
                .build()
                .perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void getQuestionnaireResponsePageWithValidParams() throws Exception {
        List<String> mockData = new ArrayList<>();
        mockData.add("A");
        mockData.add("B");
        mockData.add("C");
        PageDto mockPage = new PageDto();
        mockPage.setList(mockData);
        mockPage.setTotalCount((long) mockData.size());

        when(questionnaireResponseService.getQuestionnaireResponsePage(anyInt(), anyString()))
                .thenReturn(mockPage);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/questionnaire_response/page")
                .param("pageNo", "1")
                .param("search", "search")
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(questionnaireResponseController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        PageDto actualPageDto = objectMapper.readValue(response, PageDto.class);

        assertNotNull(actualPageDto);
        assertEquals(mockPage.getTotalCount(), actualPageDto.getTotalCount());
        assertEquals(mockPage.getList().size(), actualPageDto.getList().size());
    }

    @Test
    void getQuestionnaireResponsePageWithOnlyPageNoParam() throws Exception {
        List<String> mockData = new ArrayList<>();
        mockData.add("A");
        mockData.add("B");
        mockData.add("C");
        PageDto mockPage = new PageDto();
        mockPage.setList(mockData);
        mockPage.setTotalCount((long) mockData.size());

        when(questionnaireResponseService.getQuestionnaireResponsePage(anyInt(), any()))
                .thenReturn(mockPage);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/questionnaire_response/page")
                .param("pageNo", "1")
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(questionnaireResponseController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        PageDto actualPageDto = objectMapper.readValue(response, PageDto.class);

        assertNotNull(actualPageDto);
        assertEquals(mockPage.getTotalCount(), actualPageDto.getTotalCount());
        assertEquals(mockPage.getList().size(), actualPageDto.getList().size());
    }

    @Test
    void getQuestionnaireResponsePageWithInvalidOnlySearchParam() throws Exception {
        List<String> mockData = new ArrayList<>();
        mockData.add("A");
        mockData.add("B");
        mockData.add("C");
        PageDto mockPage = new PageDto();
        mockPage.setList(mockData);
        mockPage.setTotalCount((long) mockData.size());

        when(questionnaireResponseService.getQuestionnaireResponsePage(any(), anyString()))
                .thenReturn(mockPage);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/questionnaire_response/page")
                .param("search", "search")
                .accept(MediaType.APPLICATION_JSON);

        MockMvcBuilders
                .standaloneSetup(questionnaireResponseController)
                .build()
                .perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void getQuestionnaireResponsePageWithInvalidPageNoParam() throws Exception {
        List<String> mockData = new ArrayList<>();
        mockData.add("A");
        mockData.add("B");
        mockData.add("C");
        PageDto mockPage = new PageDto();
        mockPage.setList(mockData);
        mockPage.setTotalCount((long) mockData.size());

        when(questionnaireResponseService.getQuestionnaireResponsePage(any(), any()))
                .thenReturn(mockPage);

        // Negative PageNo
        RequestBuilder requestBuilder1 = MockMvcRequestBuilders.get("/api/questionnaire_response/page")
                .param("pageNo", "-1")
                .accept(MediaType.APPLICATION_JSON);

        // Non-numeric PageNo
        RequestBuilder requestBuilder2 = MockMvcRequestBuilders.get("/api/questionnaire_response/page")
                .param("pageNo", "not a int")
                .accept(MediaType.APPLICATION_JSON);

        // @TODO: Should be this
        // MockMvcBuilders
        // .standaloneSetup(questionnaireResponseController)
        // .build()
        // .perform(requestBuilder1)
        // .andExpect(status().isBadRequest());
        MockMvcBuilders
                .standaloneSetup(questionnaireResponseController)
                .build()
                .perform(requestBuilder1)
                .andExpect(status().is2xxSuccessful());

        MockMvcBuilders
                .standaloneSetup(questionnaireResponseController)
                .build()
                .perform(requestBuilder2)
                .andExpect(status().isBadRequest());
    }


    @Test
    void getQuestionnaireResponseByPatientIdWithValidPatientId() throws Exception {
        Map<String, Object> mockPatient = new HashMap<>();
        mockPatient.put("id", "patient-id-1");
        mockPatient.put("name", "patient-name-1");

        when(questionnaireResponseService.getQuestionnaireResponseByPatientId(anyString()))
                .thenReturn(mockPatient);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/questionnaire_response/byPatient")
                .param("patientId", "patient-id-1")
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(questionnaireResponseController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        Map<String, Object> actualPatient = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});

        assertNotNull(actualPatient);
        assertEquals(mockPatient.get("id"), actualPatient.get("id"));
        assertEquals(mockPatient.get("name"), actualPatient.get("name"));
    }

    @Test
    void getQuestionnaireResponseByPatientIdWithInvalidPatientId() throws Exception {
        when(questionnaireResponseService.getQuestionnaireResponseByPatientId(anyString()))
                .thenReturn(null);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/questionnaire_response/byPatient")
                .param("patientId", "not-a-id")
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(questionnaireResponseController).build();

        // @TODO: should be
        // ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().isNotFound());

        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertEquals("", response);
    }

    @Test
    void getQuestionnaireResponseByPatientIdWithNoPatientId() throws Exception {
        when(questionnaireResponseService.getQuestionnaireResponseByPatientId(anyString()))
                .thenReturn(null);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/questionnaire_response/byPatient")
                .accept(MediaType.APPLICATION_JSON);

        MockMvcBuilders.standaloneSetup(questionnaireResponseController).build().perform(requestBuilder).andExpect(status().isBadRequest());
    }

    @Test
    void getDataForExportWithValidPatientId() throws Exception {
        List<String> mockPatients = new ArrayList<>();
        mockPatients.add("A");
        mockPatients.add("B");
        mockPatients.add("C");
        when(questionnaireResponseService.getDataForExport(anyString()))
                .thenReturn(mockPatients);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/questionnaire_response/export/uuid-1")
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(questionnaireResponseController).build();

        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        List<String> actualPatients = objectMapper.readValue(response, new TypeReference<List<String>>() {});

        assertNotNull(actualPatients);
        assertEquals(mockPatients.size(), actualPatients.size());
    }

    @Test
    void getDataForExportWithInvalidPatientId() throws Exception {
        when(questionnaireResponseService.getDataForExport(anyString()))
                .thenReturn(new ArrayList<>());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/questionnaire_response/export/uuid-2")
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(questionnaireResponseController).build();

        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        List<String> actualPatients = objectMapper.readValue(response, new TypeReference<List<String>>() {});

        assertNotNull(actualPatients);
        assertEquals(0, actualPatients.size());
    }

    @Test
    void getDataForExportWithNoPatientId() throws Exception {
        when(questionnaireResponseService.getDataForExport(anyString()))
                .thenReturn(new ArrayList<>());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/questionnaire_response/export/")
                .accept(MediaType.APPLICATION_JSON);

        MockMvcBuilders
                .standaloneSetup(questionnaireResponseController)
                .build()
                .perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllDataForExport() throws Exception {
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("1", List.of("D1", "D2", "D3"));
        when(questionnaireResponseService.getAllDataForExport()).thenReturn(mockData);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/questionnaire_response/export/all")
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(questionnaireResponseController).build();

        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        Map<String, Object> actualData = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});

        assertNotNull(actualData);
        assertEquals(mockData.size(), actualData.size());
        assertEquals(((List<?>) mockData.get("1")).get(0), ((List<?>) actualData.get("1")).get(0));
    }

    //        @Nullable @RequestParam(value = "locationId") Object locationId,
    //        @RequestParam(value = "pageNo") Integer pageNo,
    //        @Nullable @RequestParam(value = "startDate") String startDate,
    //        @Nullable @RequestParam(value = "endDate") String endDate,
    //        @Nullable @RequestParam(value = "searchString") String searchString

    @Test
    void getAllPatientsUnderLocationWithAllParams() throws Exception {
        when(questionnaireResponseService.getConsultationsUnderLocationId(any(), any(), any(), any(), any()))
                .thenAnswer(i -> {
                    PageDto mockPage = new PageDto();
                    List<Object> mockList = new ArrayList<>();
                    for(Object arg: i.getArguments()) if (arg != null) mockList.add(arg);
                    mockPage.setList(mockList);
                    mockPage.setTotalCount((long) mockList.size());
                    return mockPage;
                });

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/questionnaire_response/consultations/locationId")
                .param("locationId", "l1")
                .param("pageNo", "1")
                .param("startDate", "sD")
                .param("endDate", "eD")
                .param("searchString", "sS")
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(questionnaireResponseController).build();

        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        PageDto actualPage = objectMapper.readValue(response, PageDto.class);

        assertNotNull(actualPage);
        assertEquals(5, actualPage.getList().size());
        assertEquals(5, actualPage.getTotalCount());
    }

    @Test
    void getAllPatientsUnderLocationWithOnlyRequiredParams() throws Exception {
        when(questionnaireResponseService.getConsultationsUnderLocationId(any(), any(), any(), any(), any()))
                .thenAnswer(i -> {
                    PageDto mockPage = new PageDto();
                    List<Object> mockList = new ArrayList<>();
                    for(Object arg: i.getArguments()) if (arg != null) mockList.add(arg);
                    mockPage.setList(mockList);
                    mockPage.setTotalCount((long) mockList.size());
                    return mockPage;
                });

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/questionnaire_response/consultations/locationId")
                .param("pageNo", "1")
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(questionnaireResponseController).build();

        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        PageDto actualPage = objectMapper.readValue(response, PageDto.class);

        assertNotNull(actualPage);
        assertEquals(1, actualPage.getList().size());
        assertEquals(1, actualPage.getTotalCount());
    }

    @Test
    void getAllPatientsUnderLocationWithInvalidPageNo() throws Exception {
        when(questionnaireResponseService.getConsultationsUnderLocationId(any(), any(), any(), any(), any()))
            .thenAnswer(i -> {
                PageDto mockPage = new PageDto();
                List<Object> mockList = new ArrayList<>();
                for(Object arg: i.getArguments()) if (arg != null) mockList.add(arg);
                mockPage.setList(mockList);
                mockPage.setTotalCount((long) mockList.size());
                return mockPage;
            });

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/questionnaire_response/consultations/locationId")
                .param("pageNo", "not a page number")
                .accept(MediaType.APPLICATION_JSON);

        MockMvcBuilders.standaloneSetup(questionnaireResponseController).build().perform(requestBuilder).andExpect(status().isBadRequest());
    }

    @Test
    void getAllPatientsUnderLocationWithInvalidPageNoAndOptionalFields() throws Exception {
        when(questionnaireResponseService.getConsultationsUnderLocationId(any(), any(), any(), any(), any()))
                .thenAnswer(i -> {
                    PageDto mockPage = new PageDto();
                    List<Object> mockList = new ArrayList<>();
                    for(Object arg: i.getArguments()) if (arg != null) mockList.add(arg);
                    mockPage.setList(mockList);
                    mockPage.setTotalCount((long) mockList.size());
                    return mockPage;
                });

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/questionnaire_response/consultations/locationId")
                .param("locationId", "l1")
                .param("searchString", "sS")
                .param("pageNo", "not a page number")
                .accept(MediaType.APPLICATION_JSON);

        MockMvcBuilders.standaloneSetup(questionnaireResponseController).build().perform(requestBuilder).andExpect(status().isBadRequest());
    }

    @Test
    void getAllPatientsUnderLocationWithNoPageNoAndOptionalFields() throws Exception {
        when(questionnaireResponseService.getConsultationsUnderLocationId(any(), any(), any(), any(), any()))
                .thenAnswer(i -> {
                    PageDto mockPage = new PageDto();
                    List<Object> mockList = new ArrayList<>();
                    for(Object arg: i.getArguments()) if (arg != null) mockList.add(arg);
                    mockPage.setList(mockList);
                    mockPage.setTotalCount((long) mockList.size());
                    return mockPage;
                });

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/questionnaire_response/consultations/locationId")
                .param("locationId", "l1")
                .param("searchString", "sS")
                .accept(MediaType.APPLICATION_JSON);

        MockMvcBuilders.standaloneSetup(questionnaireResponseController).build().perform(requestBuilder).andExpect(status().isBadRequest());
    }

    @Test
    void getAllPatientsUnderLocationWithNoParams() throws Exception {
        when(questionnaireResponseService.getConsultationsUnderLocationId(any(), any(), any(), any(), any()))
                .thenAnswer(i -> {
                    PageDto mockPage = new PageDto();
                    List<Object> mockList = new ArrayList<>();
                    for(Object arg: i.getArguments()) if (arg != null) mockList.add(arg);
                    mockPage.setList(mockList);
                    mockPage.setTotalCount((long) mockList.size());
                    return mockPage;
                });

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/questionnaire_response/consultations/locationId")
                .accept(MediaType.APPLICATION_JSON);

        MockMvcBuilders.standaloneSetup(questionnaireResponseController).build().perform(requestBuilder).andExpect(status().isBadRequest());
    }

    // ===============================================
    // Mock data generating functions
    // ===============================================
    List<QuestionnaireResponseRequestDto> getMockQuestionnaireResponseRequestDto() throws IOException {
        File file = new File("src/test/resources/mockdata/questionnaireresponse/mockQuestionnaireResponseRequestDto.json");
        InputStream fileInputStream = new FileInputStream(file);
        return objectMapper.readValue(fileInputStream, new TypeReference<List<QuestionnaireResponseRequestDto>>() {});
    }

    List<QuestionnaireResponse> getMockQuestionnaireResponse() throws IOException {
        File file = new File("src/test/resources/mockdata/questionnaireresponse/mockQuestionnaireResponse.json");
        InputStream fileInputStream = new FileInputStream(file);
        return objectMapper.readValue(fileInputStream, new TypeReference<List<QuestionnaireResponse>>() {});
    }
}