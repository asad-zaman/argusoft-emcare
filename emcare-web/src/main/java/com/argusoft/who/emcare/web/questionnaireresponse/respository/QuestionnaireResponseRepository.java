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

    @Query(value = "SELECT DISTINCT(PATIENT_ID) as \"patientId\"," +
            "MAX(CONSULTATION_DATE) as \"consultationDate\"" +
            "FROM QUESTIONNAIRE_RESPONSE where patient_id in :resourceId " +
            "GROUP BY PATIENT_ID " +
            "ORDER BY MAX(CONSULTATION_DATE) DESC, PATIENT_ID", nativeQuery = true)
    List<MiniPatient> getDistinctPatientIdInAndConsultationDate(@Param("resourceId") List<String> resourceId, Pageable pageable);

    @Query(value = "SELECT DISTINCT(PATIENT_ID) as \"patientId\"," +
            "MAX(CONSULTATION_DATE) as \"consultationDate\"" +
            "FROM QUESTIONNAIRE_RESPONSE where patient_id in :resourceId " +
            "GROUP BY PATIENT_ID " +
            "ORDER BY MAX(CONSULTATION_DATE) DESC, PATIENT_ID", nativeQuery = true)
    public List<MiniPatient> findDistinctByPatientIdIn(List<String> resourceId);

    public List<QuestionnaireResponse> findByPatientId(String patientId);
}
