package com.argusoft.who.emcare.web.questionnaire_response.respository;

import com.argusoft.who.emcare.web.questionnaire_response.model.QuestionnaireResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionnaireResponseRepository extends JpaRepository<QuestionnaireResponse, Long> {

    public List<QuestionnaireResponse> findByPatientIdIn(List<String> ids);
}
