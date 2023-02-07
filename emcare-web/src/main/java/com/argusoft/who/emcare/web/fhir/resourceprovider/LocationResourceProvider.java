package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.LocationResourceService;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocationResourceProvider implements IResourceProvider {

    @Autowired
    LocationResourceService locationResourceService;

    @Autowired
    OrganizationResourceProvider organizationResourceProvider;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);

    @Override
    public Class<Location> getResourceType() {
        return Location.class;
    }

    @Create
    public MethodOutcome createLocation(@ResourceParam Location theLocation) {
        locationResourceService.saveResource(theLocation);
        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.LOCATION_TYPE_STRING, theLocation.getId(), "1"));
        retVal.setResource(theLocation);

        return retVal;
    }

    @Read()
    public Location getResourceById(@IdParam IdType theId) {
        return locationResourceService.getByResourceId(theId.getIdPart());
    }

    @Search()
    public List<Location> getAllLocations(@OptionalParam(name = CommonConstant.RESOURCE_LAST_UPDATED_AT) DateParam theDate) {
        return locationResourceService.getAllLocations(theDate);
    }

    @Delete()
    public void deleteLocationResource(@IdParam IdType theId) {
        locationResourceService.deleteLocationResource(theId.getIdPart());
    }

    @Update
    public MethodOutcome updateLocationResource(@IdParam IdType theId, @ResourceParam Location theLocation) {
        return locationResourceService.updateLocationResource(theId, theLocation);
    }


}
