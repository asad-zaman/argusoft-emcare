package com.argusoft.who.emcare.web.device.mapper;

import com.argusoft.who.emcare.web.device.dto.DeviceDto;
import com.argusoft.who.emcare.web.device.dto.DeviceWithUserDetails;
import com.argusoft.who.emcare.web.device.model.DeviceMaster;
import java.util.Date;
import org.keycloak.admin.client.resource.UsersResource;

/**
 *
 * @author jay
 */
public class DeviceMapper {

    public static DeviceMaster dtoToEntityDeviceMasterCreate(DeviceDto deviceDto, String userId) {
        DeviceMaster master = new DeviceMaster();

        master.setAndroidVersion(deviceDto.getAndroidVersion());
        master.setImeiNumber(deviceDto.getImeiNumber());
        master.setMacAddress(deviceDto.getMacAddress());
        master.setLastLoggedInUser(userId);
        master.setIsBlocked(deviceDto.getIsBlocked());
        master.setCreatedBy(userId);
        master.setCreatedOn(new Date());

        return master;
    }

    public static DeviceMaster dtoToEntityDeviceMasterUpdate(DeviceMaster deviceMaster, DeviceDto deviceDto, String userId) {
        DeviceMaster master = new DeviceMaster();

        master.setDeviceId(deviceMaster.getDeviceId());
        master.setAndroidVersion(deviceDto.getAndroidVersion());
        master.setImeiNumber(deviceMaster.getImeiNumber());
        master.setMacAddress(deviceDto.getMacAddress());
        master.setLastLoggedInUser(userId);
        master.setIsBlocked(deviceDto.getIsBlocked());
        master.setCreatedBy(deviceMaster.getCreatedBy());
        master.setCreatedOn(deviceMaster.getCreatedOn());
        master.setModifiedOn(new Date());
        master.setModifiedBy(userId);

        return master;
    }

    public static DeviceWithUserDetails entityToDtoDeviceWithUser(DeviceMaster deviceMaster, UsersResource usersResource) {
        DeviceWithUserDetails deviceWithUserDetails = new DeviceWithUserDetails();

        deviceWithUserDetails.setDeviceId(deviceMaster.getDeviceId());
        deviceWithUserDetails.setAndroidVersion(deviceMaster.getAndroidVersion());
        deviceWithUserDetails.setImeiNumber(deviceMaster.getImeiNumber());
        deviceWithUserDetails.setMacAddress(deviceMaster.getMacAddress());
        deviceWithUserDetails.setLastLoggedInUser(deviceMaster.getLastLoggedInUser());
        deviceWithUserDetails.setIsBlocked(deviceMaster.getIsBlocked());
        deviceWithUserDetails.setCreatedBy(deviceMaster.getCreatedBy());
        deviceWithUserDetails.setCreatedOn(deviceMaster.getCreatedOn());
        deviceWithUserDetails.setModifiedOn(deviceMaster.getModifiedOn());
        deviceWithUserDetails.setModifiedBy(deviceMaster.getModifiedBy());
        deviceWithUserDetails.setUsersResource(usersResource.get(deviceMaster.getLastLoggedInUser()).toRepresentation());

        return deviceWithUserDetails;
    }

}
