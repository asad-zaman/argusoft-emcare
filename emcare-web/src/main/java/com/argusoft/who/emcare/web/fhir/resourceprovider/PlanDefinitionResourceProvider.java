package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.PlanDefinitionResourceService;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.PlanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlanDefinitionResourceProvider implements IResourceProvider {

    @Autowired
    PlanDefinitionResourceService planDefinitionResourceService;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);

    @Override
    public Class<PlanDefinition> getResourceType() {
        return PlanDefinition.class;
    }

    @Create
    public MethodOutcome createLocation(@ResourceParam PlanDefinition planDefinition) {
        planDefinitionResourceService.saveResource(planDefinition);
        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.PLANDEFINITION_TYPE_STRING, planDefinition.getId(), "1"));
        retVal.setResource(planDefinition);

        return retVal;
    }

    @Read()
    public PlanDefinition getResourceById(@IdParam IdType theId) {
        return planDefinitionResourceService.getByResourceId(theId.getIdPart());
    }

    @Search()
    public List<PlanDefinition> getAllPlanDefinition() {
        return planDefinitionResourceService.getAllPlanDefinition();
    }

    @Delete()
    public void deleteLocationResource(@IdParam IdType theId) {
        planDefinitionResourceService.deletePlanDefinition(theId.getIdPart());
    }

    @Update
    public MethodOutcome updateLocationResource(@IdParam IdType theId, @ResourceParam PlanDefinition planDefinition) {
        return planDefinitionResourceService.updateLocationResource(theId, planDefinition);
    }
}
