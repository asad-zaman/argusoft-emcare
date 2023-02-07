package com.argusoft.who.emcare.web.location.model;

import com.argusoft.who.emcare.web.common.model.EntityAuditInfo;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author jay
 */
@Data
@Entity
@Table(name = "location_master")
public class LocationMaster extends EntityAuditInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 4000)
    private String name;

    @Basic(optional = false)
    @Column(name = "type", nullable = false, length = 10)
    private String type;

    @Basic(optional = false)
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "parent")
    private Long parent;

}
