package com.argusoft.who.emcare.web.questionnaire_response.respository;

import com.argusoft.who.emcare.web.questionnaire_response.dto.MiniPatient;
import com.argusoft.who.emcare.web.questionnaire_response.model.QuestionnaireResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionnaireResponseRepository extends JpaRepository<QuestionnaireResponse, String> {

    public List<QuestionnaireResponse> findByPatientIdIn(List<String> ids);

    public List<MiniPatient> findDistinctByPatientIdIn(List<String> resourceId, Pageable pageable);

    public List<MiniPatient> findDistinctByPatientIdIn(List<String> resourceId);

    public List<QuestionnaireResponse> findByPatientId(String patientId);
}
