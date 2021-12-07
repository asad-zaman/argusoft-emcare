package com.argusoft.who.emcare.web.device.service;

import com.argusoft.who.emcare.web.device.dto.DeviceDto;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author jay
 */
public interface DeviceService {

    public ResponseEntity<Object> addNewDevice(DeviceDto deviceDto);

    public ResponseEntity<Object> updateDeviceDetails(DeviceDto deviceDto);

    public ResponseEntity<Object> getDeviceInfoByImei(String imei, String macAddress, String userId);

    public ResponseEntity<Object> getAllDevice(HttpServletRequest request);

}
