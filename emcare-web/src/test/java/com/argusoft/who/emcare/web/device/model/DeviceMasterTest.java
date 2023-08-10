package com.argusoft.who.emcare.web.device.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = {DeviceMaster.class})
public class DeviceMasterTest {

    private DeviceMaster deviceMaster;

    @BeforeEach
    public void setUp(){
        deviceMaster = new DeviceMaster();
    }

    @Test
    public void testDeviceId(){
        Integer deviceId = 1;
        deviceMaster.setDeviceId(deviceId);
        assertEquals(deviceId, deviceMaster.getDeviceId());
    }

    @Test
    public void testAndroidVersion(){
        String androidVersion = "version1";
        deviceMaster.setAndroidVersion(androidVersion);
        assertEquals(androidVersion, deviceMaster.getAndroidVersion());
    }

    @Test
    public void testImeiNumber(){
        String imeiNumber = "number1";
        deviceMaster.setImeiNumber(imeiNumber);
        assertEquals(imeiNumber, deviceMaster.getImeiNumber());
    }
    @Test
    public void testMacAddress(){
        String macAddress = "address1";
        deviceMaster.setMacAddress(macAddress);
        assertEquals(macAddress, deviceMaster.getMacAddress());
    }

    @Test
    public void testDeviceName(){
        String deviceName = "device1";
        deviceMaster.setDeviceName(deviceName);
        assertEquals(deviceName, deviceMaster.getDeviceName());
    }

    @Test
    public void testDeviceOs(){
        String deviceOs = "deviceOs1";
        deviceMaster.setDeviceOs(deviceOs);
        assertEquals(deviceOs, deviceMaster.getDeviceOs());
    }

    @Test
    public void testDeviceUUID(){
        String deviceUUID = "UUID1";
        deviceMaster.setDeviceUUID(deviceUUID);
        assertEquals(deviceUUID, deviceMaster.getDeviceUUID());
    }

    @Test
    public void testDeviceModel(){
        String deviceModel = "model1";
        deviceMaster.setDeviceModel(deviceModel);
        assertEquals(deviceModel, deviceMaster.getDeviceModel());
    }

    @Test
    public void testIsBlocked(){
        Boolean isBlocked = true;
        deviceMaster.setIsBlocked(isBlocked);
        assertEquals(isBlocked, deviceMaster.getIsBlocked());
    }

    @Test
    public void testIgVersion(){
        String igVersion = "version1";
        deviceMaster.setIgVersion(igVersion);
        assertEquals(igVersion, deviceMaster.getIgVersion());
    }

    @Test
    public void testLastLoggedInUser(){
        String lastLoggedInUser = "user1";
        deviceMaster.setLastLoggedInUser(lastLoggedInUser);
        assertEquals(lastLoggedInUser, deviceMaster.getLastLoggedInUser());
    }

    @Test
    public void testUserName(){
        String userName = "user1";
        deviceMaster.setUserName(userName);
        assertEquals(userName, deviceMaster.getUserName());
    }

    @Test
    void testEqualsAndHashCode() {
        DeviceMaster device1 = new DeviceMaster();
        DeviceMaster device2 = new DeviceMaster();

        device1.setDeviceId(1);
        device1.setAndroidVersion("v1");
        device1.setUserName("user1");
        device1.setLastLoggedInUser("user1");
        device1.setDeviceOs("abc");
        device1.setDeviceModel("model1");
        device1.setDeviceName("device1");
        device1.setDeviceUUID("uuid1");
        device1.setIsBlocked(true);
        device1.setIgVersion("v1");
        device1.setImeiNumber("imei1");
        device1.setMacAddress("mac1");

        device2.setDeviceId(1);
        device2.setAndroidVersion("v1");
        device2.setUserName("user1");
        device2.setLastLoggedInUser("user1");
        device2.setDeviceOs("abc");
        device2.setDeviceModel("model1");
        device2.setDeviceName("device1");
        device2.setDeviceUUID("uuid1");
        device2.setIsBlocked(true);
        device2.setIgVersion("v1");
        device2.setImeiNumber("imei1");
        device2.setMacAddress("mac1");

        assertEquals(device1, device2);

        assertEquals(device1.hashCode(), device2.hashCode());

        DeviceMaster differentDevice = new DeviceMaster();
        differentDevice.setDeviceId(3);

        assertNotEquals(device1, differentDevice);
    }
}
