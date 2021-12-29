package com.argusoft.who.emcare.web.location.mapper;

import com.argusoft.who.emcare.web.location.dto.LocationMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationaListDto;
import com.argusoft.who.emcare.web.location.model.LocationMaster;

/**
 * @author jay
 */
public class LocationMasterMapper {

    public static LocationMaster firstEntity(LocationMasterDto locationMasterDto) {

        LocationMaster locationMaster = new LocationMaster();

        locationMaster.setActive(true);
        locationMaster.setName(locationMasterDto.getName());
        locationMaster.setParent(0L);
        locationMaster.setType(locationMasterDto.getType());
        return locationMaster;
    }

    public static LocationMaster dtoToEntityForLocationMasterCreate(LocationMasterDto locationMasterDto) {

        LocationMaster locationMaster = new LocationMaster();

        locationMaster.setId(locationMasterDto.getId());
        locationMaster.setActive(true);
        locationMaster.setName(locationMasterDto.getName());
        if (locationMasterDto.getParent() == null) {
            locationMaster.setParent(0L);
        } else {
            locationMaster.setParent(locationMasterDto.getParent());
        }
        locationMaster.setType(locationMasterDto.getType());

        return locationMaster;
    }

    public static LocationMaster dtoToEntityForLocationMasterUpdate(LocationMasterDto locationMasterDto) {

        LocationMaster locationMaster = new LocationMaster();

        locationMaster.setId(locationMasterDto.getId());
        locationMaster.setActive(true);
        locationMaster.setName(locationMasterDto.getName());
        locationMaster.setParent(locationMasterDto.getParent());
        locationMaster.setType(locationMasterDto.getType());
        return locationMaster;
    }

    public static LocationaListDto entityToLocationList(LocationMaster lMaster, String locationName) {

        LocationaListDto locationDto = new LocationaListDto();

        locationDto.setId(lMaster.getId());
        locationDto.setActive(lMaster.isActive());
        locationDto.setName(lMaster.getName());
        locationDto.setParent(lMaster.getParent());
        locationDto.setType(lMaster.getType());
        locationDto.setCreatedBy(lMaster.getCreatedBy());
        locationDto.setCreatedOn(lMaster.getCreatedOn());
        locationDto.setModifiedBy(lMaster.getModifiedBy());
        locationDto.setModifiedOn(lMaster.getModifiedOn());
        locationDto.setParentName(locationName);

        return locationDto;
    }
}
