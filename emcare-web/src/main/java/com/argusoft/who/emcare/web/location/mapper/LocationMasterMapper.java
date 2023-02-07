package com.argusoft.who.emcare.web.location.mapper;

import com.argusoft.who.emcare.web.location.dto.LocationMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationMasterWithHierarchy;
import com.argusoft.who.emcare.web.location.dto.LocationaListDto;
import com.argusoft.who.emcare.web.location.model.LocationMaster;

/**
 * @author jay
 */
public class LocationMasterMapper {

    private LocationMasterMapper() {
    }

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

    public static LocationMasterWithHierarchy getLocationMasterWithHierarchy(LocationMaster lMaster, String hierarchy) {

        LocationMasterWithHierarchy master = new LocationMasterWithHierarchy();

        master.setActive(lMaster.isActive());
        master.setHierarch(hierarchy);
        master.setId(lMaster.getId());
        master.setName(lMaster.getName());
        master.setParent(lMaster.getParent());
        master.setType(lMaster.getType());

        return master;
    }
}
