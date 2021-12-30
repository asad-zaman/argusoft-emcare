package com.argusoft.who.emcare.web.userLocationMapping.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_location_mapping")
public class UserLocationMapping implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "location_id", nullable = false)
    private Integer locationId;

    @Column(name = "reg_request_from", nullable = false)
    private String regRequestFrom;

    @Basic(optional = false)
    @Column(name = "state", nullable = false)
    private boolean state;
    
    @Basic(optional = false)
    @Column(name = "is_first", nullable = false)
    private boolean isFirst;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getRegRequestFrom() {
        return regRequestFrom;
    }

    public void setRegRequestFrom(String regRequestFrom) {
        this.regRequestFrom = regRequestFrom;
    }

    public boolean isIsFirst() {
        return isFirst;
    }

    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }
    
}
