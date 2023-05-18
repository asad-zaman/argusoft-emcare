package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.RelatedPersonResourceService;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RelatedPersonResourceProvider implements IResourceProvider {

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);
    @Autowired
    RelatedPersonResourceService relatedPersonResourceService;

    @Override
    public Class<RelatedPerson> getResourceType() {
        return RelatedPerson.class;
    }

    public MethodOutcome createEncounter(@ResourceParam RelatedPerson relatedPerson) {
        relatedPersonResourceService.saveResource(relatedPerson);
        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.ENCOUNTER, relatedPerson.getId(), "1"));
        retVal.setResource(relatedPerson);
        return retVal;
    }

    @Read()
    public RelatedPerson getResourceById(@IdParam IdType theId) {
        return relatedPersonResourceService.getResourceById(theId.getIdPart());
    }

    @Update
    public MethodOutcome updateRelatedPersonResource(@IdParam IdType theId, @ResourceParam RelatedPerson relatedPerson) {
        return relatedPersonResourceService.updateRelatedPersonResource(theId, relatedPerson);
    }

    @Search()
    public List<RelatedPerson> getAllRelatedPerson(
            @OptionalParam(name = CommonConstant.RESOURCE_LAST_UPDATED_AT) DateParam theDate,
            @RequiredParam(name = CommonConstant.RESOURCE_FACILITY_ID) String theId) {
        return relatedPersonResourceService.getAllRelatedPerson(theDate, theId);
    }

    @Search(queryName = "patient")
    public List<RelatedPerson> getAllRelatedPersonByPatientId(@RequiredParam(name = CommonConstant.RESOURCE_ID) IdType theId) {
        return relatedPersonResourceService.getAllRelatedPersonByPatientId(theId);
    }

    @Search(queryName = "summary")
    public Bundle getRelatedPersonCountBasedOnDate(
            @RequiredParam(name = CommonConstant.SUMMARY) String type,
            @OptionalParam(name = CommonConstant.RESOURCE_LAST_UPDATED_AT) DateParam theDate,
            @OptionalParam(name = CommonConstant.RESOURCE_FACILITY_ID) String theId) {
        return relatedPersonResourceService.getRelatedPersonCountBasedOnDate(type, theDate, theId);
    }
}
