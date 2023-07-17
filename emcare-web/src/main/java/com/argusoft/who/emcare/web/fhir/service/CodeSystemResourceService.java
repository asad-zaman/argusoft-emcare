package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.IdType;

import java.util.List;

public interface CodeSystemResourceService {


    public CodeSystem saveResource(CodeSystem codeSystem);

    public CodeSystem getResourceById(String id);

    public MethodOutcome updateCodeSystem(IdType theId, CodeSystem codeSystem);

    public List<CodeSystem> getAllCodeSystem(DateParam theDate);

    public PageDto getCodeSystemPage(Integer pageNo, String searchString);

    public Bundle getCodeSystemDataForGoogleFhirDataPipes(String summaryType, Integer count, String total);
}
