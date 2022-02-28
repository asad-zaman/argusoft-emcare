package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.dao.LocationResourceRepository;
import com.argusoft.who.emcare.web.fhir.model.LocationResource;
import com.argusoft.who.emcare.web.fhir.service.LocationResourceService;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Meta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Transactional
@Service
public class LocationResourceServiceImpl implements LocationResourceService {

    @Autowired
    LocationResourceRepository locationResourceRepository;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);

    @Override
    public LocationResource saveResource(LocationResource locationResource) {
        return locationResourceRepository.save(locationResource);
    }

    @Override
    public Location getByResourceId(String resourceId) {
        LocationResource locationResource = locationResourceRepository.findByResourceId(resourceId);
        Location location = null;
        if (locationResource != null) {
            location = parser.parseResource(Location.class, locationResource.getText());
        }
        return location;
    }

    @Override
    public List<Location> getAllLocations() {
        List<Location> locationList = new ArrayList<>();

        List<LocationResource> locationResources = locationResourceRepository.findAll();
        for (LocationResource locationResource : locationResources) {
            Location patient = parser.parseResource(Location.class, locationResource.getText());
            locationList.add(patient);
        }
        return locationList;
    }

    @Override
    public void deleteLocationResource(String resourceId) {
        LocationResource locationResource = locationResourceRepository.findByResourceId(resourceId);

        if (locationResource == null) {
            throw new ResourceNotFoundException("Resource Not Found");
        } else {
            locationResourceRepository.delete(locationResource);
        }
    }

    @Override
    public MethodOutcome updateLocationResource(IdType theId, Location theLocation) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        theLocation.setMeta(m);


        String locationString = parser.encodeResourceToString(theLocation);
        LocationResource updatableLocationResource = locationResourceRepository.findByResourceId(theId.getIdPart());
        LocationResource locationResource = new LocationResource();
        locationResource.setText(locationString);
        locationResource.setType(CommonConstant.LOCATION_TYPE_STRING);
        locationResource.setResourceId(updatableLocationResource.getResourceId());
        locationResource.setId(updatableLocationResource.getId());

        saveResource(locationResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.LOCATION_TYPE_STRING, theLocation.getId(), "1"));
        retVal.setResource(theLocation);
        return retVal;
    }
}
