package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.QuestionnaireMasterRepository;
import com.argusoft.who.emcare.web.fhir.dto.QuestionnaireDto;
import com.argusoft.who.emcare.web.fhir.mapper.EmcareResourceMapper;
import com.argusoft.who.emcare.web.fhir.model.QuestionnaireMaster;
import com.argusoft.who.emcare.web.fhir.service.QuestionnaireMasterService;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Questionnaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class QuestionnaireMasterServiceImpl implements QuestionnaireMasterService {

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);
    
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

    @Override
    public PageDto getQuestionnaireDtosPage(Integer pageNo) {
        List<Questionnaire> questionnairesList = new ArrayList<>();
        List<QuestionnaireDto> questionnaireDtosList;
        
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE);
        Integer totalCount = repository.findAll().size();
        List<QuestionnaireMaster> questionnaireMasters = repository.findAll(page).getContent();
        
        for(QuestionnaireMaster qm : questionnaireMasters) {
            Questionnaire q = parser.parseResource(Questionnaire.class, qm.getText());
            questionnairesList.add(q);
        }
        
        questionnaireDtosList = EmcareResourceMapper.questionnaireEntitiesToDtoMapper(questionnairesList);
        
        PageDto pageDto = new PageDto();
        pageDto.setList(questionnaireDtosList);
        pageDto.setTotalCount(totalCount.longValue());
        return pageDto;
    }

    @Override
    public MethodOutcome updateQuestionnaireResource(IdType theId, Questionnaire questionnaire) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());

        Integer versionId = 1;

        if (questionnaire.getMeta() != null && questionnaire.getMeta().getVersionId() != null) {
            versionId = Integer.parseInt(questionnaire.getMeta().getVersionId()) + 1;
            m.setVersionId(String.valueOf(versionId));
        }
        questionnaire.setMeta(m);


        String questionnaireString = parser.encodeResourceToString(questionnaire);
        QuestionnaireMaster questionnaireMaster = retrieveQuestionnaireByResourceId(questionnaire.getIdElement().getIdPart());
        if (questionnaireMaster == null) {
            questionnaireMaster = new QuestionnaireMaster();
            questionnaireMaster.setVersion("1.0");
            questionnaireMaster.setResourceId(questionnaire.getIdElement().getIdPart());
        } else {
            Double version = (Double.parseDouble(questionnaireMaster.getVersion()) * 10 + 1) / 10; //Incrementing version by 0.1
            questionnaireMaster.setVersion(version.toString());
        }
        questionnaireMaster.setText(questionnaireString);

        questionnaireMaster = saveResource(questionnaireMaster);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.FHIR_QUESTIONNAIRE, questionnaire.getId(), questionnaireMaster.getVersion()));
        retVal.setResource(questionnaire);

        return retVal;
    }


}
