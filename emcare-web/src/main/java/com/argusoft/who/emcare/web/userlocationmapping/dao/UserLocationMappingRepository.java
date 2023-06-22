package com.argusoft.who.emcare.web.userlocationmapping.dao;

import com.argusoft.who.emcare.web.dashboard.dto.ChartDto;
import com.argusoft.who.emcare.web.dashboard.dto.DashboardDto;
import com.argusoft.who.emcare.web.dashboard.dto.ScatterCharDto;
import com.argusoft.who.emcare.web.userlocationmapping.model.UserLocationMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface UserLocationMappingRepository extends JpaRepository<UserLocationMapping, Integer> {

    List<UserLocationMapping> findByUserId(String userId);

    @Query(value = "WITH RECURSIVE CHILD AS\n" +
        " (SELECT *\n" +
        "   FROM LOCATION_MASTER\n" +
        "   WHERE PARENT = :id\n" +
        "       OR ID = :id\n" +
        "   UNION SELECT L.*\n" +
        "  FROM LOCATION_MASTER L\n" +
        "       INNER JOIN CHILD S ON S.ID = L.PARENT)\n" +
        "SELECT DISTINCT ULM.USER_ID\n" +
        "FROM CHILD AS CH\n" +
        "LEFT JOIN LOCATION_RESOURCES AS LR ON CH.ID = LR.LOCATION_ID\n" +
        "LEFT JOIN USER_LOCATION_MAPPING AS ULM ON LR.RESOURCE_ID = ULM.FACILITY_ID\n" +
        "WHERE ULM.USER_ID IS NOT NULL\n" +
        "   AND ULM.FACILITY_ID IS NOT NULL\n" +
        "   AND ULM.STATE = :filter offset :pageNo * :pageSize limit :pageSize", nativeQuery = true)
    public List<String> getAllUserOnChildLocationsWithPage(@Param("id") Integer id, @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize, Boolean filter);

    @Query(value = "WITH RECURSIVE CHILD AS\n" +
        " (SELECT *\n" +
        "   FROM LOCATION_MASTER\n" +
        "   WHERE PARENT = :id\n" +
        "       OR ID = :id\n" +
        "   UNION SELECT L.*\n" +
        "  FROM LOCATION_MASTER L\n" +
        "       INNER JOIN CHILD S ON S.ID = L.PARENT)\n" +
        "SELECT DISTINCT ULM.USER_ID\n" +
        "FROM CHILD AS CH\n" +
        "LEFT JOIN LOCATION_RESOURCES AS LR ON CH.ID = LR.LOCATION_ID\n" +
        "LEFT JOIN USER_LOCATION_MAPPING AS ULM ON LR.RESOURCE_ID = ULM.FACILITY_ID\n" +
        "WHERE ULM.USER_ID IS NOT NULL\n" +
        "   AND ULM.FACILITY_ID IS NOT NULL\n" +
        "   AND ULM.STATE = :filter", nativeQuery = true)
    public List<String> getAllUserOnChildLocations(@Param("id") Integer id, Boolean filter);


    @Query(value = "SELECT USER_ID\n" +
        "FROM USER_LOCATION_MAPPING\n" +
        "WHERE STATE = :filter \n" +
        "AND FACILITY_ID IN :facilityIds \n" +
        "GROUP BY USER_ID\n" +
        "ORDER BY MAX(CREATE_DATE) DESC\n" +
        "LIMIT 10\n" +
        "OFFSET :offset ;", nativeQuery = true)
    public List<String> getAllUserBasedOnFacilityId(@Param("facilityIds") List<String> facilityIds,
                                                    @Param("filter") Boolean filter,
                                                    @Param("offset") Integer offset);

    @Query(value = "SELECT USER_ID\n" +
        "FROM USER_LOCATION_MAPPING\n" +
        "WHERE STATE = :filter \n" +
        "\tAND FACILITY_ID IN :facilityIds \n" +
        "GROUP BY USER_ID\n" +
        "ORDER BY MAX(CREATE_DATE) DESC;", nativeQuery = true)
    public List<String> getAllUserBasedOnFacilityIdCount(@Param("facilityIds") List<String> facilityIds,
                                                         @Param("filter") Boolean filter);

    @Query(value = "select ulm.user_id  from user_location_mapping ulm left join user_entity ue on ulm.user_id = ue.id \n" +
            "where ulm.facility_id in :facilityIds and (\n" +
            "ue.first_name ilike concat('%',:searchString,'%') or\n" +
            "ue.last_name  ilike concat('%',:searchString,'%') or\n" +
            "ue.email ilike concat('%',:searchString,'%')\n" +
            ") and ulm.state = :filter\n" +
            "group by ulm.user_id \n" +
            "order by max(ulm.create_date) desc",nativeQuery = true)
    public List<String> getAllUserBasedOnFacilityAndSearchCount(@Param("facilityIds") List<String> facilityIds,
                                                                @Param("searchString") String searchString,
                                                                @Param("filter") Boolean filter);

    @Query(value = "select ulm.user_id  from user_location_mapping ulm left join user_entity ue on ulm.user_id = ue.id \n" +
            "where ulm.facility_id in :facilityIds and (\n" +
            "ue.first_name ilike concat('%',:searchString,'%') or\n" +
            "ue.last_name  ilike concat('%',:searchString,'%') or\n" +
            "ue.email ilike concat('%',:searchString,'%')\n" +
            ") and ulm.state = :filter\n" +
            "group by ulm.user_id \n" +
            "order by max(ulm.create_date) desc limit 10 offset :offset",nativeQuery = true)
    public List<String> getAllUserBasedOnFacilityAndSearch(@Param("facilityIds") List<String> facilityIds,
                                                    @Param("searchString") String searchString,
                                                    @Param("filter") Boolean filter,
                                                    @Param("offset") Integer offset);


    List<UserLocationMapping> findByRegRequestFromAndIsFirst(String regRequestFrom, boolean isFirst);

    List<UserLocationMapping> findByIsFirstOrderByCreateDateDesc(boolean isFirst);

    UserLocationMapping findByUserIdAndLocationId(String userId, Integer locationId);

    UserLocationMapping findByUserIdAndFacilityId(String userId, String facilityId);

    @Query(value = "WITH TOTAL_USER AS\n" +
        " (SELECT COUNT(DISTINCT USER_ID) AS \"totalUser\"\n" +
        "  FROM USER_LOCATION_MAPPING),\n" +
        " PENDING_REQUEST AS\n" +
        " (SELECT COUNT(*) AS \"pendingRequest\"\n" +
        "  FROM USER_LOCATION_MAPPING\n" +
        "  WHERE IS_FIRST = TRUE),\n" +
        " LAST_SEVEN_DAY_REQUEST AS\n" +
        "(SELECT COUNT (DISTINCT ID) AS \"totalPatient\"\n" +
        " FROM emcare_resources where type = 'PATIENT' and created_on > '2023-05-31')\n" +
        "SELECT *\n" +
        "FROM TOTAL_USER,\n" +
        " PENDING_REQUEST,\n" +
        " LAST_SEVEN_DAY_REQUEST;", nativeQuery = true)
    DashboardDto getDashboardData();

    @Query(value = "SELECT FACILITY_ID AS \"facilityId\", \n" +
        " COUNT(*) AS \"count\"\n" +
        "FROM USER_LOCATION_MAPPING\n" +
        "WHERE FACILITY_ID IS NOT NULL\n" +
        "GROUP BY FACILITY_ID;", nativeQuery = true)
    List<ChartDto> getDashboardBarChartData();

    @Query(value = "SELECT FACILITY_ID AS \"facilityId\", \n" +
        " COUNT(*) AS \"count\"\n" +
        "FROM EMCARE_RESOURCES\n" +
        "WHERE FACILITY_ID IS NOT NULL AND TYPE = 'PATIENT'\n" +
        "GROUP BY FACILITY_ID;", nativeQuery = true)
    List<ChartDto> getDashboardPieChartData();

    @Query(value = "SELECT all_dates.date AS day, COALESCE(counts.count, 0) AS count\n" +
        "        FROM (\n" +
        "            SELECT cast(generate_series(CURRENT_DATE - INTERVAL '14 days', CURRENT_DATE, INTERVAL '1 day') as date) AS date\n" +
        "        ) AS all_dates\n" +
        "        LEFT JOIN (\n" +
        "            SELECT cast(consultation_date as date) AS day, COUNT(distinct(encounter_id)) AS count\n" +
        "            FROM questionnaire_response qr\n" +
        "            WHERE consultation_date >= CURRENT_DATE - INTERVAL '14 days'\n" +
        "            GROUP BY cast(consultation_date as date)\n" +
        "        ) AS counts ON all_dates.date = counts.day\n" +
        "        ORDER BY all_dates.date;", nativeQuery = true)
    List<ScatterCharDto> getDashboardScatterChartData(
        @Param("currentWeekNumber") Integer currentWeekNumber);

    @Query(value = "select distinct(user_id) from user_location_mapping;", nativeQuery = true)
    List<String> getDistinctUserId();

    @Query(value = "select distinct(user_id) from user_location_mapping where state= :filter ", nativeQuery = true)
    List<String> getDistinctUserIdUsingFilter(@Param("filter") Boolean filter);

    @Query(value = "select * from keycloak.user_attribute where user_id= :userId and name = 'tenantID' ;", nativeQuery = true)
    Map<String, Object> getUserTenantNameFromKeyCloak(@Param("userId") String userId);

    @Query(value = "SELECT ULM.USER_ID\n" +
        "FROM USER_LOCATION_MAPPING ULM\n" +
        "LEFT JOIN USER_ENTITY UE ON ULM.USER_ID = UE.ID\n" +
        "WHERE (UE.FIRST_NAME ilike %:searchString%\n" +
        "\tOR UE.LAST_NAME ilike %:searchString%\n" +
        "\tOR UE.USERNAME ilike %:searchString%\n" +
        "\tOR UE.EMAIL ilike %:searchString%\n" +
        "\tOR ULM.USER_ID ilike %:searchString%)\n" +
        "\tAND ULM.state = :state \n" +
        "GROUP BY ULM.USER_ID\n" +
        "ORDER BY MAX(ULM.CREATE_DATE)\n" +
        "LIMIT 10 offset :offset ;", nativeQuery = true)
    List<String> getUserPageDataWithSearch(@Param("searchString") String searchString, @Param("state") Boolean state, @Param("offset") Integer offset);

    @Query(value = "SELECT ULM.USER_ID\n" +
        "FROM USER_LOCATION_MAPPING ULM\n" +
        "LEFT JOIN USER_ENTITY UE ON ULM.USER_ID = UE.ID\n" +
        "WHERE (UE.FIRST_NAME ilike %:searchString%\n" +
        "\tOR UE.LAST_NAME ilike %:searchString%\n" +
        "\tOR UE.USERNAME ilike %:searchString%\n" +
        "\tOR UE.EMAIL ilike %:searchString%\n" +
        "\tOR ULM.USER_ID ilike %:searchString%)\n" +
        "\tAND ULM.state = :state \n" +
        "GROUP BY ULM.USER_ID\n" +
        "ORDER BY MAX(ULM.CREATE_DATE)", nativeQuery = true)
    List<String> getUserPageDataWithSearchCount(@Param("searchString") String searchString, @Param("state") Boolean state);


    @Query(value = "select ulm.user_id  from user_location_mapping ulm  left join user_entity ue  on ulm.user_id = ue.id where ulm.state = :state\n" +
        "group by ulm.user_id order by max(ulm.create_date)  limit 10 offset :offset", nativeQuery = true)
    List<String> getUserPageDataWithoutSearch(@Param("state") Boolean state, @Param("offset") Integer offset);

    @Query(value = "select ulm.user_id  from user_location_mapping ulm  left join user_entity ue  on ulm.user_id = ue.id where ulm.state = :state\n" +
        "group by ulm.user_id order by max(ulm.create_date)", nativeQuery = true)
    List<String> getUserPageDataWithoutSearchCount(@Param("state") Boolean state);

}
