package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;

import java.util.List;

public interface EncounterResourceService {

    public Encounter saveResource(Encounter encounter);

    public Encounter getResourceById(String id);

    public MethodOutcome updateEncounterResource(IdType idType, Encounter encounter);

    public List<Encounter> getAllEncounter(DateParam theDate, String searchText, String theId);

    public Bundle getEncounterCountBasedOnDate(String summaryType, DateParam theDate, String theId);


}
