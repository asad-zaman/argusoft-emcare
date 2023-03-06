package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.RelatedPerson;

import java.util.List;

public interface RelatedPersonResourceService {

    public RelatedPerson saveResource(RelatedPerson relatedPerson);

    public RelatedPerson getResourceById(String id);

    public MethodOutcome updateRelatedPersonResource(IdType idType, RelatedPerson relatedPerson);

    public List<RelatedPerson> getAllRelatedPerson(DateParam theDate);

    public Bundle getRelatedPersonCountBasedOnDate(String summaryType, DateParam theDate);


}
