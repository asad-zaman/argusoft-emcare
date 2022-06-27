package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.CodeSystemResourceService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CodeSystemResourceProvider implements IResourceProvider {

    @Autowired
    CodeSystemResourceService codeSystemResourceService;

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return CodeSystem.class;
    }

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);

    @Create
    public MethodOutcome createStructureMap(@ResourceParam CodeSystem codeSystem) {
        codeSystemResourceService.saveResource(codeSystem);
        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.LOCATION_TYPE_STRING, codeSystem.getId(), "1"));
        retVal.setResource(codeSystem);
        return retVal;
    }

    @Read()
    public CodeSystem getResourceById(@IdParam IdType theId) {
        return codeSystemResourceService.getResourceById(theId.getIdPart());
    }

    @Update
    public MethodOutcome updateStructureMapResource(@IdParam IdType theId, @ResourceParam CodeSystem codeSystem) {
        return codeSystemResourceService.updateCodeSystem(theId, codeSystem);
    }

    @Search()
    public List<CodeSystem> getAllStructureMap() {
        return codeSystemResourceService.getAllCodeSystem();
    }
}
