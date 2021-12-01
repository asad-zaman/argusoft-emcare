package com.argusoft.who.emcare.web.device.service.impl;

import com.argusoft.who.emcare.web.device.dao.DeviceRepository;
import com.argusoft.who.emcare.web.device.dto.DeviceDto;
import com.argusoft.who.emcare.web.device.mapper.DeviceMapper;
import com.argusoft.who.emcare.web.device.model.DeviceMaster;
import com.argusoft.who.emcare.web.device.service.DeviceService;
import com.argusoft.who.emcare.web.secuirty.EmCareSecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author jay
 */
@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    EmCareSecurityUser emCareSecurityUser;

    @Autowired
    DeviceRepository deviceRepository;

    @Override
    public ResponseEntity<Object> addNewDevice(DeviceDto deviceDto) {
        String userId = emCareSecurityUser.getLoggedInUser().getSubject();
        DeviceMaster newDevice = DeviceMapper.dtoToEntityDeviceMasterCreate(deviceDto, userId);
        if (deviceRepository.getDeviceByImei(newDevice.getImeiNumber()) == null) {
            newDevice = deviceRepository.save(newDevice);
            return ResponseEntity.status(HttpStatus.OK).body(newDevice);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("IMEI Already Exists");
        }
    }

    @Override
    public ResponseEntity<Object> updateDeviceDetails(DeviceDto deviceDto) {
        String userId = emCareSecurityUser.getLoggedInUser().getSubject();
        DeviceMaster oldDeviceDetails = deviceRepository.findById(deviceDto.getDeviceId()).get();
        DeviceMaster updatedDevice = DeviceMapper.dtoToEntityDeviceMasterUpdate(oldDeviceDetails, deviceDto, userId);
        deviceRepository.updateDevice(
                updatedDevice.getAndroidVersion(),
                updatedDevice.getLastLoggedInUser(),
                updatedDevice.getIsBlocked(),
                updatedDevice.getDeviceId()
        );
        return ResponseEntity.status(HttpStatus.OK).body(updatedDevice);
    }

    @Override
    public ResponseEntity<Object> getDeviceInfoByImei(String imei, String userId) {
        DeviceMaster device = new DeviceMaster();
        if (imei != null) {
            device = deviceRepository.getDeviceByImei(imei);
        }
        if (userId != null) {
            device = deviceRepository.getDeviceByuserId(userId);
        }

        return ResponseEntity.ok(device);
    }

    @Override
    public ResponseEntity<Object> getAllDevice() {
        return ResponseEntity.ok(deviceRepository.findAll());
    }

}
