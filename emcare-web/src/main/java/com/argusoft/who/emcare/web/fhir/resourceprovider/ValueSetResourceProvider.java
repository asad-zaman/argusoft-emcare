package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.ValueSetResourceService;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import org.hl7.fhir.r4.model.ValueSet;

@Component
public class ValueSetResourceProvider implements IResourceProvider {

    @Autowired
    ValueSetResourceService valueSetResourceService;

    @Override
    public Class<ValueSet> getResourceType() {
        return ValueSet.class;
    }

    @Create
    public MethodOutcome createValueSet(@ResourceParam ValueSet valueSet) {
        valueSetResourceService.saveResource(valueSet);
        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.VALUESET_TYPE_STRING, valueSet.getId(), "1"));
        retVal.setResource(valueSet);

        return retVal;
    }

    @Read()
    public ValueSet getResourceById(@IdParam IdType theId) {
        return valueSetResourceService.getByResourceId(theId.getIdPart());
    }

    @Search()
    public List<ValueSet> getAllValueSets() {
        return valueSetResourceService.getAllValueSets();
    }

    @Delete()
    public void deleteValueSetResource(@IdParam IdType theId) {
        valueSetResourceService.deleteValueSet(theId.getIdPart());
    }

    @Update
    public MethodOutcome updateValueSetResource(@IdParam IdType theId, @ResourceParam ValueSet valueSet) {
        return valueSetResourceService.updateValueSetResource(theId, valueSet);
    }
}
