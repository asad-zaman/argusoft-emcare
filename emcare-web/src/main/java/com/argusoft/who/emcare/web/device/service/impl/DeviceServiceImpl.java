package com.argusoft.who.emcare.web.device.service.impl;

import com.argusoft.who.emcare.web.device.dao.DeviceRepository;
import com.argusoft.who.emcare.web.device.dto.DeviceDto;
import com.argusoft.who.emcare.web.device.dto.DeviceWithUserDetails;
import com.argusoft.who.emcare.web.device.mapper.DeviceMapper;
import com.argusoft.who.emcare.web.device.model.DeviceMaster;
import com.argusoft.who.emcare.web.device.service.DeviceService;
import com.argusoft.who.emcare.web.secuirty.EmCareSecurityUser;
import com.argusoft.who.emcare.web.user.service.UserService;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.keycloak.admin.client.resource.UsersResource;
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

    @Autowired
    UserService userService;

    @Override
    public ResponseEntity<Object> addNewDevice(DeviceDto deviceDto) {
        String userId = emCareSecurityUser.getLoggedInUser().getSubject();
        DeviceMaster oldDevice = deviceRepository.getDeviceByImei(deviceDto.getImeiNumber());
        if (oldDevice == null) {
            DeviceMaster newDevice = DeviceMapper.dtoToEntityDeviceMasterCreate(deviceDto, userId);
            newDevice = deviceRepository.save(newDevice);
            return ResponseEntity.status(HttpStatus.OK).body(newDevice);
        } else {
            DeviceMaster updatedDevice = DeviceMapper.dtoToEntityDeviceMasterUpdate(oldDevice, deviceDto, userId);
            deviceRepository.updateDevice(
                    updatedDevice.getAndroidVersion(),
                    updatedDevice.getLastLoggedInUser(),
                    updatedDevice.getIsBlocked(),
                    updatedDevice.getDeviceId()
            );
            return ResponseEntity.status(HttpStatus.OK).body(updatedDevice);
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
    public ResponseEntity<Object> getAllDevice(HttpServletRequest request) {
        UsersResource usersResource = userService.getAllUserResource(request);
        List<DeviceWithUserDetails> list = new ArrayList<>();
        List<DeviceMaster> allDevice = deviceRepository.findAll();
        allDevice.forEach(deviceMaster -> {
            list.add(DeviceMapper.entityToDtoDeviceWithUser(deviceMaster, usersResource));
        });
        return ResponseEntity.ok(list);
    }

}
