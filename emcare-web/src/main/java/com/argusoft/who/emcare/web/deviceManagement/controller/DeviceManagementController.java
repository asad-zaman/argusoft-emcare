package com.argusoft.who.emcare.web.deviceManagement.controller;

import com.argusoft.who.emcare.web.deviceManagement.dao.DeviceManagementRepository;
import com.argusoft.who.emcare.web.deviceManagement.model.DeviceManagement;
import com.argusoft.who.emcare.web.deviceManagement.service.DeviceManagementService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devices")
public class DeviceManagementController {
    
    @Autowired
    private DeviceManagementService deviceManagementService;
    
    @Autowired
    private DeviceManagementRepository deviceManagementRepository;
    
    @GetMapping("")
    public List<DeviceManagement> retrieveDevices() {
        return deviceManagementService.retrieveDevices();
    }
    
    @PostMapping("")
    public ResponseEntity<DeviceManagement> saveOrUpdateDevice(@RequestBody DeviceManagement device) {
        return new ResponseEntity<>(deviceManagementService.saveDevice(device), HttpStatus.OK);
    }
    
}
