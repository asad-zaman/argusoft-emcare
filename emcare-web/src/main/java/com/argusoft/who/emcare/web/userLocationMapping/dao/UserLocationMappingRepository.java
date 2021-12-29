package com.argusoft.who.emcare.web.userLocationMapping.dao;

import com.argusoft.who.emcare.web.userLocationMapping.model.UserLocationMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserLocationMappingRepository extends JpaRepository<UserLocationMapping, Integer> {

    List<UserLocationMapping> findByUserId(String userId);

    @Query(value = "WITH RECURSIVE child AS \n" +
            "(SELECT * FROM location_master WHERE id = :id \n" +
            "UNION SELECT l.* FROM location_master l \n" +
            "INNER JOIN child s ON s.id = l.parent)\n" +
            "SELECT ulm.user_id FROM user_location_mapping as ulm \n" +
            "left join child as ch on ulm.location_id = ch.id", nativeQuery = true)
    public List<String> getAllUserOnChildLocations(@Param("id") Integer id);


}
