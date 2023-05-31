package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface EmcareResourceRepository extends JpaRepository<EmcareResource, Integer> {

    List<EmcareResource> findAllByType(String type);

    List<EmcareResource> findAllByType(String type, Pageable pageable);

    List<EmcareResource> findByTypeContainingAndTextContainingIgnoreCase(String type, String searchString, Pageable pageable);

    List<EmcareResource> findByTypeContainingAndTextContainingIgnoreCase(String type, String searchString);

    List<EmcareResource> findByTypeContainingAndTextContainingIgnoreCaseOrderByCreatedOnDesc(String type, String searchString);

    @Query(value = "SELECT * FROM EMCARE_RESOURCES WHERE TYPE = :type AND (CREATED_ON > :date OR MODIFIED_ON > :date)", nativeQuery = true)
    List<EmcareResource> getByDateAndType(@Param("date") Date date, @Param("type") String type);

    List<EmcareResource> findByTypeAndModifiedOnGreaterThanOrCreatedOnGreaterThanAndFacilityIdIn(String type, Date modifiedOn, Date createdOn, List<String> ids);

    EmcareResource findByResourceId(String resourceId);

    List<EmcareResource> findByFacilityIdIn(List<String> ids, Pageable pageable);

    List<EmcareResource> findByFacilityIdIn(List<String> ids);

    List<EmcareResource> findByResourceIdIn(List<String> ids);

    @Query(value = "SELECT COUNT(*) FROM EMCARE_RESOURCES WHERE (CREATED_ON > :date OR MODIFIED_ON > :date) AND TYPE = 'PATIENT'", nativeQuery = true)
    Long getCountBasedOnDate(@Param("date") Date date);

    @Query(value = "SELECT COUNT(*) FROM EMCARE_RESOURCES WHERE (CREATED_ON > :date OR MODIFIED_ON > :date) AND TYPE = 'PATIENT' AND facility_id in :ids", nativeQuery = true)
    Long getCountBasedOnDateWithFacilityId(@Param("date") Date date, @Param("ids") List<String> ids);

    @Query(value = "SELECT COUNT(*) FROM EMCARE_RESOURCES WHERE TYPE = 'PATIENT'", nativeQuery = true)
    Long getCount();

    @Query(value = "SELECT COUNT(*) FROM EMCARE_RESOURCES WHERE TYPE = 'PATIENT' AND facility_id in :ids", nativeQuery = true)
    Long getCountWithFacilityId(@Param("ids") List<String> ids);

    @Query(value = "with t1 as (select to_date(birth_date,'yyyy-mm-dd') as birth_date from emcare_resources er),\n" +
            "t2 as (select birth_date,(extract(year from age(birth_date) * 12) + extract(month from age(birth_date))) age_in_months from t1)\n" +
            "select '0 to 2 Months' as key,sum(case when age_in_months <= 2 then 1 else 0 end) value from t2\n" +
            "union select '3 to 59 Months' as key,sum(case when age_in_months between 3 and 59 then 1 else 0 end) value from t2", nativeQuery = true)
    List<Map<String,Object>> getPieChartDataBasedOnAgeGroup();



}