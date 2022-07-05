package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.IdType;

import java.util.List;

public interface ActivityDefinitionResourceService {

    public ActivityDefinition saveResource(ActivityDefinition definition);

    public ActivityDefinition getResourceById(String id);

    public MethodOutcome updateActivityDefinitionResource(IdType idType, ActivityDefinition definition);

    public List<ActivityDefinition> getAllActivityDefinition();

    public PageDto getActivityDefinitionPage(Integer pageNo, String searchString);
}
