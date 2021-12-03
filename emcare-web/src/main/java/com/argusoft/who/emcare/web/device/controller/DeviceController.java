package com.argusoft.who.emcare.web.device.controller;

import com.argusoft.who.emcare.web.device.dto.DeviceDto;
import com.argusoft.who.emcare.web.device.service.DeviceService;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
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

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Object> getDeviceByImei(
            @RequestParam(value = "imei", required = false) String imei,
            @RequestParam(value = "userId", required = false) String userId
    ) {
        return deviceService.getDeviceInfoByImei(imei, userId);
    }
}
