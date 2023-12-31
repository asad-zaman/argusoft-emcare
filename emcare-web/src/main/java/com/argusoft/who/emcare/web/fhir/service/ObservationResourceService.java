package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;

import java.util.List;

public interface ObservationResourceService {

    public Observation saveResource(Observation observation);

    public Observation getResourceById(String id);

    public MethodOutcome updateObservationResource(IdType idType, Observation observation);

    public List<Observation> getAllObservation(DateParam theDate, String searchText);
}
