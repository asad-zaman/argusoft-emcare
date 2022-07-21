package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.model.QuestionnaireMaster;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.PlanDefinition;
import org.hl7.fhir.r4.model.Questionnaire;

import java.util.List;


public interface QuestionnaireMasterService {
    
    public QuestionnaireMaster saveResource(QuestionnaireMaster questionnaireMaster);

    public List<QuestionnaireMaster> retrieveAllQuestionnaires();
    
    public QuestionnaireMaster retrieveQuestionnaireById(Integer id);
    
    public QuestionnaireMaster retrieveQuestionnaireByResourceId(String resourceId);
    
    public void remove(QuestionnaireMaster questionnaireMaster);
    
    public PageDto getQuestionnaireDtosPage(Integer pageNo);

    public MethodOutcome updateQuestionnaireResource(IdType theId, Questionnaire questionnaire);

    
}
