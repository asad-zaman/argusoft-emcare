package com.argusoft.who.emcare.web.device.service;

import com.argusoft.who.emcare.web.device.dto.DeviceDto;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jay
 */
public interface DeviceService {

    public ResponseEntity<Object> addNewDevice(DeviceDto deviceDto);

    public ResponseEntity<Object> updateDeviceDetails(DeviceDto deviceDto);

    public ResponseEntity<Object> changeDeviceStatus(Integer deviceId, Boolean status);

    public ResponseEntity<Object> getDeviceInfoByImei(String imei, String macAddress, String userId, String deviceUUID);

    public ResponseEntity<Object> getAllDevice(HttpServletRequest request);

}
