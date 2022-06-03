package com.argusoft.who.emcare.web.dashboard.service.impl;

import com.argusoft.who.emcare.web.dashboard.service.DashboardService;
import com.argusoft.who.emcare.web.userLocationMapping.dao.UserLocationMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    UserLocationMappingRepository userLocationMappingRepository;

    @Override
    public ResponseEntity<Object> getDashboardData() {
        return ResponseEntity.ok().body(userLocationMappingRepository.getDashboardData());
    }
}
