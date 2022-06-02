package com.argusoft.who.emcare.web.userLocationMapping.dao;

import com.argusoft.who.emcare.web.dashboard.dto.DashboardDto;
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
            "where ulm.user_id is not null offset :pageNo * :pageSize limit :pageSize", nativeQuery = true)
    public List<String> getAllUserOnChildLocationsWithPage(@Param("id") Integer id, @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);

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

    UserLocationMapping findByUserIdAndLocationId(String userId, Integer locationId);

    @Query(value = "WITH TOTAL_USER AS\n" +
            " (SELECT COUNT(DISTINCT USER_ID) AS \"totalUser\"\n" +
            "  FROM USER_LOCATION_MAPPING),\n" +
            " PENDING_REQUEST AS\n" +
            " (SELECT COUNT(DISTINCT USER_ID) AS \"pendingRequest\"\n" +
            "  FROM USER_LOCATION_MAPPING\n" +
            "  WHERE IS_FIRST = TRUE),\n" +
            " LAST_SEVEN_DAY_REQUEST AS\n" +
            "(SELECT COUNT (DISTINCT ID) AS \"totalPatient\"\n" +
            " FROM emcare_resources where type = 'PATIENT')\n" +
            "SELECT *\n" +
            "FROM TOTAL_USER,\n" +
            " PENDING_REQUEST,\n" +
            " LAST_SEVEN_DAY_REQUEST;", nativeQuery = true)
    DashboardDto getDashboardData();

}
