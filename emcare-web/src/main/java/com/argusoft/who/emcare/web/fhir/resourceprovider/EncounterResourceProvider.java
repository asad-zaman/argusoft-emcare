package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.EncounterResourceService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EncounterResourceProvider implements IResourceProvider {

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);
    @Autowired
    EncounterResourceService encounterResourceService;

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Encounter.class;
    }

    @Create
    public MethodOutcome createEncounter(@ResourceParam Encounter encounter) {
        encounterResourceService.saveResource(encounter);
        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.ENCOUNTER, encounter.getId(), "1"));
        retVal.setResource(encounter);
        return retVal;
    }

    @Read()
    public Encounter getResourceById(@IdParam IdType theId) {
        return encounterResourceService.getResourceById(theId.getIdPart());
    }

    @Update
    public MethodOutcome updateEncounterResource(@IdParam IdType theId, @ResourceParam Encounter encounter) {
        return encounterResourceService.updateEncounterResource(theId, encounter);
    }

    @Search()
    public List<Encounter> getAllEncounter(
            @OptionalParam(name = CommonConstant.RESOURCE_LAST_UPDATED_AT) DateParam theDate,
            @OptionalParam(name = CommonConstant.RESOURCE_CONTENT) String searchText,
            @RequiredParam(name = CommonConstant.RESOURCE_FACILITY_ID) String theId) {
        return encounterResourceService.getAllEncounter(theDate, searchText, theId);
    }

    @Search(queryName = "summary")
    public Bundle getEncounterCountBasedOnDate(
            @RequiredParam(name = CommonConstant.SUMMARY) String type,
            @OptionalParam(name = CommonConstant.RESOURCE_LAST_UPDATED_AT) DateParam theDate,
            @OptionalParam(name = CommonConstant.RESOURCE_FACILITY_ID) String theId) {
        return encounterResourceService.getEncounterCountBasedOnDate(type, theDate, theId);
    }
}
