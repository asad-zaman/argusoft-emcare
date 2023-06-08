package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.dto.EmcareResourceDto;
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

    List<EmcareResource> findByTypeContainingAndTextContainingIgnoreCase(String type, String searchString,
                                                                         Pageable pageable);

    List<EmcareResource> findByTypeContainingAndTextContainingIgnoreCase(String type, String searchString);

    List<EmcareResource> findByTypeContainingAndTextContainingIgnoreCaseOrderByCreatedOnDesc(String type,
                                                                                             String searchString);

    @Query(value = "SELECT * FROM EMCARE_RESOURCES WHERE TYPE = :type AND (CREATED_ON > :date OR MODIFIED_ON > :date)", nativeQuery = true)
    List<EmcareResource> getByDateAndType(@Param("date") Date date, @Param("type") String type);

    List<EmcareResource> findByTypeAndModifiedOnGreaterThanOrCreatedOnGreaterThanAndFacilityIdIn(String type,
                                                                                                 Date modifiedOn, Date createdOn, List<String> ids);

    EmcareResource findByResourceId(String resourceId);

    List<EmcareResource> findByFacilityIdIn(List<String> ids, Pageable pageable);

    List<EmcareResource> findByFacilityIdIn(List<String> ids);

    List<EmcareResource> findByResourceIdIn(List<String> ids);

    @Query(value = "WITH MAX_CONSULTATION_DATE AS\n" +
        "(SELECT PATIENT_ID,\n" +
        "MAX(CONSULTATION_DATE) as cnslDate\n" +
        "FROM QUESTIONNAIRE_RESPONSE\n" +
        "GROUP BY PATIENT_ID)\n" +
        "SELECT count(*)\n" +
        "FROM EMCARE_RESOURCES\n" +
        "LEFT OUTER JOIN MAX_CONSULTATION_DATE ON EMCARE_RESOURCES.RESOURCE_ID = MAX_CONSULTATION_DATE.PATIENT_ID\n" +
        "LEFT JOIN LOCATION_RESOURCES ON EMCARE_RESOURCES.facility_id = LOCATION_RESOURCES.resource_id ", nativeQuery = true)
    int getCountOfPatients();

    @Query(value = "WITH MAX_CONSULTATION_DATE AS\n" +
        "(SELECT PATIENT_ID,\n" +
        "MAX(CONSULTATION_DATE) as cnslDate\n" +
        "FROM QUESTIONNAIRE_RESPONSE\n" +
        "GROUP BY PATIENT_ID)\n" +
        "SELECT count(*)\n" +
        "FROM EMCARE_RESOURCES\n" +
        "LEFT OUTER JOIN MAX_CONSULTATION_DATE ON EMCARE_RESOURCES.RESOURCE_ID = MAX_CONSULTATION_DATE.PATIENT_ID\n" +
        "LEFT JOIN LOCATION_RESOURCES ON EMCARE_RESOURCES.facility_id = LOCATION_RESOURCES.resource_id \n" +
        "where (cast((EMCARE_RESOURCES.text) as json) -> cast('identifier' as text) -> 0 ->> cast('value' as text) ilike concat('%',:searchString,'%') or\n" +
        "\t   CONCAT(cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 0, ' ', cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 1) ilike concat('%',:searchString,'%') or\n" +
        "\t   cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 ->> cast('family' as text) ilike concat('%',:searchString,'%') or\n" +
        "\t   cast((EMCARE_RESOURCES.text) as json) ->> cast('gender' as text) ilike concat('%',:searchString,'%') or\n" +
        "\t   cast((EMCARE_RESOURCES.text) as json) ->> cast('birthDate' as text) ilike concat('%',:searchString,'%') or\n" +
        "\t   cast((LOCATION_RESOURCES.text) as json) ->> cast('name' as text) ilike concat('%',:searchString,'%'))", nativeQuery = true)
    int getCountOfPatientsByTypeContainingAndTextContainingIgnoreCase(@Param("searchString") String searchString);

    @Query(value = "WITH MAX_CONSULTATION_DATE AS\n" +
        "(SELECT PATIENT_ID,\n" +
        "MAX(CONSULTATION_DATE) as cnslDate\n" +
        "FROM QUESTIONNAIRE_RESPONSE\n" +
        "GROUP BY PATIENT_ID)\n" +
        "SELECT\n" +
        "CONCAT('Patient',' ',row_number() over (ORDER BY emcare_resources.id)) as \"key\",\n" +
        "EMCARE_RESOURCES.resource_id, \n" +
        "cast((EMCARE_RESOURCES.text) as json) -> cast('identifier' as text) -> 0 ->> cast('value' as text) as \"identifier\",\n" +
        "CONCAT(cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 0, ' ', cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 1) as \"givenName\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 ->> cast('family' as text) as \"familyName\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) ->> cast('gender' as text) as \"gender\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) ->> cast('birthDate' as text) as \"birthDate\",\n" +
        "cast((LOCATION_RESOURCES.text) as json) ->> cast('name' as text) as \"facilityName\",\n" +
        "cast((LOCATION_RESOURCES.text) as json) -> cast('address' as text) -> cast('line' as text) ->> 0 as \"addressLine\",\n" +
        "(LOCATION_RESOURCES.organization_name) as \"organizationName\",\n" +
        "(LOCATION_RESOURCES.location_name) as \"locationName\",\n" +
        "MAX_CONSULTATION_DATE.cnslDate as \"consultationDate\"\n" +
        "FROM EMCARE_RESOURCES\n" +
        "LEFT OUTER JOIN MAX_CONSULTATION_DATE ON EMCARE_RESOURCES.RESOURCE_ID = MAX_CONSULTATION_DATE.PATIENT_ID\n" +
        "LEFT JOIN LOCATION_RESOURCES ON EMCARE_RESOURCES.facility_id = LOCATION_RESOURCES.resource_id \n" +
        "where (cast((EMCARE_RESOURCES.text) as json) -> cast('identifier' as text) -> 0 ->> cast('value' as text) ilike concat('%',:searchString,'%') or \n" +
        "\t   CONCAT(cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 0," +
        "\t   cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 1) ilike concat('%',:searchString,'%') or\n" +
        "\t   cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 ->> cast('family' as text) ilike concat('%',:searchString,'%') or\n" +
        "\t   cast((EMCARE_RESOURCES.text) as json) ->> cast('gender' as text) ilike concat('%',:searchString,'%') or\n" +
        "\t   cast((EMCARE_RESOURCES.text) as json) ->> cast('birthDate' as text) ilike concat('%',:searchString,'%') or\n" +
        "\t   cast((LOCATION_RESOURCES.text) as json) ->> cast('name' as text) ilike concat('%',:searchString,'%'))", nativeQuery = true)
    List<EmcareResourceDto> getPatientsByTypeContainingAndTextContainingIgnoreCase(@Param("searchString") String searchString, Pageable pageable);


    @Query(value = "WITH MAX_CONSULTATION_DATE AS\n" +
        "(SELECT PATIENT_ID,\n" +
        "MAX(CONSULTATION_DATE) as cnslDate\n" +
        "FROM QUESTIONNAIRE_RESPONSE\n" +
        "GROUP BY PATIENT_ID)\n" +
        "SELECT\n" +
        "CONCAT('Patient',' ',row_number() over (ORDER BY emcare_resources.id)) as \"key\",\n" +
        "EMCARE_RESOURCES.resource_id, \n" +
        "cast((EMCARE_RESOURCES.text) as json) -> cast('identifier' as text) -> 0 ->> cast('value' as text) as \"identifier\",\n" +
        "CONCAT(cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 0, ' ', cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 1) as \"givenName\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 ->> cast('family' as text) as \"familyName\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) ->> cast('gender' as text) as \"gender\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) ->> cast('birthDate' as text) as \"birthDate\",\n" +
        "cast((LOCATION_RESOURCES.text) as json) ->> cast('name' as text) as \"facilityName\",\n" +
        "cast((LOCATION_RESOURCES.text) as json) -> cast('address' as text) -> cast('line' as text) ->> 0 as \"addressLine\",\n" +
        "(LOCATION_RESOURCES.organization_name) as \"organizationName\",\n" +
        "(LOCATION_RESOURCES.location_name) as \"locationName\",\n" +
        "MAX_CONSULTATION_DATE.cnslDate as \"consultationDate\"\n" +
        "FROM EMCARE_RESOURCES\n" +
        "LEFT OUTER JOIN MAX_CONSULTATION_DATE ON EMCARE_RESOURCES.RESOURCE_ID = MAX_CONSULTATION_DATE.PATIENT_ID\n" +
        "LEFT JOIN LOCATION_RESOURCES ON EMCARE_RESOURCES.facility_id = LOCATION_RESOURCES.resource_id \n" +
        "where (cast((EMCARE_RESOURCES.text) as json) -> cast('identifier' as text) -> 0 ->> cast('value' as text) ilike concat('%',:searchString,'%') or \n" +
        "\t   CONCAT(cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 0," +
        "\t   cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 1) ilike concat('%',:searchString,'%') or\n" +
        "\t   cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 ->> cast('family' as text) ilike concat('%',:searchString,'%') or\n" +
        "\t   cast((EMCARE_RESOURCES.text) as json) ->> cast('gender' as text) ilike concat('%',:searchString,'%') or\n" +
        "\t   cast((EMCARE_RESOURCES.text) as json) ->> cast('birthDate' as text) ilike concat('%',:searchString,'%') or\n" +
        "\t   cast((LOCATION_RESOURCES.text) as json) ->> cast('name' as text) ilike concat('%',:searchString,'%'))", nativeQuery = true)
    List<EmcareResourceDto> getPatientForExportWithSearch(@Param("searchString") String searchString);


    @Query(value = "\n" +
        "WITH MAX_CONSULTATION_DATE AS\n" +
        "(SELECT PATIENT_ID,\n" +
        "MAX(CONSULTATION_DATE) as cnslDate\n" +
        "FROM QUESTIONNAIRE_RESPONSE\n" +
        "GROUP BY PATIENT_ID)\n" +
        "SELECT\n" +
        "CONCAT('Patient',' ',row_number() over (ORDER BY emcare_resources.id)) as \"key\",\n" +
        "EMCARE_RESOURCES.resource_id, \n" +
        "cast((EMCARE_RESOURCES.text) as json) -> cast('identifier' as text) -> 0 ->> cast('value' as text) as \"identifier\",\n" +
        "CONCAT(cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 0, ' ', cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 1) as \"givenName\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 ->> cast('family' as text) as \"familyName\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) ->> cast('gender' as text) as \"gender\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) ->> cast('birthDate' as text) as \"birthDate\",\n" +
        "cast((LOCATION_RESOURCES.text) as json) ->> cast('name' as text) as \"facilityName\",\n" +
        "cast((LOCATION_RESOURCES.text) as json) -> cast('address' as text) -> cast('line' as text) ->> 0 as \"addressLine\",\n" +
        "(LOCATION_RESOURCES.organization_name) as \"organizationName\",\n" +
        "(LOCATION_RESOURCES.location_name) as \"locationName\",\n" +
        "MAX_CONSULTATION_DATE.cnslDate as \"consultationDate\"\n" +
        "FROM EMCARE_RESOURCES\n" +
        "LEFT OUTER JOIN MAX_CONSULTATION_DATE ON EMCARE_RESOURCES.RESOURCE_ID = MAX_CONSULTATION_DATE.PATIENT_ID\n" +
        "LEFT JOIN LOCATION_RESOURCES ON EMCARE_RESOURCES.facility_id = LOCATION_RESOURCES.resource_id \n" +
        "where EMCARE_RESOURCES.FACILITY_ID in :ids and EMCARE_RESOURCES.modified_on between :startDate and :endDate ORDER BY EMCARE_RESOURCES.created_on DESC limit 10 offset :offset", nativeQuery = true)
    List<Map<String, Object>> getFilteredPatientsIn(@Param("ids") List<String> ids,
                                                    @Param("startDate") Date startDate,
                                                    @Param("endDate") Date endDate,
                                                    @Param("offset") Long offset);


    @Query(value = "\n" +
        "WITH MAX_CONSULTATION_DATE AS\n" +
        "(SELECT PATIENT_ID,\n" +
        "MAX(CONSULTATION_DATE) as cnslDate\n" +
        "FROM QUESTIONNAIRE_RESPONSE\n" +
        "GROUP BY PATIENT_ID)\n" +
        "SELECT\n" +
        "CONCAT('Patient',' ',row_number() over (ORDER BY emcare_resources.id)) as \"key\",\n" +
        "EMCARE_RESOURCES.resource_id, \n" +
        "cast((EMCARE_RESOURCES.text) as json) -> cast('identifier' as text) -> 0 ->> cast('value' as text) as \"identifier\",\n" +
        "CONCAT(cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 0, ' ', cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 1) as \"givenName\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 ->> cast('family' as text) as \"familyName\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) ->> cast('gender' as text) as \"gender\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) ->> cast('birthDate' as text) as \"birthDate\",\n" +
        "cast((LOCATION_RESOURCES.text) as json) ->> cast('name' as text) as \"facilityName\",\n" +
        "cast((LOCATION_RESOURCES.text) as json) -> cast('address' as text) -> cast('line' as text) ->> 0 as \"addressLine\",\n" +
        "(LOCATION_RESOURCES.organization_name) as \"organizationName\",\n" +
        "(LOCATION_RESOURCES.location_name) as \"locationName\",\n" +
        "MAX_CONSULTATION_DATE.cnslDate as \"consultationDate\"\n" +
        "FROM EMCARE_RESOURCES\n" +
        "LEFT OUTER JOIN MAX_CONSULTATION_DATE ON EMCARE_RESOURCES.RESOURCE_ID = MAX_CONSULTATION_DATE.PATIENT_ID\n" +
        "LEFT JOIN LOCATION_RESOURCES ON EMCARE_RESOURCES.facility_id = LOCATION_RESOURCES.resource_id \n" +
        "where EMCARE_RESOURCES.FACILITY_ID in :ids and EMCARE_RESOURCES.modified_on between :startDate and :endDate ORDER BY EMCARE_RESOURCES.created_on DESC", nativeQuery = true)
    List<Map<String, Object>> getFilteredPatientsInCount(@Param("ids") List<String> ids,
                                                         @Param("startDate") Date startDate,
                                                         @Param("endDate") Date endDate);

    @Query(value = "\n" +
        "WITH MAX_CONSULTATION_DATE AS\n" +
        "(SELECT PATIENT_ID,\n" +
        "MAX(CONSULTATION_DATE) as cnslDate\n" +
        "FROM QUESTIONNAIRE_RESPONSE\n" +
        "GROUP BY PATIENT_ID)\n" +
        "SELECT\n" +
        "CONCAT('Patient',' ',row_number() over (ORDER BY emcare_resources.id)) as \"key\",\n" +
        "EMCARE_RESOURCES.resource_id, \n" +
        "cast((EMCARE_RESOURCES.text) as json) -> cast('identifier' as text) -> 0 ->> cast('value' as text) as \"identifier\",\n" +
        "CONCAT(cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 0, ' ', cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 1) as \"givenName\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 ->> cast('family' as text) as \"familyName\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) ->> cast('gender' as text) as \"gender\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) ->> cast('birthDate' as text) as \"birthDate\",\n" +
        "cast((LOCATION_RESOURCES.text) as json) ->> cast('name' as text) as \"facilityName\",\n" +
        "cast((LOCATION_RESOURCES.text) as json) -> cast('address' as text) -> cast('line' as text) ->> 0 as \"addressLine\",\n" +
        "(LOCATION_RESOURCES.organization_name) as \"organizationName\",\n" +
        "(LOCATION_RESOURCES.location_name) as \"locationName\",\n" +
        "MAX_CONSULTATION_DATE.cnslDate as \"consultationDate\"\n" +
        "FROM EMCARE_RESOURCES\n" +
        "LEFT OUTER JOIN MAX_CONSULTATION_DATE ON EMCARE_RESOURCES.RESOURCE_ID = MAX_CONSULTATION_DATE.PATIENT_ID\n" +
        "LEFT JOIN LOCATION_RESOURCES ON EMCARE_RESOURCES.facility_id = LOCATION_RESOURCES.resource_id \n" +
        "where EMCARE_RESOURCES.modified_on between :startDate and :endDate ORDER BY EMCARE_RESOURCES.created_on DESC limit 10 offset :offset", nativeQuery = true)
    List<Map<String, Object>> getFilteredDateOnly(@Param("startDate") Date startDate,
                                                  @Param("endDate") Date endDate,
                                                  @Param("offset") Long offset);


    @Query(value = "\n" +
        "WITH MAX_CONSULTATION_DATE AS\n" +
        "(SELECT PATIENT_ID,\n" +
        "MAX(CONSULTATION_DATE) as cnslDate\n" +
        "FROM QUESTIONNAIRE_RESPONSE\n" +
        "GROUP BY PATIENT_ID)\n" +
        "SELECT\n" +
        "CONCAT('Patient',' ',row_number() over (ORDER BY emcare_resources.id)) as \"key\",\n" +
        "EMCARE_RESOURCES.resource_id, \n" +
        "cast((EMCARE_RESOURCES.text) as json) -> cast('identifier' as text) -> 0 ->> cast('value' as text) as \"identifier\",\n" +
        "CONCAT(cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 0, ' ', cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 -> cast('given' as text) ->> 1) as \"givenName\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) -> cast('name' as text) -> 0 ->> cast('family' as text) as \"familyName\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) ->> cast('gender' as text) as \"gender\",\n" +
        "cast((EMCARE_RESOURCES.text) as json) ->> cast('birthDate' as text) as \"birthDate\",\n" +
        "cast((LOCATION_RESOURCES.text) as json) ->> cast('name' as text) as \"facilityName\",\n" +
        "cast((LOCATION_RESOURCES.text) as json) -> cast('address' as text) -> cast('line' as text) ->> 0 as \"addressLine\",\n" +
        "(LOCATION_RESOURCES.organization_name) as \"organizationName\",\n" +
        "(LOCATION_RESOURCES.location_name) as \"locationName\",\n" +
        "MAX_CONSULTATION_DATE.cnslDate as \"consultationDate\"\n" +
        "FROM EMCARE_RESOURCES\n" +
        "LEFT OUTER JOIN MAX_CONSULTATION_DATE ON EMCARE_RESOURCES.RESOURCE_ID = MAX_CONSULTATION_DATE.PATIENT_ID\n" +
        "LEFT JOIN LOCATION_RESOURCES ON EMCARE_RESOURCES.facility_id = LOCATION_RESOURCES.resource_id \n" +
        "where EMCARE_RESOURCES.modified_on between :startDate and :endDate ORDER BY EMCARE_RESOURCES.created_on DESC", nativeQuery = true)
    List<Map<String, Object>> getFilteredDateOnlyCount(@Param("startDate") Date startDate,
                                                       @Param("endDate") Date endDate);

    @Query(value = "SELECT COUNT(*) FROM EMCARE_RESOURCES WHERE (CREATED_ON > :date OR MODIFIED_ON > :date) AND TYPE = 'PATIENT'", nativeQuery = true)
    Long getCountBasedOnDate(@Param("date") Date date);

    @Query(value = "SELECT COUNT(*) FROM EMCARE_RESOURCES WHERE (CREATED_ON > :date OR MODIFIED_ON > :date) AND TYPE = 'PATIENT' AND facility_id in :ids", nativeQuery = true)
    Long getCountBasedOnDateWithFacilityId(@Param("date") Date date, @Param("ids") List<String> ids);

    @Query(value = "SELECT COUNT(*) FROM EMCARE_RESOURCES WHERE TYPE = 'PATIENT'", nativeQuery = true)
    Long getCount();

    @Query(value = "SELECT COUNT(*) FROM EMCARE_RESOURCES WHERE TYPE = 'PATIENT' AND facility_id in :ids", nativeQuery = true)
    Long getCountWithFacilityId(@Param("ids") List<String> ids);

    @Query(value = "with t1 as (select to_date(birth_date,'yyyy-mm-dd') as birth_date from emcare_resources er),\n" +
        "t2 as (select birth_date,(extract(year from age(birth_date) * 12) + extract(month from age(birth_date))) age_in_months from t1)\n"
        +
        "select '0 to 2 Months' as key,sum(case when age_in_months <= 2 then 1 else 0 end) value from t2\n" +
        "union select '3 to 59 Months' as key,sum(case when age_in_months between 3 and 59 then 1 else 0 end) value from t2", nativeQuery = true)
    List<Map<String, Object>> getPieChartDataBasedOnAgeGroup();

}
