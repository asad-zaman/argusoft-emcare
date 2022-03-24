package com.argusoft.who.emcare.web.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author jay
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
        value = {"createdOn", "createdBy", "modifiedOn", "modifiedBy"},
        allowGetters = true
)
public class EntityAuditInfo {

    @CreatedBy
    @Basic(optional = false)
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;
    @CreatedDate
    @Basic(optional = false)
    @Column(name = "created_on", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;
    @LastModifiedBy
    @Column(name = "modified_by")
    private String modifiedBy;
    @LastModifiedDate
    @Column(name = "modified_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedOn;

    public static class Fields {

        private Fields() {
        }

        public static final String CREATED_BY = "createdBy";
        public static final String CREATED_ON = "createdOn";
        public static final String MODIFIED_BY = "modifiedBy";
        public static final String MODIFIED_ON = "modifiedOn";

    }

}
