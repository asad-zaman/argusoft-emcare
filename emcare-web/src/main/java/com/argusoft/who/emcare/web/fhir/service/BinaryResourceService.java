package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import org.hl7.fhir.r4.model.Binary;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.IdType;

import java.util.List;

public interface BinaryResourceService {

    public Binary saveResource(Binary binary);

    public Binary getResourceById(String id);

    public MethodOutcome updateBinaryResource(IdType theId, Binary binary);

    public List<Binary> getAllBinaryResource(DateParam theDate);
}
