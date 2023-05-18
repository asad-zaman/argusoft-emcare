package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.BinaryResourceService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Binary;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class BinaryResourceProvider implements IResourceProvider {

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Binary.class;
    }

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);

    @Autowired
    BinaryResourceService binaryResourceService;

    @Create
    public MethodOutcome addBinaryResource(@ResourceParam Binary binary) {
        binaryResourceService.saveResource(binary);
        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.BINARY_TYPE_STRING, binary.getId(), "1"));
        retVal.setResource(binary);
        return retVal;
    }

    @Read()
    public Binary getResourceById(@IdParam IdType theId) {
        return binaryResourceService.getResourceById(theId.getIdPart());
    }

    @Update
    public MethodOutcome updateBinaryResource(@IdParam IdType theId, @ResourceParam Binary binary) {
        return binaryResourceService.updateBinaryResource(theId, binary);
    }

    @Search()
    public List<Binary> getAllBinaryResource(@OptionalParam(name = CommonConstant.RESOURCE_LAST_UPDATED_AT) DateParam theDate) {
        return binaryResourceService.getAllBinaryResource(theDate);
    }

}
