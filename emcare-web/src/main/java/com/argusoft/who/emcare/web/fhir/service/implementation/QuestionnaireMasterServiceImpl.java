package com.argusoft.who.emcare.web.fhir.service.implementation;

import com.argusoft.who.emcare.web.fhir.dao.QuestionnaireMasterRepository;
import com.argusoft.who.emcare.web.fhir.model.QuestionnaireMaster;
import com.argusoft.who.emcare.web.fhir.service.QuestionnaireMasterService;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class QuestionnaireMasterServiceImpl implements QuestionnaireMasterService {

    
    @Autowired
    QuestionnaireMasterRepository repository;
    
    @Override
    public QuestionnaireMaster saveResource(QuestionnaireMaster questionnaireMaster) {
        return repository.save(questionnaireMaster);
    }

    @Override
    public List<QuestionnaireMaster> retrieveAllQuestionnaires() {
        return repository.findAll();
    }

    @Override
    public QuestionnaireMaster retrieveQuestionnaireById(Integer id) {
        Optional<QuestionnaireMaster> qm = repository.findById(id);
        if(qm.isPresent()){
            return qm.get();
        } else {
            return null;
        }
    }
    
    @Override
    public QuestionnaireMaster retrieveQuestionnaireByResourceId(String resourceId) {
        return repository.findByResourceId(resourceId);
    }

    @Override
    public void remove(QuestionnaireMaster questionnaireMaster) {
        repository.delete(questionnaireMaster);
    }
    
}
