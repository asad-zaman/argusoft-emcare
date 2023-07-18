package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.dao.ConditionResourceRepository;
import com.argusoft.who.emcare.web.fhir.model.ConditionResource;
import com.argusoft.who.emcare.web.fhir.service.ConditionResourceService;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ConditionResourceServiceImpl implements ConditionResourceService {

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(true);
    @Autowired
    ConditionResourceRepository conditionResourceRepository;

    @Override
    public Condition saveResource(Condition condition) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        condition.setMeta(m);

        String conditionId = null;
        if (condition.getId() != null) {
            conditionId = condition.getIdElement().getIdPart();
        } else {
            conditionId = UUID.randomUUID().toString();
        }
        condition.setId(conditionId);

        String resourceString = parser.encodeResourceToString(condition);

        ConditionResource conditionResource = new ConditionResource();
        conditionResource.setText(resourceString);
        conditionResource.setPatientId(condition.getSubject().getIdentifier().getId());
        conditionResource.setEncounterId(condition.getEncounter().getIdentifier().getId());
        conditionResource.setResourceId(conditionId);

        conditionResourceRepository.save(conditionResource);

        return condition;
    }

    @Override
    public Condition getResourceById(String id) {
        ConditionResource conditionResource = conditionResourceRepository.findByResourceId(id);
        Condition condition = null;
        if (conditionResource != null) {
            condition = parser.parseResource(Condition.class, conditionResource.getText());
        }
        return condition;
    }

    @Override
    public MethodOutcome updateConditionResource(IdType idType, Condition condition) {
        Integer version = 1;
        version = Integer.parseInt(condition.getMeta().getVersionId());
        if (version > 0) {
            version++;
        }
        Meta m = new Meta();
        m.setVersionId(version.toString());
        m.setLastUpdated(new Date());
        condition.setMeta(m);


        String resourceString = parser.encodeResourceToString(condition);
        ConditionResource conditionResource = conditionResourceRepository.findByResourceId(idType.getIdPart());
        conditionResource.setText(resourceString);
        conditionResource.setPatientId(condition.getSubject().getIdentifier().getId());
        conditionResource.setEncounterId(condition.getEncounter().getIdentifier().getId());

        conditionResourceRepository.save(conditionResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.CONDITION, condition.getId(), version.toString()));
        retVal.setResource(condition);
        return retVal;
    }

    @Override
    public List<Condition> getAllCondition(DateParam theDate, String searchText) {
        List<Condition> conditions = new ArrayList<>();
        List<ConditionResource> conditionResources;

        if (theDate == null) {
            if (searchText == null) {
                conditionResources = conditionResourceRepository.findAll();
            } else {
                conditionResources = conditionResourceRepository.findByTextContainingIgnoreCase(searchText);
            }
        } else {
            if (searchText == null) {
                conditionResources = conditionResourceRepository.findByModifiedOnGreaterThanOrCreatedOnGreaterThan(theDate.getValue(), theDate.getValue());
            } else {
                conditionResources = conditionResourceRepository.fetchByDateAndText(searchText, theDate.getValue());
            }
        }

        for (ConditionResource conditionResource : conditionResources) {
            Condition condition = parser.parseResource(Condition.class, conditionResource.getText());
            conditions.add(condition);
        }
        return conditions;
    }

    @Override
    public List<Condition> getByPatientId(String patientId) {
        List<Condition> conditions = new ArrayList<>();
        List<ConditionResource> conditionResources = conditionResourceRepository.findByPatientIdOrResourceIdOrEncounterId(patientId, patientId, patientId);
        for (ConditionResource conditionResource : conditionResources) {
            Condition condition = parser.parseResource(Condition.class, conditionResource.getText());
            conditions.add(condition);
        }
        return conditions;
    }

    public Bundle getConditionDataForGoogleFhirDataPipes(String summaryType, Integer count, String total) {
        Bundle bundle = new Bundle();
        switch(summaryType) {
            case "count":
                bundle.setTotal((int)conditionResourceRepository.count());
                return bundle;
            case "data":
                List<Condition> conditions = getAllCondition(null, null);
                bundle.setTotal(Math.min(count, conditions.size()));

                for(int i = 0; i < Math.min(count, conditions.size()); i++) {
                    bundle.addEntry(
                            new Bundle.BundleEntryComponent()
                                    .setResource(conditions.get(i))
                                    .setFullUrl("http://localhost:8080/fhir/" + conditions.get(i).getId().split("/_history")[0])
                    );
                }
                return bundle;
        }
        return null;
    }
}
