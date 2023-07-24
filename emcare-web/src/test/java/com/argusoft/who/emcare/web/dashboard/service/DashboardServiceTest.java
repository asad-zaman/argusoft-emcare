package com.argusoft.who.emcare.web.dashboard.service;

import com.argusoft.who.emcare.web.dashboard.dto.ChartDto;
import com.argusoft.who.emcare.web.dashboard.dto.DashboardDto;
import com.argusoft.who.emcare.web.dashboard.dto.ScatterCharDto;
import com.argusoft.who.emcare.web.dashboard.service.impl.DashboardServiceImpl;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.dto.FacilityMapDto;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import com.argusoft.who.emcare.web.fhir.service.LocationResourceService;
import com.argusoft.who.emcare.web.userlocationmapping.dao.UserLocationMappingRepository;
import org.codehaus.jackson.type.TypeReference;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {DashboardServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
class DashboardServiceTest {

    @Mock
    UserLocationMappingRepository userLocationMappingRepository;

    @Mock
    LocationResourceService locationResourceService;

    @Mock
    EmcareResourceService emcareResourceService;

    @InjectMocks
    DashboardServiceImpl dashboardService;

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
    void getDashboardData() throws IOException {
        DashboardDto dashboardDto = getDashboardDTODemoData();

        mock(UserLocationMappingRepository.class);

        when(userLocationMappingRepository.getDashboardData()).thenReturn(dashboardDto);

        ResponseEntity<Object> response = dashboardService.getDashboardData();
        DashboardDto body = (DashboardDto) response.getBody();

        assertNotNull(body);
        assertTrue(body.getPendingRequest() == dashboardDto.getPendingRequest());
        assertTrue(body.getTotalUser() == dashboardDto.getTotalUser());
        assertTrue(body.getTotalPatient() == dashboardDto.getTotalPatient());
    }

    @Test
    void getDashboardBarChartData() throws IOException {
        mock(UserLocationMappingRepository.class);
        mock(EmcareResourceService.class);
        mock(LocationResourceService.class);

        List<ChartDto> demoPieChartData = getDemoPieChartData();
        Map<String, Object> demoAgeData = getDemoAgeData();
        List<ScatterCharDto> demoScatterCharData = getDemoScatterCharData();
        List<FacilityMapDto> demoFacilityMapData = getDemoFacilityMapData();

        when(userLocationMappingRepository.getDashboardPieChartData()).thenReturn(demoPieChartData);
        when(emcareResourceService.getPatientAgeGroupCount()).thenReturn(demoAgeData);
        when(userLocationMappingRepository.getDashboardScatterChartData(any(Integer.class))).thenReturn(demoScatterCharData);
        when(locationResourceService.getFacilityDto(anyString())).thenAnswer(invocationOnMock -> getDemoFacilityDto(invocationOnMock.getArgument(0)));
        when(locationResourceService.getAllFacilityMapDto()).thenReturn(demoFacilityMapData);

        ResponseEntity<Object> response = dashboardService.getDashboardBarChartData();
        Map<String, Object> listMap = (HashMap<String, Object>) response.getBody();

        assertNotNull(listMap);
        assertNotNull(listMap.get("consultationPerFacility"));
        assertTrue(((List<Object>) listMap.get("consultationPerFacility")).size() == demoPieChartData.size());

        assertNotNull(listMap.get("consultationByAgeGroup"));
        Map<String, Object> responseAgeData = (Map<String, Object>)listMap.get("consultationByAgeGroup");
        demoAgeData.forEach((k, v) -> {
            assertTrue(((Number)responseAgeData.get(k)).longValue() == ((Number)v).longValue());
        });

        assertNotNull(listMap.get("scatterChart"));
        assertTrue(((List<Object>)listMap.get("scatterChart")).size() == demoScatterCharData.size());

        assertNotNull(listMap.get("mapView"));
        assertTrue(((List<Object>)listMap.get("mapView")).size() == demoFacilityMapData.size());
    }

    // Functions to fill demo data
    DashboardDto getDashboardDTODemoData() throws IOException {
        File file = new File("src/test/resources/mockdata/dashboard/demoDashboardDTO.json");
        InputStream fileInputStream = new FileInputStream(file);
        return objectMapper.readValue(fileInputStream, DashboardDTOImpl.class);
    }

    List<ChartDto> getDemoPieChartData () throws IOException {
        File file = new File("src/test/resources/mockdata/dashboard/demoPieChartData.json");
        InputStream fileInputStream = new FileInputStream(file);
        return objectMapper.readValue(fileInputStream, new TypeReference<List<ChartDtoImpl>>(){});
    }

    Map<String, Object> getDemoAgeData() throws IOException {
        File file = new File("src/test/resources/mockdata/dashboard/demoAgeData.json");
        InputStream fileInputStream = new FileInputStream(file);
        return objectMapper.readValue(fileInputStream, Map.class);
    }

    List<ScatterCharDto> getDemoScatterCharData() throws IOException {
        File file = new File("src/test/resources/mockdata/dashboard/demoScatterPlotData.json");
        InputStream fileInputStream = new FileInputStream(file);
        return objectMapper.readValue(fileInputStream, new TypeReference<List<ScatterCharDtoImpl>>(){});
    }

    FacilityDto getDemoFacilityDto(String facilityId) throws IOException {
        List<FacilityDto> facilityDtoList = getDemoFacilityData();
        for(int i = 0; i < facilityDtoList.size(); i++) {
            if(facilityDtoList.get(i).getFacilityId().equals(facilityId)) {
                return facilityDtoList.get(i);
            }
        }
        return null;
    }

    List<FacilityDto> getDemoFacilityData() throws IOException {
        File file = new File("src/test/resources/mockdata/dashboard/demoFacilitiesData.json");
        InputStream fileInputStream = new FileInputStream(file);
        return objectMapper.readValue(fileInputStream, new TypeReference<List<FacilityDto>>(){});
    }

    List<FacilityMapDto> getDemoFacilityMapData() throws IOException {
        File file = new File("src/test/resources/mockdata/dashboard/demoFacilityMapData.json");
        InputStream fileInputStream = new FileInputStream(file);
        return objectMapper.readValue(fileInputStream, new TypeReference<List<FacilityMapDto>>(){});
    }
}

// InterfaceImpClasses
class DashboardDTOImpl implements DashboardDto {
    Long totalUser = 200L;
    Long pendingRequest = 100L;
    Long totalPatient = 100L;

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

class ChartDtoImpl implements ChartDto {
    public String facilityId;
    public Long count;

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public String getFacilityId() {
        return facilityId;
    }
    @Override
    public Long getCount() {
        return count;
    }
}

class ScatterCharDtoImpl implements ScatterCharDto {
    public LocalDate day;
    public Integer count;

    public ScatterCharDtoImpl() { }

    public ScatterCharDtoImpl(String day, Integer i) {
        this.day = LocalDate.parse(day);
        this.count = i;
    }

    public ScatterCharDtoImpl(LocalDate day, Integer i) {
        this.day = day;
        this.count = i;
    }

    @Override
    public LocalDate getDay() {
        return day;
    }

    @Override
    public Integer getCount() {
        return count;
    }

    public void setDay(String day) {
        this.day = LocalDate.parse(day);
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
