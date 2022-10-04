package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.OperationDefinitionResourceService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OperationDefinitionResourceProvider implements IResourceProvider {

    @Autowired
    OperationDefinitionResourceService operationDefinitionResourceService;


    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);


    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return OperationDefinition.class;
    }

    @Create
    public MethodOutcome createOperationDefinition(@ResourceParam OperationDefinition operationDefinition) {
        operationDefinitionResourceService.saveResource(operationDefinition);
        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.OPERATION_DEFINITION, operationDefinition.getId(), "1"));
        retVal.setResource(operationDefinition);
        return retVal;
    }

    @Read()
    public OperationDefinition getResourceById(@IdParam IdType theId) {
        return operationDefinitionResourceService.getResourceById(theId.getIdPart());
    }

    @Update
    public MethodOutcome updateOperationDefinitionResource(@IdParam IdType theId, @ResourceParam OperationDefinition operationDefinition) {
        return operationDefinitionResourceService.updateOperationDefinitionResource(theId, operationDefinition);
    }

    @Search()
    public List<OperationDefinition> getAllOperationDefinition(@OptionalParam(name = CommonConstant.RESOURCE_LAST_UPDATED_AT) DateParam theDate) {
        return operationDefinitionResourceService.getAllOperationDefinition(theDate);
    }
}
