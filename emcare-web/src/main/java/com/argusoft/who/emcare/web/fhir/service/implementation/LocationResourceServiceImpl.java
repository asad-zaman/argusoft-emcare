package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.dao.LocationResourceRepository;
import com.argusoft.who.emcare.web.fhir.dao.OrganizationResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.mapper.EmcareResourceMapper;
import com.argusoft.who.emcare.web.fhir.model.LocationResource;
import com.argusoft.who.emcare.web.fhir.service.LocationResourceService;
import com.argusoft.who.emcare.web.fhir.service.OrganizationResourceService;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Transactional
@Service
public class LocationResourceServiceImpl implements LocationResourceService {

    @Autowired
    LocationResourceRepository locationResourceRepository;

    @Autowired
    OrganizationResourceRepository organizationResourceRepository;

    @Autowired
    OrganizationResourceService organizationResourceService;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(true);

    @Override
    public LocationResource saveResource(Location theLocation) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        theLocation.setMeta(m);

        String locationId = UUID.randomUUID().toString();
        theLocation.setId(locationId);

        Organization organization = organizationResourceService.getByResourceId(theLocation.getManagingOrganization().getId());
        theLocation.getManagingOrganization().setDisplay(organization.getName());

        String locationString = parser.encodeResourceToString(theLocation);

        LocationResource locationResource = new LocationResource();
        locationResource.setText(locationString);
        locationResource.setType(CommonConstant.LOCATION_TYPE_STRING);
        locationResource.setResourceId(locationId);

        locationResource = locationResourceRepository.save(locationResource);

        return locationResource;
    }

    @Override
    public Location getByResourceId(String resourceId) {
        LocationResource locationResource = locationResourceRepository.findByResourceId(resourceId);
        Location location = null;
        if (locationResource != null) {
            location = parser.parseResource(Location.class, locationResource.getText());
        } else {
            return null;
        }
        Organization organization = organizationResourceService.getByResourceId(location.getManagingOrganization().getId());
        location.getManagingOrganization().setDisplay(organization.getName());
        return location;
    }

    @Override
    public List<Location> getAllLocations() {
        List<Location> locationList = new ArrayList<>();

        List<LocationResource> locationResources = locationResourceRepository.findAll();
        for (LocationResource locationResource : locationResources) {
            Location location = parser.parseResource(Location.class, locationResource.getText());
            Organization organization = organizationResourceService.getByResourceId(location.getManagingOrganization().getId());
            location.getManagingOrganization().setDisplay(organization.getName());
            locationList.add(location);
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

        Organization organization = organizationResourceService.getByResourceId(theLocation.getManagingOrganization().getId());
        theLocation.getManagingOrganization().setDisplay(organization.getName());

        String locationString = parser.encodeResourceToString(theLocation);
        LocationResource updatableLocationResource = locationResourceRepository.findByResourceId(theId.getIdPart());
        LocationResource locationResource = new LocationResource();
        locationResource.setText(locationString);
        locationResource.setType(CommonConstant.LOCATION_TYPE_STRING);
        locationResource.setResourceId(updatableLocationResource.getResourceId());
        locationResource.setId(updatableLocationResource.getId());

        locationResourceRepository.save(locationResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.LOCATION_TYPE_STRING, theLocation.getId(), "1"));
        retVal.setResource(theLocation);
        return retVal;
    }

    @Override
    public List<Location> getEmCareLocationResourcePage(Integer pageNo, String searchString) {
        List<Location> locationList = new ArrayList<>();
        Page<LocationResource> locationResources = null;
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE);

        if (searchString != null && !searchString.isEmpty()) {
            locationResources = locationResourceRepository.findByTextContainingIgnoreCase(searchString, page);
        } else {
            locationResources = locationResourceRepository.findAll(page);
        }


        for (LocationResource locationResource : locationResources) {
            Location location = parser.parseResource(Location.class, locationResource.getText());
            Organization organization = organizationResourceService.getByResourceId(location.getManagingOrganization().getId());
            location.getManagingOrganization().setDisplay(organization.getName());
            locationList.add(location);
        }

        return locationList;
    }

    @Override
    public FacilityDto getFacilityDto(String id) {
        Location location = getByResourceId(id);
        return EmcareResourceMapper.getFacilityDto(location,id);
    }
}
