package com.argusoft.who.emcare.web.adminSetting.repository;

import com.argusoft.who.emcare.web.adminSetting.Entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminSettingRepository extends JpaRepository<Settings, Long> {

    Settings findBySettingType(String settingType);
}
