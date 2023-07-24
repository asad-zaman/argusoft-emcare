package com.argusoft.who.emcare.web.dashboard.service;

import com.argusoft.who.emcare.web.EmcareWebApplication;
import com.argusoft.who.emcare.web.dashboard.dto.ChartDto;
import com.argusoft.who.emcare.web.dashboard.dto.DashboardDto;
import com.argusoft.who.emcare.web.dashboard.dto.ScatterCharDto;
import com.argusoft.who.emcare.web.dashboard.service.impl.DashboardServiceImpl;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.dto.FacilityMapDto;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import com.argusoft.who.emcare.web.fhir.service.LocationResourceService;
import com.argusoft.who.emcare.web.userlocationmapping.dao.UserLocationMappingRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3._1999.xhtml.Li;

import java.time.LocalDate;
import java.util.ArrayList;
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

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void getDashboardData() {
        mock(UserLocationMappingRepository.class);

        DashboardDto dashboardDto = new DashboardDto() {
            @Override
            public Long getTotalUser() {
                return 200L;
            }

            @Override
            public Long getPendingRequest() {
                return 100L;
            }

            @Override
            public Long getTotalPatient() {
                return 100L;
            }
        };

        when(userLocationMappingRepository.getDashboardData()).thenReturn(dashboardDto);

        ResponseEntity<Object> response = dashboardService.getDashboardData();
        DashboardDto body = (DashboardDto) response.getBody();

        assertNotNull(body);
        assertTrue(body.getPendingRequest() == 100L);
        assertTrue(body.getTotalUser() == 200L);
        assertTrue(body.getTotalPatient() == 100L);
    }

    @Test
    void getDashboardBarChartData() {
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
            assertTrue(((Long)responseAgeData.get(k)) == ((Long)v));
        });

        assertNotNull(listMap.get("scatterChart"));
        assertTrue(((List<Object>)listMap.get("scatterChart")).size() == demoScatterCharData.size());

        assertNotNull(listMap.get("mapView"));
        assertTrue(((List<Object>)listMap.get("mapView")).size() == demoFacilityMapData.size());
    }

    List<ChartDto> getDemoPieChartData () {
        List<ChartDto> demoPieChartData = new ArrayList<>();
        demoPieChartData.add(new ChartDtoImpl("ec00fb95-0d15-47e8-8fe4-ebdcbb373c93", 612L));
        demoPieChartData.add(new ChartDtoImpl("1a7f0458-196d-4b76-89ca-f723cad06248", 6L));
        demoPieChartData.add(new ChartDtoImpl("ed52aa7d-8515-4e52-9d7e-711cba34fadc", 31L));
        demoPieChartData.add(new ChartDtoImpl("adad81f6-ea1b-44ec-9452-1dd212f00e23", 8L));
        demoPieChartData.add(new ChartDtoImpl("fe189e5f-7f62-4ef1-928b-39798de67126", 9L));
        return  demoPieChartData;
    }

    Map<String, Object> getDemoAgeData() {
        Map<String, Object> demoAgeData = new HashMap<>();
        demoAgeData.put("0 to 2 Months", 34L);
        demoAgeData.put("3 to 59 Months", 200L);
        return  demoAgeData;
    }

    List<ScatterCharDto> getDemoScatterCharData() {
        List<ScatterCharDto> demoScatterCharData = new ArrayList<>();
        demoScatterCharData.add(new ScatterCharDtoImpl("2023-07-07", 0));
        demoScatterCharData.add(new ScatterCharDtoImpl("2023-07-10", 1));
        demoScatterCharData.add(new ScatterCharDtoImpl("2023-07-11", 10));
        demoScatterCharData.add(new ScatterCharDtoImpl("2023-07-13", 2));
        demoScatterCharData.add(new ScatterCharDtoImpl("2023-07-17", 4));
        demoScatterCharData.add(new ScatterCharDtoImpl("2023-07-20", 3));
        return demoScatterCharData;
    }

    FacilityDto getDemoFacilityDto(String facilityId) {
        List<FacilityDto> facilityDtoList = getDemoFacilityData();
        for(int i = 0; i < facilityDtoList.size(); i++) {
            if(facilityDtoList.get(i).getFacilityId().equals(facilityId)) {
                return facilityDtoList.get(i);
            }
        }
        return null;
    }

    List<FacilityDto> getDemoFacilityData() {
        List<FacilityDto> facilityDtoList = new ArrayList<>();
        FacilityDto f1 = new FacilityDto();
        f1.setLocationId(124L);
        f1.setLocationName("Geneva");
        f1.setFacilityId("1a7f0458-196d-4b76-89ca-f723cad06248");
        f1.setFacilityName("WHO Test");
        f1.setAddress("test");
        f1.setStatus("Active");

        FacilityDto f2 = new FacilityDto();
        f2.setLocationId(1452L);
        f2.setLocationName("AL-Aymen");
        f2.setFacilityId("ed52aa7d-8515-4e52-9d7e-711cba34fadc");
        f2.setFacilityName("Tammoz");
        f2.setAddress("Tammoz street");
        f2.setStatus("Active");

        FacilityDto f3 = new FacilityDto();
        f3.setLocationId(1448L);
        f3.setLocationName("AL-Jameea");
        f3.setFacilityId("fe189e5f-7f62-4ef1-928b-39798de67126");
        f3.setFacilityName("AL-Adoura");
        f3.setAddress("AL-Jameea street");
        f3.setStatus("Active");

        FacilityDto f4 = new FacilityDto();
        f4.setLocationId(1451L);
        f4.setLocationName("AL-Shaab");
        f4.setFacilityId("adad81f6-ea1b-44ec-9452-1dd212f00e23");
        f4.setFacilityName("Sulaiman AL-Faidhee");
        f4.setAddress("Sulaiman AL-Faidhee street");
        f4.setStatus("Active");

        FacilityDto f5 = new FacilityDto();
        f5.setLocationId(1455L);
        f5.setLocationName("AL -Yarmook");
        f5.setFacilityId("ec00fb95-0d15-47e8-8fe4-ebdcbb373c93");
        f5.setFacilityName("Al-Rabee-Test");
        f5.setAddress("Al-Rabee-Test");
        f5.setStatus("Active");

        facilityDtoList.add(f1);
        facilityDtoList.add(f2);
        facilityDtoList.add(f3);
        facilityDtoList.add(f4);
        facilityDtoList.add(f5);
        return facilityDtoList;
    }

    List<FacilityMapDto> getDemoFacilityMapData() {
        List<FacilityMapDto> facilityMapDtoList = new ArrayList<>();
        FacilityMapDto fM1 = new FacilityMapDto();
        fM1.setLocationId(124L);
        fM1.setLocationName("Geneva");
        fM1.setFacilityId("1a7f0458-196d-4b76-89ca-f723cad06248");
        fM1.setFacilityName("WHO Test");
        fM1.setAddress("test");
        fM1.setStatus("Active");
        fM1.setOrganizationId("13a3fba4-0be6-4b1c-abac-c26cf66c2f37");
        fM1.setOrganizationName("WHO");
        fM1.setLatitude("33.2233");
        fM1.setLongitude("43.6794");

        FacilityMapDto fM2 = new FacilityMapDto();
        fM2.setLocationId(1452L);
        fM2.setLocationName("AL-Aymen");
        fM2.setFacilityId("ed52aa7d-8515-4e52-9d7e-711cba34fadc");
        fM2.setFacilityName("Tammoz");
        fM2.setAddress("Tammoz street");
        fM2.setStatus("Active");
        fM2.setOrganizationId("13a3fba4-0be6-4b1c-abac-c26cf66c2f37");
        fM2.setOrganizationName("Iraq Health Ministry");
        fM2.setLatitude("33.30135300142959");
        fM2.setLongitude("44.361567780761405");

        FacilityMapDto fM3 = new FacilityMapDto();
        fM3.setLocationId(1448L);
        fM3.setLocationName("AL-Jameea");
        fM3.setFacilityId("fe189e5f-7f62-4ef1-928b-39798de67126");
        fM3.setFacilityName("AL-Adoura");
        fM3.setAddress("AL-Jameea street");
        fM3.setStatus("Active");
        fM3.setOrganizationId("13a3fba4-0be6-4b1c-abac-c26cf66c2f37");
        fM3.setOrganizationName("Iraq Health Ministry");
        fM3.setLatitude("33.3999142814339");
        fM3.setLongitude("44.4069036443254");

        FacilityMapDto fM4 = new FacilityMapDto();
        fM4.setLocationId(1451L);
        fM4.setLocationName("AL-Shaab");
        fM4.setFacilityId("adad81f6-ea1b-44ec-9452-1dd212f00e23");
        fM4.setFacilityName("Sulaiman AL-Faidhee");
        fM4.setAddress("Sulaiman AL-Faidhee street");
        fM4.setStatus("Active");
        fM4.setOrganizationId("13a3fba4-0be6-4b1c-abac-c26cf66c2f37");
        fM4.setOrganizationName("Iraq Health Ministry");
        fM4.setLatitude("33.399878453527414");
        fM4.setLongitude("44.40686072885849");

        FacilityMapDto fM5 = new FacilityMapDto();
        fM5.setLocationId(1455L);
        fM5.setLocationName("AL -Yarmook");
        fM5.setFacilityId("ec00fb95-0d15-47e8-8fe4-ebdcbb373c93");
        fM5.setFacilityName("Al-Rabee-Test");
        fM5.setAddress("Al-Rabee-Test");
        fM5.setStatus("Active");
        fM5.setOrganizationId("13a3fba4-0be6-4b1c-abac-c26cf66c2f37");
        fM5.setOrganizationName("Iraq Health Ministry");
        fM5.setLatitude("23");
        fM5.setLongitude("21");

        facilityMapDtoList.add(fM1);
        facilityMapDtoList.add(fM2);
        facilityMapDtoList.add(fM3);
        facilityMapDtoList.add(fM4);
        facilityMapDtoList.add(fM5);

        return  facilityMapDtoList;
    }
}

// InterfaceImpClasses
class ChartDtoImpl implements ChartDto {
    public String facilityId;
    public Long count;
    public ChartDtoImpl(String uuid, Long i) {
        this.facilityId = uuid;
        this.count = i;
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
    public ScatterCharDtoImpl(String day, Integer i) {
        this.day = LocalDate.parse(day);
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
}
