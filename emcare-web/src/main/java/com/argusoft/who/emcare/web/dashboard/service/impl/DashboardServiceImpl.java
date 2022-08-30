package com.argusoft.who.emcare.web.dashboard.service.impl;

import com.argusoft.who.emcare.web.dashboard.dto.ChartDto;
import com.argusoft.who.emcare.web.dashboard.service.DashboardService;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.service.LocationResourceService;
import com.argusoft.who.emcare.web.userLocationMapping.dao.UserLocationMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    UserLocationMappingRepository userLocationMappingRepository;

    @Autowired
    LocationResourceService locationResourceService;

    @Override
    public ResponseEntity<Object> getDashboardData() {
        return ResponseEntity.ok().body(userLocationMappingRepository.getDashboardData());
    }

    @Override
    public ResponseEntity<Object> getDashboardBarChartData() {
        List<ChartDto> barData = userLocationMappingRepository.getDashboardBarChartData();
        List<ChartDto> pieData = userLocationMappingRepository.getDashboardPieChartData();
        Map<String, Object> listMap = new HashMap<>();
        List<String> names = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        for (ChartDto chartDto : barData) {
            FacilityDto facilityDto = locationResourceService.getFacilityDto(chartDto.getFacilityId());
            names.add(facilityDto.getFacilityName());
            counts.add(chartDto.getCount());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("names", names);
        map.put("counts", counts);

        List<Map<String, Object>> pieD = new ArrayList<>();
        for (ChartDto chartDto : pieData) {
            FacilityDto facilityDto = locationResourceService.getFacilityDto(chartDto.getFacilityId());
            Map<String, Object> pie = new HashMap<>();
            pie.put("name", facilityDto.getFacilityName());
            pie.put("count", chartDto.getCount());
            pieD.add(pie);
        }
        listMap.put("barChart", map);
        listMap.put("pieChart", pieD);
        listMap.put("mapView", locationResourceService.getAllFacilityMapDto());
        return ResponseEntity.ok().body(listMap);
    }
}
