package com.argusoft.who.emcare.web.dashboard.controller;

import com.argusoft.who.emcare.web.dashboard.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "**")
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    DashboardService dashboardService;


    @GetMapping("")
    public ResponseEntity<?> getDashboardData() {
        return dashboardService.getDashboardData();
    }
}
