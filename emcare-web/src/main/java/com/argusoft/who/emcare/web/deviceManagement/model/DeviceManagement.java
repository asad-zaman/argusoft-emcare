package com.argusoft.who.emcare.web.deviceManagement.model;

import java.util.Date;
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
    
    @Column(name = "created_by")
    private long createdBy;
    
    @Column(name = "created_at")
    private Date createdAt;
    
    @Column(name = "updated_by")
    private long updatedBy;
    
    @Column(name = "updated_at")
    private Date updatedAt;

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
    
    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}
