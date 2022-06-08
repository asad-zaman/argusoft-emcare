package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import com.argusoft.who.emcare.web.fhir.model.ValueSetResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.ValueSet;

import java.util.List;

public interface ValueSetResourceService {

    public ValueSetResource saveResource(ValueSet planDefinition);

    public ValueSet getByResourceId(String resourceId);

    public List<ValueSet> getAllValueSets();

    public void deleteValueSet(String resourceId);

    public MethodOutcome updateValueSetResource(IdType theId, ValueSet valueSet);


}
