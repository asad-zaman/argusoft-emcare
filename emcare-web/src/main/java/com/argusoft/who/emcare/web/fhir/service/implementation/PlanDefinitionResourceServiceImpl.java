package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.dao.PlanDefinitionResourceRepository;
import com.argusoft.who.emcare.web.fhir.model.PlanDefinitionResource;
import com.argusoft.who.emcare.web.fhir.service.PlanDefinitionResourceService;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.PlanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Transactional
@Service
public class PlanDefinitionResourceServiceImpl implements PlanDefinitionResourceService {

    @Autowired
    PlanDefinitionResourceRepository planDefinitionResourceRepository;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(true);

    @Override
    public PlanDefinitionResource saveResource(PlanDefinition planDefinition) {

        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        planDefinition.setMeta(m);

        String planId = null;
        if (planDefinition.getId() != null) {
            planId = planDefinition.getIdElement().getIdPart();
        } else {
            planId = UUID.randomUUID().toString();
        }

        planDefinition.setId(planId);


        String locationString = parser.encodeResourceToString(planDefinition);

        PlanDefinitionResource planDefinitionResource = new PlanDefinitionResource();
        planDefinitionResource.setText(locationString);
        planDefinitionResource.setType(CommonConstant.PLANDEFINITION_TYPE_STRING);
        planDefinitionResource.setResourceId(planId);

        planDefinitionResource = planDefinitionResourceRepository.save(planDefinitionResource);

        return planDefinitionResource;
    }

    @Override
    public PlanDefinition getByResourceId(String resourceId) {
        PlanDefinitionResource planDefinitionResource = planDefinitionResourceRepository.findByResourceId(resourceId);
        PlanDefinition planDefinition = null;
        if (planDefinitionResource != null) {
            planDefinition = parser.parseResource(PlanDefinition.class, planDefinitionResource.getText());
        }
        return planDefinition;
    }

    @Override
    public List<PlanDefinition> getAllPlanDefinition(DateParam theDate) {
        List<PlanDefinition> planDefinitions = new ArrayList<>();

        List<PlanDefinitionResource> planDefinitionResources = new ArrayList<>();

        if (theDate == null) {
            planDefinitionResources =  planDefinitionResourceRepository.findAll();
        } else {
            planDefinitionResources = planDefinitionResourceRepository.findByModifiedOnGreaterThanOrCreatedOnGreaterThan(theDate.getValue(), theDate.getValue());
        }

        for (PlanDefinitionResource planDefinitionResource : planDefinitionResources) {
            PlanDefinition planDefinition = parser.parseResource(PlanDefinition.class, planDefinitionResource.getText());
            planDefinitions.add(planDefinition);
        }
        return planDefinitions;
    }

    @Override
    public void deletePlanDefinition(String resourceId) {
        PlanDefinitionResource planDefinitionResource = planDefinitionResourceRepository.findByResourceId(resourceId);

        if (planDefinitionResource == null) {
            throw new ResourceNotFoundException("Plan Definition Not Found");
        } else {
            planDefinitionResourceRepository.delete(planDefinitionResource);
        }
    }

    @Override
    public MethodOutcome updateLocationResource(IdType theId, PlanDefinition planDefinition) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        planDefinition.setMeta(m);


        String encodeResource = parser.encodeResourceToString(planDefinition);
        PlanDefinitionResource planDefinitionResource = planDefinitionResourceRepository.findByResourceId(theId.getIdPart());
        PlanDefinitionResource definitionResource = new PlanDefinitionResource();
        definitionResource.setText(encodeResource);
        definitionResource.setType(CommonConstant.LOCATION_TYPE_STRING);
        definitionResource.setResourceId(planDefinitionResource.getResourceId());
        definitionResource.setId(planDefinitionResource.getId());

        planDefinitionResourceRepository.save(definitionResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.PLANDEFINITION_TYPE_STRING, planDefinition.getId(), "1"));
        retVal.setResource(planDefinition);
        return retVal;
    }
}
