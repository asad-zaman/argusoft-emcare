package com.argusoft.who.emcare.web.location.dto;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author jay
 */
@Data
public class LocationaListDto {

    private Integer id;
    private String name;
    private String type;
    private boolean isActive;
    private Long parent;
    private String parentName;
    private String createdBy;
    private Date createdOn;
    private String modifiedBy;
    private Date modifiedOn;

}
