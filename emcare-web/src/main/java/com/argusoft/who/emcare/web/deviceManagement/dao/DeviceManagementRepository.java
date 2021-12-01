package com.argusoft.who.emcare.web.deviceManagement.dao;

import com.argusoft.who.emcare.web.deviceManagement.model.DeviceManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceManagementRepository extends JpaRepository<DeviceManagement, Long> {
    
}
