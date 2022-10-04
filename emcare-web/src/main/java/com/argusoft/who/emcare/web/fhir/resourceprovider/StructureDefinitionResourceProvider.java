package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.StructureDefinitionService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StructureDefinitionResourceProvider implements IResourceProvider {

    @Autowired
    StructureDefinitionService structureDefinitionService;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);


    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return StructureDefinition.class;
    }

    @Create
    public MethodOutcome createStructureDefinition(@ResourceParam StructureDefinition structureDefinition) {
        structureDefinitionService.saveResource(structureDefinition);
        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.LOCATION_TYPE_STRING, structureDefinition.getId(), "1"));
        retVal.setResource(structureDefinition);
        return retVal;
    }

    @Read()
    public StructureDefinition getResourceById(@IdParam IdType theId) {
        return structureDefinitionService.getResourceById(theId.getIdPart());
    }

    @Update
    public MethodOutcome updateStructureMapResource(@IdParam IdType theId, @ResourceParam StructureDefinition structureDefinition) {
        return structureDefinitionService.updateStructureDefinition(theId, structureDefinition);
    }


    @Search()
    public List<StructureDefinition> getAllStructureMap(@OptionalParam(name = CommonConstant.RESOURCE_LAST_UPDATED_AT) DateParam theDate) {
        return structureDefinitionService.getAllStructureMap(theDate);
    }

}
