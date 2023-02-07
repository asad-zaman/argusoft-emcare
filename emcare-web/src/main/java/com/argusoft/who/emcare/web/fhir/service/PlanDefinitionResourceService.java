package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.fhir.model.PlanDefinitionResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.PlanDefinition;

import java.util.List;

public interface PlanDefinitionResourceService {

    public PlanDefinitionResource saveResource(PlanDefinition planDefinition);

    public PlanDefinition getByResourceId(String resourceId);

    public List<PlanDefinition> getAllPlanDefinition(DateParam theDate);

    public void deletePlanDefinition(String resourceId);

    public MethodOutcome updateLocationResource(IdType theId, PlanDefinition planDefinition);


}
