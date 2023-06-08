package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.LocationResourceRepository;
import com.argusoft.who.emcare.web.fhir.dao.OrganizationResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.dto.FacilityMapDto;
import com.argusoft.who.emcare.web.fhir.mapper.EmcareResourceMapper;
import com.argusoft.who.emcare.web.fhir.model.LocationResource;
import com.argusoft.who.emcare.web.fhir.service.LocationResourceService;
import com.argusoft.who.emcare.web.fhir.service.OrganizationResourceService;
import com.argusoft.who.emcare.web.location.dao.LocationMasterDao;
import com.argusoft.who.emcare.web.location.model.LocationMaster;
import com.argusoft.who.emcare.web.location.service.LocationService;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
public class LocationResourceServiceImpl implements LocationResourceService {

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(true);
    @Autowired
    LocationMasterDao locationMasterDao;
    @Autowired
    LocationResourceRepository locationResourceRepository;
    @Autowired
    OrganizationResourceRepository organizationResourceRepository;
    @Autowired
    LocationService locationService;
    @Autowired
    OrganizationResourceService organizationResourceService;

    @Override
    public LocationResource saveResource(Location theLocation) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        theLocation.setMeta(m);

        String locationId = null;
        if (theLocation.getId() != null) {
            locationId = theLocation.getIdElement().getIdPart();
        } else {
            locationId = UUID.randomUUID().toString();
        }
        theLocation.setId(locationId);

        Organization organization = organizationResourceService.getByResourceId(theLocation.getManagingOrganization().getId());
        theLocation.getManagingOrganization().setDisplay(organization.getName());

        String locationString = parser.encodeResourceToString(theLocation);
        List<Extension> extentions = theLocation.getExtension();
        Integer systemLocationId = (Integer) extentions.get(0).getValueAsPrimitive().getValue();
        LocationMaster systemLocation = locationService.getLocationMasterById(systemLocationId);
        LocationResource locationResource = new LocationResource();
        locationResource.setText(locationString);
        locationResource.setOrgId(theLocation.getManagingOrganization().getId());
        locationResource.setOrganizationName(theLocation.getManagingOrganization().getDisplay());
        locationResource.setLocationId(Long.valueOf(systemLocationId));
        locationResource.setLocationName(systemLocation.getName());
        locationResource.setLocationId(Long.valueOf(systemLocationId));
        locationResource.setType(CommonConstant.LOCATION_TYPE_STRING);
        locationResource.setResourceId(locationId);

        locationResource = locationResourceRepository.saveAndFlush(locationResource);

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
    public List<Location> getAllLocations(DateParam theDate) {
        List<Location> locationList = new ArrayList<>();

        List<LocationResource> locationResources;

        if (theDate == null) {
            locationResources = locationResourceRepository.findAll();
        } else {
            locationResources = locationResourceRepository.findByModifiedOnGreaterThanOrCreatedOnGreaterThan(theDate.getValue(), theDate.getValue());
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

        List<Extension> extentions = theLocation.getExtension();
        Integer systemLocationId = (Integer) extentions.get(0).getValueAsPrimitive().getValue();
        LocationMaster systemLocation = locationService.getLocationMasterById(systemLocationId);

        LocationResource updatableLocationResource = locationResourceRepository.findByResourceId(theId.getIdPart());

        LocationResource locationResource = new LocationResource();
        locationResource.setText(locationString);
        locationResource.setType(CommonConstant.LOCATION_TYPE_STRING);
        locationResource.setOrgId(theLocation.getManagingOrganization().getId());
        locationResource.setOrganizationName(theLocation.getManagingOrganization().getDisplay());
        locationResource.setLocationId(Long.valueOf(systemLocationId));
        locationResource.setLocationName(systemLocation.getName());
        locationResource.setLocationId(Long.valueOf(systemLocationId));
        locationResource.setResourceId(updatableLocationResource.getResourceId());
        locationResource.setId(updatableLocationResource.getId());

        locationResourceRepository.save(locationResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.LOCATION_TYPE_STRING, theLocation.getId(), "1"));
        retVal.setResource(theLocation);
        return retVal;
    }

    @Override
    public PageDto getEmCareLocationResourcePage(Integer pageNo, String searchString, Boolean filter) {
        List<FacilityDto> facilityDtos = new ArrayList<>();
        Page<LocationResource> locationResources = null;
        Sort sort = Sort.by("created_on").descending();
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE, sort);
        Long count = 0L;
        String status;

        if(filter==null){
            filter=false;
        }

        status= filter ? "inactive" : "active";

        if (searchString != null && !searchString.isEmpty()) {
            locationResources = locationResourceRepository.searchFacilityByStatus(searchString, status, page);
            count = locationResourceRepository.searchFacilityByStatus(searchString, status);
        } else {
                locationResources = locationResourceRepository.findResourceByStatus(status, page);
                count = Long.valueOf(locationResourceRepository.findResourceByStatus(status));
        }


        for (LocationResource locationResource : locationResources) {
            Location location = parser.parseResource(Location.class, locationResource.getText());
            facilityDtos.add(EmcareResourceMapper.getFacilityDtoForList(location, locationResource));
        }
        PageDto pageDto = new PageDto();
        pageDto.setList(facilityDtos);
        pageDto.setTotalCount(count);

        return pageDto;
    }

    @Override
    public FacilityDto getFacilityDto(String id) {
        LocationResource locationResource = locationResourceRepository.findByResourceId(id);
        if (locationResource == null) {
            return null;
        }
        Location location = getByResourceId(id);
        return EmcareResourceMapper.getFacilityDtoForList(location, locationResource);
    }

    @Override
    public List<FacilityDto> getActiveFacility() {
        List<FacilityDto> facilityDtos = new ArrayList<>();
        List<LocationResource> locationResources = locationResourceRepository.findAll();
        for (LocationResource locationResource : locationResources) {
            Location location = parser.parseResource(Location.class, locationResource.getText());
            facilityDtos.add(EmcareResourceMapper.getFacilityDtoForList(location, locationResource));
        }

        return facilityDtos.stream().filter(f -> "Active".equals(f.getStatus())).collect(Collectors.toList());
    }

    @Override
    public List<FacilityMapDto> getAllFacilityMapDto() {
        List<FacilityMapDto> facilityDtos = new ArrayList<>();
        List<LocationResource> locationResources = locationResourceRepository.findAll();
        for (LocationResource locationResource : locationResources) {
            Location location = parser.parseResource(Location.class, locationResource.getText());
            facilityDtos.add(EmcareResourceMapper.getFacilityMapDto(location, locationResource));
        }
        return facilityDtos;
    }

    @Override
    public List<String> getAllChildFacilityIds(String facilityId) {
        List<String> childFacilityIds = new ArrayList<>();
        FacilityDto facilityDto = getFacilityDto(facilityId);
        List<Integer> locationIds = locationMasterDao.getAllChildLocationId(facilityDto.getLocationId().intValue());
        childFacilityIds = locationResourceRepository.findResourceIdIn(locationIds);
        return childFacilityIds;
    }
}
