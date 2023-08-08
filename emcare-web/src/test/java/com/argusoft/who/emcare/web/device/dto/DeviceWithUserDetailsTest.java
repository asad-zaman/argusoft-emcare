package com.argusoft.who.emcare.web.device.dto;

import com.argusoft.who.emcare.web.user.dto.MultiLocationUserListDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = {DeviceWithUserDetails.class})
public class DeviceWithUserDetailsTest {

    private DeviceWithUserDetails deviceWithUserDetails;

    @BeforeEach
    public void setUp(){
        deviceWithUserDetails = new DeviceWithUserDetails();
    }

    @Test
    public void testDeviceId(){
        Integer deviceId = 1;
        deviceWithUserDetails.setDeviceId(deviceId);
        assertEquals(deviceId, deviceWithUserDetails.getDeviceId());
    }

    @Test
    public void testAndroidVersion(){
        String androidVersion = "version1";
        deviceWithUserDetails.setAndroidVersion(androidVersion);
        assertEquals(androidVersion, deviceWithUserDetails.getAndroidVersion());
    }

    @Test
    public void testImeiNumber(){
        String imeiNumber = "number1";
        deviceWithUserDetails.setImeiNumber(imeiNumber);
        assertEquals(imeiNumber, deviceWithUserDetails.getImeiNumber());
    }
    @Test
    public void testMacAddress(){
        String macAddress = "address1";
        deviceWithUserDetails.setMacAddress(macAddress);
        assertEquals(macAddress, deviceWithUserDetails.getMacAddress());
    }

    @Test
    public void testDeviceName(){
        String deviceName = "device1";
        deviceWithUserDetails.setDeviceName(deviceName);
        assertEquals(deviceName, deviceWithUserDetails.getDeviceName());
    }

    @Test
    public void testDeviceOs(){
        String deviceOs = "deviceOs1";
        deviceWithUserDetails.setDeviceOs(deviceOs);
        assertEquals(deviceOs, deviceWithUserDetails.getDeviceOs());
    }

    @Test
    public void testDeviceUUID(){
        String deviceUUID = "UUID1";
        deviceWithUserDetails.setDeviceUUID(deviceUUID);
        assertEquals(deviceUUID, deviceWithUserDetails.getDeviceUUID());
    }

    @Test
    public void testDeviceModel(){
        String deviceModel = "model1";
        deviceWithUserDetails.setDeviceModel(deviceModel);
        assertEquals(deviceModel, deviceWithUserDetails.getDeviceModel());
    }

    @Test
    public void testLastLoggedInUser(){
        String lastLoggedInUser = "user1";
        deviceWithUserDetails.setLastLoggedInUser(lastLoggedInUser);
        assertEquals(lastLoggedInUser, deviceWithUserDetails.getLastLoggedInUser());
    }
    @Test
    public void testIsBlocked(){
        Boolean isBlocked = true;
        deviceWithUserDetails.setIsBlocked(isBlocked);
        assertEquals(isBlocked, deviceWithUserDetails.getIsBlocked());
    }

    @Test
    public void testIgVersion(){
        String igVersion = "version1";
        deviceWithUserDetails.setIgVersion(igVersion);
        assertEquals(igVersion, deviceWithUserDetails.getIgVersion());
    }

    @Test
    public void testUserResource(){
        MultiLocationUserListDto userResource = new MultiLocationUserListDto();
        userResource.setId("id1");
        deviceWithUserDetails.setUsersResource(userResource);
        assertEquals(userResource,deviceWithUserDetails.getUsersResource());
    }
}
