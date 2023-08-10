package com.argusoft.who.emcare.web.device.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = {DeviceDto.class})
public class DeviceDtoTest {
    
    private DeviceDto deviceDto;
    
    @BeforeEach
    public void setUp(){
        deviceDto = new DeviceDto();
    }

    @Test
    public void testDeviceId(){
        Integer deviceId = 1;
        deviceDto.setDeviceId(deviceId);
        assertEquals(deviceId, deviceDto.getDeviceId());
    }

    @Test
    public void testAndroidVersion(){
        String androidVersion = "version1";
        deviceDto.setAndroidVersion(androidVersion);
        assertEquals(androidVersion, deviceDto.getAndroidVersion());
    }

    @Test
    public void testImeiNumber(){
        String imeiNumber = "number1";
        deviceDto.setImeiNumber(imeiNumber);
        assertEquals(imeiNumber, deviceDto.getImeiNumber());
    }
    @Test
    public void testMacAddress(){
        String macAddress = "address1";
        deviceDto.setMacAddress(macAddress);
        assertEquals(macAddress, deviceDto.getMacAddress());
    }

    @Test
    public void testDeviceName(){
        String deviceName = "device1";
        deviceDto.setDeviceName(deviceName);
        assertEquals(deviceName, deviceDto.getDeviceName());
    }

    @Test
    public void testDeviceOs(){
        String deviceOs = "deviceOs1";
        deviceDto.setDeviceOs(deviceOs);
        assertEquals(deviceOs, deviceDto.getDeviceOs());
    }

    @Test
    public void testDeviceUUID(){
        String deviceUUID = "UUID1";
        deviceDto.setDeviceUUID(deviceUUID);
        assertEquals(deviceUUID, deviceDto.getDeviceUUID());
    }

    @Test
    public void testDeviceModel(){
        String deviceModel = "model1";
        deviceDto.setDeviceModel(deviceModel);
        assertEquals(deviceModel, deviceDto.getDeviceModel());
    }

    @Test
    public void testIsBlocked(){
        Boolean isBlocked = true;
        deviceDto.setIsBlocked(isBlocked);
        assertEquals(isBlocked, deviceDto.getIsBlocked());
    }

    @Test
    public void testIgVersion(){
        String igVersion = "version1";
        deviceDto.setIgVersion(igVersion);
        assertEquals(igVersion, deviceDto.getIgVersion());
    }

    @Test
    public void testUserId(){
        String userId = "id1";
        deviceDto.setUserId(userId);
        assertEquals(userId, deviceDto.getUserId());
    }
}
