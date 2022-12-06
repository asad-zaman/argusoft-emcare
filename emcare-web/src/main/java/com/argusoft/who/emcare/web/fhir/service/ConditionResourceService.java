package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.IdType;

import java.util.List;

public interface ConditionResourceService {

    public Condition saveResource(Condition encounter);

    public Condition getResourceById(String id);

    public MethodOutcome updateConditionResource(IdType idType, Condition condition);

    public List<Condition> getAllCondition(DateParam theDate, String searchText);

    public List<Condition> getByPatientId(String patientId);
}
