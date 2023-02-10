package com.argusoft.who.emcare.web.adminsetting.repository;

import com.argusoft.who.emcare.web.adminsetting.entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminSettingRepository extends JpaRepository<Settings, Long> {

    Settings findByKey(String settingType);

    @Query(value = "select * from settings order by id ASC;", nativeQuery = true)
    List<Settings> findAllWithOrderById();
}
