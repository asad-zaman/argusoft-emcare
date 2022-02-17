package com.argusoft.who.emcare.web.device.model;

import com.argusoft.who.emcare.web.common.model.EntityAuditInfo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author jay
 */
@Entity
@Table(name = "device_master")
public class DeviceMaster extends EntityAuditInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "device_id", nullable = false)
    private Integer deviceId;

    @Column(name = "android_version", nullable = false)
    private String androidVersion;

    @Column(name = "imei_number")
    private String imeiNumber;

    @Column(name = "device_uuid", nullable = false)
    private String deviceUUID;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @Column(name = "device_os", nullable = false)
    private String deviceOs;

    @Column(name = "device_model", nullable = false)
    private String deviceModel;

    @Column(name = "mac_address")
    private String macAddress;

    @Column(name = "last_logged_in_user", nullable = false)
    private String lastLoggedInUser;

    @Column(name = "is_blocked", nullable = false)
    private Boolean isBlocked;

    @Column(name = "user_name")
    private String userName;

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

    public String getDeviceUUID() {
        return deviceUUID;
    }

    public void setDeviceUUID(String deviceUUID) {
        this.deviceUUID = deviceUUID;
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

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
