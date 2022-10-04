package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.StructureMap;

import java.util.List;

public interface StructureMapResourceService {


    public StructureMap saveResource(StructureMap structureMap);

    public StructureMap getResourceById(String id);

    public MethodOutcome updateStructureMapResource(IdType idType, StructureMap structureMap);

    public List<StructureMap> getAllStructureMap(DateParam theDate);

    public PageDto getStructureMapPage(Integer pageNo, String searchString);

}
