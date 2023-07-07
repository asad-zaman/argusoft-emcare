package com.argusoft.who.emcare.web.device.controller;

import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.device.dto.DeviceDto;
import com.argusoft.who.emcare.web.device.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.testcontainers.shaded.org.bouncycastle.cert.ocsp.Req;

import javax.annotation.Nullable;
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

    @PostMapping("/add")
    public ResponseEntity<Object> addNewDevice(@RequestBody DeviceDto deviceDto) {
        return deviceService.addNewDevice(deviceDto);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateDeviceDetails(@RequestBody DeviceDto deviceDto) {
        return deviceService.updateDeviceDetails(deviceDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllDevice(HttpServletRequest request) {
        return deviceService.getAllDevice(request);
    }

    @GetMapping("/page")
    public PageDto getDevicePage(HttpServletRequest request,
                                 @RequestParam(value = "pageNo") Integer pageNo,
                                 @Nullable @RequestParam(value = "orderBy", defaultValue = "deviceName") String orderBy,
                                 @Nullable @RequestParam(value = "order") String order,
                                 @Nullable @RequestParam(value = "search") String searchString
    ) {
        return deviceService.getDevicePage(request, pageNo, orderBy, order, searchString);
    }

    @GetMapping("/status/{deviceId}/{status}")
    public ResponseEntity<Object> changeDeviceStatus(@PathVariable(value = "deviceId") Integer deviceId, @PathVariable(value = "status") Boolean status) {
        return deviceService.changeDeviceStatus(deviceId, status);
    }

    @GetMapping("")
    public ResponseEntity<Object> getDeviceByImei(
            @RequestParam(value = "imei", required = false) String imei,
            @RequestParam(value = "macAddress", required = false) String macAddress,
            @RequestParam(value = "deviceUUID", required = false) String deviceUUID,
            @RequestParam(value = "userId", required = false) String userId
    ) {
        return deviceService.getDeviceInfoByImei(imei, macAddress, userId, deviceUUID);
    }
}
