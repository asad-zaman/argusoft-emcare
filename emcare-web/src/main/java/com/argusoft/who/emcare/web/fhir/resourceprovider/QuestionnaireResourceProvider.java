package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.model.QuestionnaireMaster;
import com.argusoft.who.emcare.web.fhir.service.QuestionnaireMasterService;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Questionnaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class QuestionnaireResourceProvider implements IResourceProvider {

    @Autowired
    private QuestionnaireMasterService questionnaireMasterService;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);

    @Override
    public Class<Questionnaire> getResourceType() {
        return Questionnaire.class;
    }

    @Create
    public MethodOutcome createQuestionnaire(@ResourceParam Questionnaire questionnaire) {

        String questionnaireId = null;
        if (questionnaire.getId() != null) {
            questionnaireId = questionnaire.getId();
        } else {
            questionnaireId = UUID.randomUUID().toString();
        }
        questionnaire.setId(questionnaireId);

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

        QuestionnaireMaster questionnaireMaster = questionnaireMasterService.retrieveQuestionnaireByResourceId(questionnaire.getId());

        if (questionnaireMaster == null) {
            questionnaireMaster = new QuestionnaireMaster();
            questionnaireMaster.setVersion("1.0");
            questionnaireMaster.setResourceId(questionnaire.getIdElement().getIdPart());
        } else {
            Double version = (Double.parseDouble(questionnaireMaster.getVersion()) * 10 + 1) / 10; //Incrementing version by 0.1
            questionnaireMaster.setVersion(version.toString());
        }
        questionnaireMaster.setText(questionnaireString);

        questionnaireMasterService.saveResource(questionnaireMaster);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.FHIR_QUESTIONNAIRE, questionnaire.getId(), versionId.toString()));
        retVal.setResource(questionnaire);

        return retVal;
    }

    @Read()
    public Questionnaire getQuestionnaireByResourceId(@IdParam IdType theId) {
        QuestionnaireMaster questionnaireMaster = questionnaireMasterService.retrieveQuestionnaireByResourceId(theId.getIdPart());
        Questionnaire questionnaire = null;
        if (questionnaireMaster != null) {
            questionnaire = parser.parseResource(Questionnaire.class, questionnaireMaster.getText());
        }
        return questionnaire;
    }

    @Search()
    public List<Questionnaire> getAllQuestionnaires(@OptionalParam(name = CommonConstant.RESOURCE_LAST_UPDATED_AT) DateParam theDate) {
        List<Questionnaire> questionnaireList = new ArrayList<>();
        List<QuestionnaireMaster> resourcesList = questionnaireMasterService.retrieveAllQuestionnaires(theDate);
        for (QuestionnaireMaster questionnaireMaster : resourcesList) {
            Questionnaire questionnaire = parser.parseResource(Questionnaire.class, questionnaireMaster.getText());
            questionnaireList.add(questionnaire);
        }
        return questionnaireList;
    }

    @Update
    public MethodOutcome updateQuestionnaire(@IdParam IdType theId, @ResourceParam Questionnaire questionnaire) {

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
        QuestionnaireMaster questionnaireMaster = questionnaireMasterService.retrieveQuestionnaireByResourceId(questionnaire.getIdElement().getIdPart());
        if (questionnaireMaster == null) {
            questionnaireMaster = new QuestionnaireMaster();
            questionnaireMaster.setVersion("1.0");
            questionnaireMaster.setResourceId(questionnaire.getIdElement().getIdPart());
        } else {
            Double version = (Double.parseDouble(questionnaireMaster.getVersion()) * 10 + 1) / 10; //Incrementing version by 0.1
            questionnaireMaster.setVersion(version.toString());
        }
        questionnaireMaster.setText(questionnaireString);

        questionnaireMaster = questionnaireMasterService.saveResource(questionnaireMaster);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.FHIR_QUESTIONNAIRE, questionnaire.getId(), questionnaireMaster.getVersion()));
        retVal.setResource(questionnaire);

        return retVal;
    }

    @Delete()
    public void deleteQuestionnaire(@IdParam IdType theId) {

        QuestionnaireMaster questionnaireMaster = questionnaireMasterService.retrieveQuestionnaireByResourceId(theId.getIdPart());

        if (questionnaireMaster == null) {
            throw new ResourceNotFoundException("Unknown version");
        } else {
            questionnaireMasterService.remove(questionnaireMaster);
        }
    }

    @Search()
    public Bundle getQuestionnaireCountBasedOnDate(@RequiredParam(name = CommonConstant.SUMMARY) String type, @OptionalParam(name = CommonConstant.RESOURCE_LAST_UPDATED_AT) DateParam theDate) {
        return questionnaireMasterService.getQuestionnaireCountBasedOnDate(type, theDate);
    }

}
