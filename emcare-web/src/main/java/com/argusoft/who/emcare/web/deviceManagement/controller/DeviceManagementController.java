package com.argusoft.who.emcare.web.deviceManagement.controller;

import com.argusoft.who.emcare.web.deviceManagement.model.DeviceManagement;
import com.argusoft.who.emcare.web.deviceManagement.service.DeviceManagementService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DeviceManagementController {
    
    @Autowired
    private DeviceManagementService deviceManagementService;
    
    @GetMapping("/devices")
    public List<DeviceManagement> retrieveDevices() {
        return deviceManagementService.retrieveDevices();
    }
    
    @PostMapping("/devices")
    public ResponseEntity<DeviceManagement> saveDevice(@RequestBody DeviceManagement device) {
        return new ResponseEntity<>(deviceManagementService.saveDevice(device), HttpStatus.OK);
    }
    
}
