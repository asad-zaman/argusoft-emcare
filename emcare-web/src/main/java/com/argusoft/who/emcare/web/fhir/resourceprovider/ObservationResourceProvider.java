package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.ObservationResourceService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ObservationResourceProvider implements IResourceProvider {

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);

    @Autowired
    ObservationResourceService observationResourceService;

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Observation.class;
    }


    @Create
    public MethodOutcome createObservation(@ResourceParam Observation observation) {
        observationResourceService.saveResource(observation);
        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.LIBRARY, observation.getId(), "1"));
        retVal.setResource(observation);
        return retVal;
    }

    @Read()
    public Observation getResourceById(@IdParam IdType theId) {
        return observationResourceService.getResourceById(theId.getIdPart());
    }

    @Update
    public MethodOutcome updateObservationResource(@IdParam IdType theId, @ResourceParam Observation observation) {
        return observationResourceService.updateObservationResource(theId, observation);
    }

    @Search()
    public List<Observation> getAllObservation(@OptionalParam(name = CommonConstant.RESOURCE_LAST_UPDATED_AT) DateParam theDate) {
        return observationResourceService.getAllObservation(theDate);
    }
}
