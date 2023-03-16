package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Library;

import java.util.List;

public interface LibraryResourceService {


    public Library saveResource(Library library);

    public Library getResourceById(String id);

    public MethodOutcome updateLibraryResource(IdType idType, Library library);

    public List<Library> getAllLibrary(DateParam theDate);

    public PageDto getLibraryPage(Integer pageNo, String searchString);

    public Bundle getLibraryCountBasedOnDate(String summaryType, DateParam theDate);


}
