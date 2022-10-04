package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.ActivityDefinitionResourceService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActivityDefinitionResourceProvider implements IResourceProvider {

    @Autowired
    ActivityDefinitionResourceService activityDefinitionResourceService;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);


    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return ActivityDefinition.class;
    }

    @Create
    public MethodOutcome createActivityDefinition(@ResourceParam ActivityDefinition definition) {
        activityDefinitionResourceService.saveResource(definition);
        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.LOCATION_TYPE_STRING, definition.getId(), "1"));
        retVal.setResource(definition);
        return retVal;
    }

    @Read()
    public ActivityDefinition getResourceById(@IdParam IdType theId) {
        return activityDefinitionResourceService.getResourceById(theId.getIdPart());
    }

    @Update
    public MethodOutcome updateStructureMapResource(@IdParam IdType theId, @ResourceParam ActivityDefinition definition) {
        return activityDefinitionResourceService.updateActivityDefinitionResource(theId, definition);
    }

    @Search()
    public List<ActivityDefinition> getAllActivityDefinition(@OptionalParam(name = CommonConstant.RESOURCE_LAST_UPDATED_AT) DateParam theDate) {
        return activityDefinitionResourceService.getAllActivityDefinition(theDate);
    }
}
