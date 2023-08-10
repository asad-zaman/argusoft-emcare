package com.argusoft.who.emcare.web.dashboard.controller;

import com.argusoft.who.emcare.web.dashboard.dto.DashboardDto;
import com.argusoft.who.emcare.web.dashboard.service.DashboardService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {DashboardController.class})
@RunWith(MockitoJUnitRunner.class)
class DashboardControllerTest {

    @Mock
    DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

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
    void testGetDashboardData() throws Exception {
        DashboardDtoImpl mockDashboardDto = new DashboardDtoImpl();
        mockDashboardDto.setTotalPatient(100L);
        mockDashboardDto.setPendingRequest(100L);
        mockDashboardDto.setTotalUser(200L);

        when(dashboardService.getDashboardData()).thenReturn(ResponseEntity.ok().body(mockDashboardDto));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/dashboard").accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        DashboardDtoImpl actualDashboardDto = objectMapper.readValue(response, DashboardDtoImpl.class);

        assertNotNull(actualDashboardDto);
        assertEquals(mockDashboardDto.getPendingRequest(), actualDashboardDto.getPendingRequest());
        assertEquals(mockDashboardDto.getTotalPatient(), actualDashboardDto.getTotalPatient());
        assertEquals(mockDashboardDto.getTotalUser(), actualDashboardDto.getTotalUser());
    }

    @Test
    void testGetDashboardBarChartData() throws Exception {
        HashMap<String, Object> mockBarChartData = new HashMap<>();

        List<String> mockMapViewData = new ArrayList<>();
        mockMapViewData.add("MapView1");
        mockMapViewData.add("MapView2");
        mockMapViewData.add("MapView3");

        HashMap<String, Object> mockConsultationByAgeGroupData = new HashMap<>();
        mockConsultationByAgeGroupData.put("0 to 2 Months", 32);
        mockConsultationByAgeGroupData.put("3 to 59 Months", 202);

        List<String> mockScatterChartData = new ArrayList<>();
        mockScatterChartData.add("{x: 1, y: 1}");
        mockScatterChartData.add("{x: 2, y: 2}");
        mockScatterChartData.add("{x: 3, y: 3}");
        mockScatterChartData.add("{x: 4, y: 4}");
        mockScatterChartData.add("{x: 5, y: 5}");

        List<String> mockConsultationPerFacilityData = new ArrayList<>();
        mockConsultationPerFacilityData.add("{x: 1, y: 1}");
        mockConsultationPerFacilityData.add("{x: 2, y: 2}");

        mockBarChartData.put("consultationPerFacility", mockConsultationPerFacilityData);
        mockBarChartData.put("consultationByAgeGroup", mockConsultationByAgeGroupData);
        mockBarChartData.put("scatterChart", mockScatterChartData);
        mockBarChartData.put("mapView", mockMapViewData);

        when(dashboardService.getDashboardBarChartData()).thenReturn(ResponseEntity.ok().body(mockBarChartData));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/dashboard/chart").accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        Map<String, Object> actualBarChart = objectMapper.readValue(response, new TypeReference<Map<String, Object>>(){});

        assertNotNull(actualBarChart);

        List<String> actualConsultationPerFacility = (List<String>) actualBarChart.get("consultationPerFacility");
        Map<String, Object> actualConsultationByAgeGroup = (Map<String, Object>) actualBarChart.get("consultationByAgeGroup");
        List<String> actualScatterChart = (List<String>) mockBarChartData.get("scatterChart");
        List<String> actualMapView = (List<String>) actualBarChart.get("mapView");

        assertNotNull(actualConsultationPerFacility);
        assertNotNull(actualConsultationByAgeGroup);
        assertNotNull(actualScatterChart);
        assertNotNull(actualMapView);

        assertEquals(mockMapViewData.size(), actualMapView.size());
        assertEquals(mockScatterChartData.size(), actualScatterChart.size());
        assertEquals(mockConsultationByAgeGroupData.size(), actualConsultationByAgeGroup.size());
        assertEquals(mockConsultationPerFacilityData.size(), actualConsultationPerFacility.size());
    }
}

class DashboardDtoImpl implements DashboardDto {
    private Long totalUser;
    private Long pendingRequest;
    private Long totalPatient;

    public void setTotalUser(Long totalUser) {
        this.totalUser = totalUser;
    }

    public void setPendingRequest(Long pendingRequest) {
        this.pendingRequest = pendingRequest;
    }

    public void setTotalPatient(Long totalPatient) {
        this.totalPatient = totalPatient;
    }

    @Override
    public Long getTotalUser() {
        return totalUser;
    }

    @Override
    public Long getPendingRequest() {
        return pendingRequest;
    }

    @Override
    public Long getTotalPatient() {
        return totalPatient;
    }
}