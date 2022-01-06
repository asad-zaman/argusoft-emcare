package com.argusoft.who.emcare.web.userLocationMapping.dao;

import com.argusoft.who.emcare.web.userLocationMapping.model.UserLocationMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserLocationMappingRepository extends JpaRepository<UserLocationMapping, Integer> {

    List<UserLocationMapping> findByUserId(String userId);

    @Query(value = "WITH RECURSIVE child AS \n" +
            "(SELECT * FROM location_master WHERE parent = :id or id = :id \n" +
            "UNION SELECT l.* FROM location_master l\n" +
            "INNER JOIN child s ON s.id = l.parent)\n" +
            "SELECT ulm.user_id FROM child as ch \n" +
            "left join user_location_mapping as ulm  on ch.id = ulm.location_id\n" +
            "where ulm.user_id is not null", nativeQuery = true)
    public List<String> getAllUserOnChildLocations(@Param("id") Integer id);


    List<UserLocationMapping> findByRegRequestFromAndIsFirst(String regRequestFrom, boolean isFirst);

    List<UserLocationMapping> findByIsFirst(boolean isFirst);

}
