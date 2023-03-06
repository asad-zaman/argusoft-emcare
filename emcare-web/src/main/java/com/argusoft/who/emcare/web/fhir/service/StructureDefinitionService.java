package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.StructureDefinition;

import java.util.List;

public interface StructureDefinitionService {

    public StructureDefinition saveResource(StructureDefinition structureDefinition);

    public StructureDefinition getResourceById(String id);

    public MethodOutcome updateStructureDefinition(IdType theId, StructureDefinition structureDefinition);

    public List<StructureDefinition> getAllStructureMap(DateParam theDate);

    public PageDto getStructureDefinitionPage(Integer pageNo, String searchString);

    public Bundle getStructureDefinitionCountBasedOnDate(String summaryType, DateParam theDate);


}
