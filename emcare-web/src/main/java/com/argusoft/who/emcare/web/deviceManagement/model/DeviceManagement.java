package com.argusoft.who.emcare.web.deviceManagement.model;

import javax.persistence.*;

@Entity
@Table(name = "device_management")
public class DeviceManagement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "imei_number")
    private String imeiNumber;

    @Column(name = "mac")
    private String macAddress;

    @Column(name = "last_logged_in_user")
    private String lastLoggedInUser;
    
    @Column(name = "android_version")
    private String androidVersion;
    
    @Column(name = "is_blocked")
    private String isBlocked;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getLastLoggedInUser() {
        return lastLoggedInUser;
    }

    public void setLastLoggedInUser(String lastLoggedInUser) {
        this.lastLoggedInUser = lastLoggedInUser;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(String isBlocked) {
        this.isBlocked = isBlocked;
    }

}
