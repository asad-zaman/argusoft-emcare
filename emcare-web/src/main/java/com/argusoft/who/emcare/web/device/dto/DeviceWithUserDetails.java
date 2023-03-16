package com.argusoft.who.emcare.web.device.dto;

import com.argusoft.who.emcare.web.user.dto.MultiLocationUserListDto;

/**
 * @author jay
 */
public class DeviceWithUserDetails {

    private Integer deviceId;
    private String androidVersion;
    private String imeiNumber;
    private String macAddress;
    private String deviceName;
    private String deviceOs;
    private String deviceUUID;
    private String deviceModel;
    private String lastLoggedInUser;
    private Boolean isBlocked;
    private String igVersion;
    private MultiLocationUserListDto usersResource;

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getImeiNumber() {
        return imeiNumber;
    }

    public void setImeiNumber(String imeiNumber) {
        this.imeiNumber = imeiNumber;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceOs() {
        return deviceOs;
    }

    public void setDeviceOs(String deviceOs) {
        this.deviceOs = deviceOs;
    }

    public String getDeviceUUID() {
        return deviceUUID;
    }

    public void setDeviceUUID(String deviceUUID) {
        this.deviceUUID = deviceUUID;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getLastLoggedInUser() {
        return lastLoggedInUser;
    }

    public void setLastLoggedInUser(String lastLoggedInUser) {
        this.lastLoggedInUser = lastLoggedInUser;
    }

    public Boolean getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(Boolean blocked) {
        isBlocked = blocked;
    }

    public MultiLocationUserListDto getUsersResource() {
        return usersResource;
    }

    public void setUsersResource(MultiLocationUserListDto usersResource) {
        this.usersResource = usersResource;
    }

    public String getIgVersion() {
        return igVersion;
    }

    public void setIgVersion(String igVersion) {
        this.igVersion = igVersion;
    }
}
