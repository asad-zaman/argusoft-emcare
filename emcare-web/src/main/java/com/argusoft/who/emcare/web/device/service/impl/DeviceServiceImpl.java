package com.argusoft.who.emcare.web.device.service.impl;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.config.KeyCloakConfig;
import com.argusoft.who.emcare.web.device.dao.DeviceRepository;
import com.argusoft.who.emcare.web.device.dto.DeviceDto;
import com.argusoft.who.emcare.web.device.dto.DeviceWithUserDetails;
import com.argusoft.who.emcare.web.device.mapper.DeviceMapper;
import com.argusoft.who.emcare.web.device.model.DeviceMaster;
import com.argusoft.who.emcare.web.device.service.DeviceService;
import com.argusoft.who.emcare.web.secuirty.EmCareSecurityUser;
import com.argusoft.who.emcare.web.user.dto.UserListDto;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
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

    @Autowired
    KeyCloakConfig keyCloakConfig;

    @Override
    public ResponseEntity<Object> addNewDevice(DeviceDto deviceDto) {
        String userId = emCareSecurityUser.getLoggedInUserId();
        DeviceMaster oldDevice = deviceRepository.getDeviceByDeviceUUID(deviceDto.getDeviceUUID());
        if (oldDevice == null) {
            DeviceMaster newDevice = DeviceMapper.getDeviceMatserFromDto(deviceDto, userId);
            newDevice = deviceRepository.save(newDevice);
            return ResponseEntity.status(HttpStatus.OK).body(newDevice);

        } else {
            DeviceMaster updatedDevice = DeviceMapper.getDeviceMaster(oldDevice, deviceDto, userId);
            deviceRepository.updateDevice(updatedDevice.getAndroidVersion(), updatedDevice.getLastLoggedInUser(), updatedDevice.getIsBlocked(), updatedDevice.getDeviceId());
            return ResponseEntity.status(HttpStatus.OK).body(deviceDto);
        }
    }

    @Override
    public ResponseEntity<Object> updateDeviceDetails(DeviceDto deviceDto) {
        String userId = emCareSecurityUser.getLoggedInUser().getSubject();
        DeviceMaster oldDeviceDetails = deviceRepository.findById(deviceDto.getDeviceId()).get();
        Keycloak keycloak = keyCloakConfig.getInstance();
        UserResource userResource = keycloak.realm(KeyCloakConfig.REALM).users().get(userId);
        UserSessionRepresentation sessions = userResource.getUserSessions().get(0);
        keycloak.realm(KeyCloakConfig.REALM).deleteSession(sessions.getId());
        DeviceMaster updatedDevice = DeviceMapper.getDeviceMaster(oldDeviceDetails, deviceDto, userId);
//        deviceRepository.updateDevice(
//                updatedDevice.getAndroidVersion(),
//                updatedDevice.getLastLoggedInUser(),
//                updatedDevice.getIsBlocked(),
//                updatedDevice.getDeviceId()
//        );
        return ResponseEntity.status(HttpStatus.OK).body(updatedDevice);
    }

    @Override
    public ResponseEntity<Object> changeDeviceStatus(Integer deviceId, Boolean status) {
        DeviceMaster deviceInfo = deviceRepository.findById(deviceId).get();
        deviceInfo.setIsBlocked(status);
        deviceRepository.save(deviceInfo);
        Keycloak keycloak = keyCloakConfig.getInstance();
        UserResource userResource = keycloak.realm(KeyCloakConfig.REALM).users().get(deviceInfo.getLastLoggedInUser());
        List<UserSessionRepresentation> userSessions = userResource.getUserSessions();
        if (!userSessions.isEmpty()) {
            UserSessionRepresentation sessions = userSessions.get(0);
            keycloak.realm(KeyCloakConfig.REALM).deleteSession(sessions.getId());
        }
        return ResponseEntity.ok(deviceInfo);
    }

    @Override
    public ResponseEntity<Object> getDeviceInfoByImei(String imei, String macAddress, String userId, String deviceUUID) {
        DeviceMaster device = new DeviceMaster();
        if (imei != null) {
            device = deviceRepository.getDeviceByImei(imei);
        }
        if (userId != null) {
            device = deviceRepository.getDeviceByuserId(userId);
        }
        if (macAddress != null) {
            device = deviceRepository.getDeviceByMacAddress(macAddress);
        }
        if (deviceUUID != null) {
            device = deviceRepository.getDeviceByDeviceUUID(deviceUUID);
        }


        return ResponseEntity.ok(device);
    }

    @Override
    public ResponseEntity<Object> getAllDevice(HttpServletRequest request) {
        List<UserListDto> allUsers = userService.getAllUser(request);
        List<DeviceWithUserDetails> list = new ArrayList<>();
        List<DeviceMaster> allDevice = deviceRepository.findAll();
        allDevice.forEach(deviceMaster -> list.add(DeviceMapper.getDeviceWithUser(deviceMaster, allUsers)));
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<Object> getDevicePage(HttpServletRequest request, Integer pageNo, String orderBy, String order, String searchString) {
        List<UserListDto> allUsers = userService.getAllUser(request);
        List<DeviceWithUserDetails> list = new ArrayList<>();
        if (orderBy.equalsIgnoreCase("null")) {
            orderBy = "deviceName";
        }
        Sort sort = order.equalsIgnoreCase(CommonConstant.DESC) ? Sort.by(orderBy).descending() : Sort.by(orderBy).ascending();
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE, !sort.isEmpty() ? sort : null);
        Long totalCount = 0L;
        Page<DeviceMaster> allDevice;
        if (searchString != null && !searchString.isEmpty()) {
            totalCount = Long.valueOf(deviceRepository.findByAndroidVersionContainingIgnoreCaseOrDeviceNameContainingIgnoreCaseOrDeviceOsContainingIgnoreCaseOrDeviceModelContainingIgnoreCase(searchString, searchString, searchString, searchString).size());
            allDevice = deviceRepository.findByAndroidVersionContainingIgnoreCaseOrDeviceNameContainingIgnoreCaseOrDeviceOsContainingIgnoreCaseOrDeviceModelContainingIgnoreCase(searchString, searchString, searchString, searchString, page);
        } else {
            totalCount = deviceRepository.count();
            allDevice = deviceRepository.findAll(page);
        }
//        Page<DeviceMaster> allDevice = deviceRepository.findAll(page);
        allDevice.forEach(deviceMaster -> list.add(DeviceMapper.getDeviceWithUser(deviceMaster, allUsers)));

        PageDto pageDto = new PageDto();
        pageDto.setList(list);
        pageDto.setTotalCount(totalCount);
        return ResponseEntity.ok(pageDto);
    }

}
