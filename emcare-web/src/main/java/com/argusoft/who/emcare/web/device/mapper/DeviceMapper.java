package com.argusoft.who.emcare.web.device.mapper;

import com.argusoft.who.emcare.web.device.dto.DeviceDto;
import com.argusoft.who.emcare.web.device.dto.DeviceWithUserDetails;
import com.argusoft.who.emcare.web.device.model.DeviceMaster;
import com.argusoft.who.emcare.web.user.dto.MultiLocationUserListDto;

/**
 * @author jay
 */
public class DeviceMapper {
    private DeviceMapper() {
    }

    public static DeviceMaster getDeviceMatserFromDto(DeviceDto deviceDto, String userId, String userName) {
        DeviceMaster master = new DeviceMaster();

        master.setAndroidVersion(deviceDto.getAndroidVersion());
        master.setImeiNumber(deviceDto.getImeiNumber());
        master.setMacAddress(deviceDto.getMacAddress());
        master.setLastLoggedInUser(userId);
        master.setDeviceModel(deviceDto.getDeviceModel());
        master.setDeviceName(deviceDto.getDeviceName());
        master.setDeviceUUID(deviceDto.getDeviceUUID());
        master.setDeviceOs(deviceDto.getDeviceOs());
        master.setIsBlocked(deviceDto.getIsBlocked());
        master.setUserName(userName);

        return master;
    }

    public static DeviceMaster getDeviceMaster(DeviceMaster deviceMaster, DeviceDto deviceDto, String userName) {
        DeviceMaster master = new DeviceMaster();

        master.setDeviceId(deviceMaster.getDeviceId());
        master.setAndroidVersion(deviceDto.getAndroidVersion());
        master.setImeiNumber(deviceMaster.getImeiNumber());
        master.setMacAddress(deviceDto.getMacAddress());
        master.setLastLoggedInUser(deviceMaster.getLastLoggedInUser());
        master.setUserName(userName);
        master.setIsBlocked(deviceDto.getIsBlocked());

        return master;
    }

    public static DeviceWithUserDetails getDeviceWithUser(DeviceMaster deviceMaster, MultiLocationUserListDto user) {
        DeviceWithUserDetails deviceWithUserDetails = new DeviceWithUserDetails();

        deviceWithUserDetails.setDeviceId(deviceMaster.getDeviceId());
        deviceWithUserDetails.setAndroidVersion(deviceMaster.getAndroidVersion());
        deviceWithUserDetails.setImeiNumber(deviceMaster.getImeiNumber());
        deviceWithUserDetails.setMacAddress(deviceMaster.getMacAddress());
        deviceWithUserDetails.setLastLoggedInUser(deviceMaster.getLastLoggedInUser());
        deviceWithUserDetails.setIsBlocked(deviceMaster.getIsBlocked());
        deviceWithUserDetails.setDeviceOs(deviceMaster.getDeviceOs());
        deviceWithUserDetails.setDeviceUUID(deviceMaster.getDeviceUUID());
        deviceWithUserDetails.setDeviceModel(deviceMaster.getDeviceModel());
        deviceWithUserDetails.setDeviceName(deviceMaster.getDeviceName());
        deviceWithUserDetails.setUsersResource(user);
        return deviceWithUserDetails;
    }

}
