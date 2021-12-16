package com.argusoft.who.emcare.web.device.dto;

import java.util.Date;
import lombok.Data;
import org.keycloak.representations.idm.UserRepresentation;

/**
 *
 * @author jay
 */
@Data
public class DeviceWithUserDetails {

    private Integer deviceId;
    private String androidVersion;
    private String imeiNumber;
    private String macAddress;
    private String lastLoggedInUser;
    private Boolean isBlocked;
    private UserRepresentation usersResource;
    private String createdBy;
    private Date createdOn;
    private String modifiedBy;
    private Date modifiedOn;

}
