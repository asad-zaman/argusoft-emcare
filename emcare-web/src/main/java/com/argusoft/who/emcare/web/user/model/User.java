package com.argusoft.who.emcare.web.user.model;


import com.argusoft.who.emcare.web.common.model.EntityAuditInfo;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_master")
public class User extends EntityAuditInfo implements Serializable {

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Basic(optional = false)
    @Column(name = "location_id", nullable = false)
    private Integer locationId;

    @Basic(optional = false)
    @Column(name = "reg_request_from", nullable = false)
    private String regRequestFrom;

    @Basic(optional = false)
    @Column(name = "reg_status", nullable = false)
    private String regStatus;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getRegRequestFrom() {
        return regRequestFrom;
    }

    public void setRegRequestFrom(String regRequestFrom) {
        this.regRequestFrom = regRequestFrom;
    }

    public String getRegStatus() {
        return regStatus;
    }

    public void setRegStatus(String regStatus) {
        this.regStatus = regStatus;
    }
}
