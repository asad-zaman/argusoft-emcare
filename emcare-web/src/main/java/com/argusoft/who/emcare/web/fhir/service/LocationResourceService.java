package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.model.LocationResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Location;

import java.util.List;

public interface LocationResourceService {

    public LocationResource saveResource(Location theLocation);

    public Location getByResourceId(String resourceId);

    public List<Location> getAllLocations();

    public void deleteLocationResource(String resourceId);

    public MethodOutcome updateLocationResource(IdType theId, Location theLocation);

    public PageDto getEmCareLocationResourcePage(Integer pageNo, String searchString);

    public FacilityDto getFacilityDto(String id);
}
