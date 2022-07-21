package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationDefinition;

import java.util.List;

public interface OperationDefinitionResourceService {


    public OperationDefinition saveResource(OperationDefinition operationDefinition);

    public OperationDefinition getResourceById(String id);

    public MethodOutcome updateOperationDefinitionResource(IdType idType, OperationDefinition operationDefinition);

    public List<OperationDefinition> getAllOperationDefinition();

    public PageDto getOperationDefinitionPage(Integer pageNo, String searchString);
}
