package com.argusoft.who.emcare.web.userLocationMapping.model;

import com.argusoft.who.emcare.web.common.model.EntityAuditInfo;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_location_mapping")
public class UserLocationMapping extends EntityAuditInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "location_id", nullable = false)
    private Integer locationId;
    
    @Basic(optional = false)
    @Column(name = "state", nullable = false)
    private boolean state;

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
    
}
