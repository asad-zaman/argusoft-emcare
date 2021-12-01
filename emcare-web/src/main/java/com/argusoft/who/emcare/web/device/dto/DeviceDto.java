package com.argusoft.who.emcare.web.device.dto;

import lombok.Data;

/**
 *
 * @author jay
 */
@Data
public class DeviceDto {

    private Integer deviceId;
    
    private String androidVersion;

    private String imeiNumber;

    private String macAddress;

    private String userId;

    private Boolean isBlocked;

}
