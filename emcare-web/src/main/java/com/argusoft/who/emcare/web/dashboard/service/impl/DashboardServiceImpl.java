package com.argusoft.who.emcare.web.dashboard.service.impl;

import com.argusoft.who.emcare.web.dashboard.dto.ChartDto;
import com.argusoft.who.emcare.web.dashboard.dto.ScatterCharDto;
import com.argusoft.who.emcare.web.dashboard.service.DashboardService;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import com.argusoft.who.emcare.web.fhir.service.LocationResourceService;
import com.argusoft.who.emcare.web.userlocationmapping.dao.UserLocationMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.*;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    UserLocationMappingRepository userLocationMappingRepository;

    @Autowired
    LocationResourceService locationResourceService;

    @Autowired
    EmcareResourceService emcareResourceService;

    @Override
    public ResponseEntity<Object> getDashboardData() {
        return ResponseEntity.ok().body(userLocationMappingRepository.getDashboardData());
    }

    @Override
    public ResponseEntity<Object> getDashboardBarChartData() {
        List<ChartDto> pieData = userLocationMappingRepository.getDashboardPieChartData();
        Map<String, Integer> ageData = emcareResourceService.getPatientAgeGroupCount();
        Calendar calendar = Calendar.getInstance();
        int currentWeekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
        List<ScatterCharDto> scatterCharDtos = userLocationMappingRepository.getDashboardScatterChartData(currentWeekNumber);
        Map<String, Object> listMap = new HashMap<>();

        List<Map<String, Object>> pieD = new ArrayList<>();
        for (ChartDto chartDto : pieData) {
            FacilityDto facilityDto = locationResourceService.getFacilityDto(chartDto.getFacilityId());
            Map<String, Object> pie = new HashMap<>();
            pie.put("name", facilityDto.getFacilityName());
            pie.put("count", chartDto.getCount());
            pieD.add(pie);
        }
        List<List<Object>> scatterPoints = new ArrayList<>();
        for (ScatterCharDto scatterCharDto : scatterCharDtos) {
            LocalDate week = LocalDate.now().with(ChronoField.ALIGNED_WEEK_OF_YEAR, scatterCharDto.getWeekly());
            LocalDate weekStartDate = week.with(DayOfWeek.MONDAY);
            List<Object> tuple = new ArrayList<>();
            tuple.add(scatterCharDto.getWeekly());
            tuple.add(scatterCharDto.getCount());
            tuple.add(Date.valueOf(weekStartDate));
            scatterPoints.add(tuple);
        }
        listMap.put("consultationPerFacility", pieD);
        listMap.put("consultationByAgeGroup", ageData);
        listMap.put("scatterChart", scatterPoints);
        listMap.put("mapView", locationResourceService.getAllFacilityMapDto());
        return ResponseEntity.ok().body(listMap);
    }
}
