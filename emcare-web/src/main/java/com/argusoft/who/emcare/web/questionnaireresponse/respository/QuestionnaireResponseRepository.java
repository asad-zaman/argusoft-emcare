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

    @Query(value = "insert into user_sync_log (user_id, sync_attempt_time) values ( :userId ,now())")
    public void logSyncAttempt(String userId);
}