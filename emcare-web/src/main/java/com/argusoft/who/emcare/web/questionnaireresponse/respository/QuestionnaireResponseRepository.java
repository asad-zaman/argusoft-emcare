package com.argusoft.who.emcare.web.questionnaireresponse.respository;

import com.argusoft.who.emcare.web.questionnaireresponse.dto.MiniPatient;
import com.argusoft.who.emcare.web.questionnaireresponse.model.QuestionnaireResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface QuestionnaireResponseRepository extends JpaRepository<QuestionnaireResponse, String> {

    public List<QuestionnaireResponse> findByPatientIdIn(List<String> ids);

    public List<QuestionnaireResponse> findByPatientIdInAndConsultationDateGreaterThan(List<String> ids, Date theDate);

    public List<MiniPatient> findDistinctByPatientIdIn(List<String> resourceId, Pageable pageable);

    @Query(value = "SELECT DISTINCT(QSR.PATIENT_ID) AS \"patientId\",\n" +
            "\tMAX(QSR.CONSULTATION_DATE) AS \"consultationDate\" \n" +
            "FROM ENCOUNTER_RESOURCE ENR \n" +
            "LEFT JOIN QUESTIONNAIRE_RESPONSE AS QSR ON ENR.RESOURCE_ID = QSR.ENCOUNTER_ID \n" +
            "WHERE ENR.PATIENT_ID IS NOT NULL and QSR.CONSULTATION_DATE IS NOT NULL AND ENR.PATIENT_ID in :resourceId \n" +
            "GROUP BY QSR.PATIENT_ID \n" +
            "ORDER BY MAX(QSR.CONSULTATION_DATE) DESC, QSR.PATIENT_ID", nativeQuery = true)
    List<MiniPatient> getDistinctPatientIdInAndConsultationDate(@Param("resourceId") List<String> resourceId, Pageable pageable);

    @Query(value = "SELECT DISTINCT(QSR.PATIENT_ID) AS \"patientId\",\n" +
            "\tMAX(QSR.CONSULTATION_DATE) AS \"consultationDate\" \n" +
            "FROM ENCOUNTER_RESOURCE ENR \n" +
            "LEFT JOIN QUESTIONNAIRE_RESPONSE AS QSR ON ENR.RESOURCE_ID = QSR.ENCOUNTER_ID \n" +
            "WHERE ENR.PATIENT_ID IS NOT NULL and QSR.CONSULTATION_DATE IS NOT NULL AND ENR.PATIENT_ID in :resourceId \n" +
            "GROUP BY QSR.PATIENT_ID \n" +
            "ORDER BY MAX(QSR.CONSULTATION_DATE) DESC, QSR.PATIENT_ID", nativeQuery = true)
    public List<MiniPatient> findDistinctByPatientIdIn(@Param("resourceId") List<String> resourceId);

    public List<QuestionnaireResponse> findByPatientId(String patientId);

    @Query(value = "SELECT DISTINCT(patient_id) FROM QUESTIONNAIRE_RESPONSE", nativeQuery = true)
    public List<String> findDistinctPatientIdd();

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
            "MAX_CONSULTATION_DATE.cnslDate as \"consultationDate\",\n" +
            "user_entity.first_name as \"providedByFName\",\n" +
            "user_entity.last_name as \"providedByLName\"\n" +
            "FROM EMCARE_RESOURCES\n" +
            "LEFT OUTER JOIN MAX_CONSULTATION_DATE ON EMCARE_RESOURCES.RESOURCE_ID = MAX_CONSULTATION_DATE.PATIENT_ID\n" +
            "left outer join user_entity on emcare_resources.created_by = user_entity.id \n" +
            "LEFT JOIN LOCATION_RESOURCES ON EMCARE_RESOURCES.facility_id = LOCATION_RESOURCES.resource_id \n" +
            "where MAX_CONSULTATION_DATE.cnslDate notnull and \n" +
            "EMCARE_RESOURCES.FACILITY_ID in :ids and EMCARE_RESOURCES.modified_on between :startDate and :endDate ORDER BY EMCARE_RESOURCES.created_on DESC limit 10 offset :offset",nativeQuery = true)
    List<Map<String, Object>> getFilteredConsultationsIn(@Param("ids") List<String> ids,
                                                    @Param("startDate") Date startDate,
                                                    @Param("endDate") Date endDate,
                                                    @Param("offset") Long offset);

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
            "MAX_CONSULTATION_DATE.cnslDate as \"consultationDate\",\n" +
            "user_entity.first_name as \"providedByFName\",\n" +
            "user_entity.last_name as \"providedByLName\"\n" +
            "FROM EMCARE_RESOURCES\n" +
            "LEFT OUTER JOIN MAX_CONSULTATION_DATE ON EMCARE_RESOURCES.RESOURCE_ID = MAX_CONSULTATION_DATE.PATIENT_ID\n" +
            "left outer join user_entity on emcare_resources.created_by = user_entity.id \n" +
            "LEFT JOIN LOCATION_RESOURCES ON EMCARE_RESOURCES.facility_id = LOCATION_RESOURCES.resource_id \n" +
            "where MAX_CONSULTATION_DATE.cnslDate notnull and \n" +
            "EMCARE_RESOURCES.FACILITY_ID in :ids and EMCARE_RESOURCES.modified_on between :startDate and :endDate ORDER BY EMCARE_RESOURCES.created_on DESC",nativeQuery = true)
    List<Map<String, Object>> getFilteredConsultationsInCount(@Param("ids") List<String> ids,
                                                         @Param("startDate") Date startDate,
                                                         @Param("endDate") Date endDate);
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
            "MAX_CONSULTATION_DATE.cnslDate as \"consultationDate\",\n" +
            "user_entity.first_name as \"providedByFName\",\n" +
            "user_entity.last_name as \"providedByLName\"\n" +
            "FROM EMCARE_RESOURCES\n" +
            "LEFT OUTER JOIN MAX_CONSULTATION_DATE ON EMCARE_RESOURCES.RESOURCE_ID = MAX_CONSULTATION_DATE.PATIENT_ID\n" +
            "left outer join user_entity on emcare_resources.created_by = user_entity.id \n" +
            "LEFT JOIN LOCATION_RESOURCES ON EMCARE_RESOURCES.facility_id = LOCATION_RESOURCES.resource_id \n" +
            "where MAX_CONSULTATION_DATE.cnslDate notnull and \n" +
            "EMCARE_RESOURCES.modified_on between :startDate and :endDate ORDER BY EMCARE_RESOURCES.created_on desc limit 10 offset :offset",nativeQuery = true)
    List<Map<String, Object>> getFilteredDateOnly(@Param("startDate") Date startDate,
                                                  @Param("endDate") Date endDate,
                                                  @Param("offset") Long offset);
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
            "MAX_CONSULTATION_DATE.cnslDate as \"consultationDate\",\n" +
            "user_entity.first_name as \"providedByFName\",\n" +
            "user_entity.last_name as \"providedByLName\"\n" +
            "FROM EMCARE_RESOURCES\n" +
            "LEFT OUTER JOIN MAX_CONSULTATION_DATE ON EMCARE_RESOURCES.RESOURCE_ID = MAX_CONSULTATION_DATE.PATIENT_ID\n" +
            "left outer join user_entity on emcare_resources.created_by = user_entity.id \n" +
            "LEFT JOIN LOCATION_RESOURCES ON EMCARE_RESOURCES.facility_id = LOCATION_RESOURCES.resource_id \n" +
            "where MAX_CONSULTATION_DATE.cnslDate notnull and \n" +
            "EMCARE_RESOURCES.modified_on between :startDate and :endDate ORDER BY EMCARE_RESOURCES.created_on DESC",nativeQuery = true)
    List<Map<String, Object>> getFilteredDateOnlyCount(@Param("startDate") Date startDate,
                                                       @Param("endDate") Date endDate);
}
