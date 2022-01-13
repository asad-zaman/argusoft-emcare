package com.argusoft.who.emcare.web.fhir.service;

import com.argusoft.who.emcare.web.fhir.model.QuestionnaireMaster;
import java.util.List;


public interface QuestionnaireMasterService {
    
    public QuestionnaireMaster saveResource(QuestionnaireMaster questionnaireMaster);

    public List<QuestionnaireMaster> retrieveAllQuestionnaires();
    
    public QuestionnaireMaster retrieveQuestionnaireById(Integer id);
    
    public QuestionnaireMaster retrieveQuestionnaireByResourceId(String resourceId);
    
    public void remove(QuestionnaireMaster questionnaireMaster);
    
}
