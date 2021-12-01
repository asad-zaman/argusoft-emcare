package com.argusoft.who.emcare.web.deviceManagement.service.impl;

import com.argusoft.who.emcare.web.deviceManagement.dao.DeviceManagementRepository;
import com.argusoft.who.emcare.web.deviceManagement.model.DeviceManagement;
import com.argusoft.who.emcare.web.deviceManagement.service.DeviceManagementService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeviceManagementServiceImpl implements DeviceManagementService{

    @Autowired
    private DeviceManagementRepository deviceManagementRepository;
    
    @Override
    public DeviceManagement saveDevice(DeviceManagement device) {
        return deviceManagementRepository.save(device);
    }

    @Override
    public List<DeviceManagement> retrieveDevices() {
        return deviceManagementRepository.findAll();
    }
    
}
