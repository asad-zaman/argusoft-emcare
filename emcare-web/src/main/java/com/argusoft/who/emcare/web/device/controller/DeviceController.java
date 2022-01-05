package com.argusoft.who.emcare.web.device.controller;

import com.argusoft.who.emcare.web.device.dto.DeviceDto;
import com.argusoft.who.emcare.web.device.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jay
 */
@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api/device")
public class DeviceController {

    @Autowired
    DeviceService deviceService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<Object> addNewDevice(@RequestBody DeviceDto deviceDto) {
        return deviceService.addNewDevice(deviceDto);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateDeviceDetails(@RequestBody DeviceDto deviceDto) {
        return deviceService.updateDeviceDetails(deviceDto);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllDevice(HttpServletRequest request) {
        return deviceService.getAllDevice(request);
    }

    @GetMapping("/status/{deviceId}/{status}")
    public ResponseEntity<Object> changeDeviceStatus(@PathVariable(value = "deviceId") Integer deviceId, @PathVariable(value = "status") Boolean status) {
        return deviceService.changeDeviceStatus(deviceId, status);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Object> getDeviceByImei(
            @RequestParam(value = "imei", required = false) String imei,
            @RequestParam(value = "macAddress", required = false) String macAddress,
            @RequestParam(value = "deviceUUID", required = false) String deviceUUID,
            @RequestParam(value = "userId", required = false) String userId
    ) {
        return deviceService.getDeviceInfoByImei(imei, macAddress, userId, deviceUUID);
    }
}
