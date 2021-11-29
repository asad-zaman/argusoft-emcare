package com.argusoft.who.emcare.web.location.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author jay
 */
@Data
@Entity
@Table(name = "hierarchy_master")
public class HierarchyMaster implements Serializable {

    @Id
    @Column(name = "hierarchy_type", nullable = false)
    private String hierarchyType;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "code", nullable = false)
    private String code;

    public static class Fields {

        public static final String HIERARCHY_TYPE = "hierarchyType";
        public static final String NAME = "name";
        public static final String CODE = "code";
    }
}
