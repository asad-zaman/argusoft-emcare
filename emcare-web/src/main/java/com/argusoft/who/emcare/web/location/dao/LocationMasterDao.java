package com.argusoft.who.emcare.web.location.dao;

import com.argusoft.who.emcare.web.location.model.LocationMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author jay
 */
public interface LocationMasterDao extends JpaRepository<LocationMaster, Integer> {

    @Query(value = "select * from location_master where parent = :id", nativeQuery = true)
    public List<LocationMaster> getChildLocation(@Param("id") Integer id);

    List<LocationMaster> findByType(String locationType);

    List<LocationMaster> findByParent(Long locationId);
}
