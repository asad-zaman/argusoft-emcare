package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.StructureMapResourceService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.StructureMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StructureMapResourceProvider implements IResourceProvider {

    @Autowired
    StructureMapResourceService structureMapResourceService;


    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);


    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return StructureMap.class;
    }

    @Create
    public MethodOutcome createStructureMap(@ResourceParam StructureMap structureMap) {
        structureMapResourceService.saveResource(structureMap);
        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.LOCATION_TYPE_STRING, structureMap.getId(), "1"));
        retVal.setResource(structureMap);
        return retVal;
    }

    @Read()
    public StructureMap getResourceById(@IdParam IdType theId) {
        return structureMapResourceService.getResourceById(theId.getIdPart());
    }

    @Update
    public MethodOutcome updateStructureMapResource(@IdParam IdType theId, @ResourceParam StructureMap structureMap) {
        return structureMapResourceService.updateStructureMapResource(theId, structureMap);
    }

    @Search()
    public List<StructureMap> getAllStructureMap() {
        return structureMapResourceService.getAllStructureMap();
    }
}
