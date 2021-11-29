package com.argusoft.who.emcare.web.location.mapper;

import com.argusoft.who.emcare.web.location.dto.LocationMasterDto;
import com.argusoft.who.emcare.web.location.model.LocationMaster;

/**
 *
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

    public static LocationMaster dtoToEntityForLocationMaster(LocationMasterDto locationMasterDto) {

        LocationMaster locationMaster = new LocationMaster();

        locationMaster.setId(locationMasterDto.getId());
        locationMaster.setActive(true);
        locationMaster.setName(locationMasterDto.getName());
        locationMaster.setParent(locationMasterDto.getParent());
        locationMaster.setType(locationMasterDto.getType());

        return locationMaster;
    }
}
