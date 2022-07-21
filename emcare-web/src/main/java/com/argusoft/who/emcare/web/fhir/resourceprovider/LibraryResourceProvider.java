package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.LibraryResourceService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Library;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LibraryResourceProvider implements IResourceProvider {

    @Autowired
    LibraryResourceService libraryResourceService;


    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);


    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Library.class;
    }

    @Create
    public MethodOutcome createLibrary(@ResourceParam Library library) {
        libraryResourceService.saveResource(library);
        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.LIBRARY, library.getId(), "1"));
        retVal.setResource(library);
        return retVal;
    }

    @Read()
    public Library getResourceById(@IdParam IdType theId) {
        return libraryResourceService.getResourceById(theId.getIdPart());
    }

    @Update
    public MethodOutcome updateLibraryResource(@IdParam IdType theId, @ResourceParam Library library) {
        return libraryResourceService.updateLibraryResource(theId, library);
    }

    @Search()
    public List<Library> getAllStructureMap() {
        return libraryResourceService.getAllLibrary();
    }
}
