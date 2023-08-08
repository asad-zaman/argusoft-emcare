package com.argusoft.who.emcare.web.device.mapper;

import com.argusoft.who.emcare.web.device.dto.DeviceDto;
import com.argusoft.who.emcare.web.device.dto.DeviceWithUserDetails;
import com.argusoft.who.emcare.web.device.model.DeviceMaster;
import com.argusoft.who.emcare.web.user.dto.MultiLocationUserListDto;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = {DeviceMapper.class})
public class DeviceMapperTest {

    @Test
    void testConstructorInitialization() {
        DeviceMapper deviceMapper = new DeviceMapper();

        assertNotNull(deviceMapper, "DeviceMapper instance should not be null");
    }

    @Test
    public void testGetDeviceMasterFromDto(){
        DeviceDto master = new DeviceDto();

        master.setAndroidVersion("v1");
        master.setImeiNumber("imei1");
        master.setMacAddress("add1");
        master.setDeviceModel("model1");
        master.setDeviceName("device1");
        master.setDeviceUUID("uuid1");
        master.setDeviceOs("deviceOs1");
        master.setIsBlocked(true);
        master.setIgVersion("v1");
        master.setDeviceId(1);

        String userName = "user1";
        String userId = "id1";

        DeviceMaster master1 = DeviceMapper.getDeviceMatserFromDto(master,userId,userName);

        assertNotNull(master1);
        assertEquals(master1.getIsBlocked(),master.getIsBlocked());
        assertEquals(master1.getAndroidVersion(),master.getAndroidVersion());
    }

    @Test
    public void testGetDeviceMaster(){
        String userName = "User";

        DeviceMaster deviceMaster = new DeviceMaster();
        deviceMaster.setDeviceId(1);
        deviceMaster.setImeiNumber("imei1");
        deviceMaster.setLastLoggedInUser("user1");

        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setAndroidVersion("v1");
        deviceDto.setMacAddress("add1");
        deviceDto.setIgVersion("v1");
        deviceDto.setIsBlocked(true);

        DeviceMaster master = DeviceMapper.getDeviceMaster(deviceMaster, deviceDto, userName);
        assertNotNull(master);
        assertEquals(master.getDeviceId(),deviceMaster.getDeviceId());
        assertEquals(master.getIsBlocked(), deviceDto.getIsBlocked());
    }

    @Test
    public void testGetDeviceWithUser(){
        DeviceMaster master = new DeviceMaster();

        master.setAndroidVersion("v1");
        master.setImeiNumber("imei1");
        master.setMacAddress("add1");
        master.setDeviceModel("model1");
        master.setDeviceName("device1");
        master.setDeviceUUID("uuid1");
        master.setDeviceOs("deviceOs1");
        master.setIsBlocked(true);
        master.setIgVersion("v1");
        master.setDeviceId(1);
        master.setLastLoggedInUser("user1");

        MultiLocationUserListDto user = new MultiLocationUserListDto();
        user.setUserName("user1");

        DeviceWithUserDetails deviceWithUserDetails = DeviceMapper.getDeviceWithUser(master, user);

        assertNotNull(deviceWithUserDetails);
        assertEquals(master.getDeviceOs(),deviceWithUserDetails.getDeviceOs());
        assertEquals(user,deviceWithUserDetails.getUsersResource());
    }
}
