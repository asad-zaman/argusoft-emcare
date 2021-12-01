package com.argusoft.who.emcare.web.deviceManagement.service;

import com.argusoft.who.emcare.web.deviceManagement.model.DeviceManagement;
import java.util.List;

public interface DeviceManagementService {
    
    public DeviceManagement saveDevice(DeviceManagement device);
    
    public List<DeviceManagement> retrieveDevices();
    
}
