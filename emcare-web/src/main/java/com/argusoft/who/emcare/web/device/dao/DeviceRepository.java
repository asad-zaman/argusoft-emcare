package com.argusoft.who.emcare.web.device.dao;

import com.argusoft.who.emcare.web.device.model.DeviceMaster;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author jay
 */
public interface DeviceRepository extends JpaRepository<DeviceMaster, Integer> {

    @Query(value = "select * from device_master where imei_number = :imei", nativeQuery = true)
    public DeviceMaster getDeviceByImei(@Param("imei") String imei);

    @Query(value = "select * from device_master where user_id = :userId", nativeQuery = true)
    public DeviceMaster getDeviceByuserId(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query(value = "update device_master set android_version = :android_version,"
            + " user_id= :user_id, is_blocked = :is_blocked where device_id = :device_id",
            nativeQuery = true)
    public void updateDevice(
            @Param("android_version") String android_version,
            @Param("user_id") String user_id,
            @Param("is_blocked") Boolean is_blocked,
            @Param("device_id") Integer device_id
    );
}
