package com.argusoft.who.emcare.web.device.model;

import com.argusoft.who.emcare.web.common.model.EntityAuditInfo;
import java.io.Serializable;
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
@Table(name = "device_master")
public class DeviceMaster extends EntityAuditInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "device_id", nullable = false)
    private Integer deviceId;

    @Column(name = "android_version", nullable = false)
    private String androidVersion;

    @Column(name = "imei_number", nullable = false, unique = true)
    private String imeiNumber;

    @Column(name = "mac_address", nullable = false)
    private String macAddress;

    @Column(name = "userId", nullable = false)
    private String userId;

    @Column(name = "is_blocked", nullable = false)
    private Boolean isBlocked;

    public static class Fields {

        public static final Integer deviceId = 1;
        public static final String androidVersion = "12";
        public static final String imeiNumber = "1234567989002";
        public static final String macAddress = "12:fb:4f:rt:cd";
        public static final String userId = "df29542a-2bee-4ab4-a2ed-a32c68e9cbd0";
        public static final Boolean isBlocked = false;
    }

}
