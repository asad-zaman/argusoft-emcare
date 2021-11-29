package com.argusoft.who.emcare.web.location.dto;

import lombok.Data;

/**
 *
 * @author jay
 */
@Data
public class LocationMasterDto {

    private Integer id;
    private String name;
    private String type;
    private Long parent;
}
