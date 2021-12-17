package com.argusoft.who.emcare.web.location.service.impl;

import com.argusoft.who.emcare.web.location.dao.HierarchyMasterDao;
import com.argusoft.who.emcare.web.location.dao.LocationMasterDao;
import com.argusoft.who.emcare.web.location.dto.HierarchyMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationaListDto;
import com.argusoft.who.emcare.web.location.mapper.HierarchyMasterMapper;
import com.argusoft.who.emcare.web.location.mapper.LocationMasterMapper;
import com.argusoft.who.emcare.web.location.model.HierarchyMaster;
import com.argusoft.who.emcare.web.location.model.LocationMaster;
import com.argusoft.who.emcare.web.location.service.LocationService;
import com.argusoft.who.emcare.web.secuirty.EmCareSecurityUser;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author jay
 */
@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    HierarchyMasterDao hierarchyMasterDao;

    @Autowired
    LocationMasterDao locationMasterDao;

    @Autowired
    EmCareSecurityUser emCareSecurityUser;

    @Override
    public ResponseEntity<Object> createHierarchyMaster(HierarchyMasterDto hierarchyMasterDto) {
        return ResponseEntity.ok(hierarchyMasterDao.save(HierarchyMasterMapper.dtoToEntityForHierarchyMasterCreate(hierarchyMasterDto)));
    }

    @Override
    public ResponseEntity<Object> updateHierarchyMaster(HierarchyMasterDto hierarchyMasterDto) {
        return ResponseEntity.ok(hierarchyMasterDao.save(HierarchyMasterMapper.dtoToEntityForHierarchyMasterCreate(hierarchyMasterDto)));
    }

    @Override
    public void deleteHierarchyMaster(String id) {
        hierarchyMasterDao.deleteById(id);
    }

    @Override
    public ResponseEntity<Object> getAllHierarchyMaster() {
        List<HierarchyMaster> hierarchyMasterList = hierarchyMasterDao.findAll();
        return ResponseEntity.ok(hierarchyMasterList);
    }

    @Override
    public ResponseEntity<Object> getHierarchyMasterById(String type) {
        return ResponseEntity.ok(hierarchyMasterDao.findById(type).get());
    }

    @Override
    public ResponseEntity<Object> createOrUpdate(LocationMasterDto locationMasterDto) {
        List<LocationMaster> locations = locationMasterDao.findAll();
        if (locations.isEmpty()) {
            return ResponseEntity.ok(locationMasterDao.save(LocationMasterMapper.firstEntity(locationMasterDto)));
        }
        LocationMaster locationMaster = LocationMasterMapper.dtoToEntityForLocationMasterCreate(locationMasterDto);
        LocationMaster lm = locationMasterDao.save(locationMaster);
        return ResponseEntity.ok(lm);
    }

    @Override
    public ResponseEntity<Object> getAllLocation() {
        List<LocationMaster> locationMasters = locationMasterDao.findAll();
        List<LocationaListDto> locationaListDtos = new ArrayList<>();
        for (LocationMaster locationMaster : locationMasters) {
            if (locationMaster.getParent() == 0 || locationMaster.getParent() == null) {
                locationaListDtos.add(LocationMasterMapper.entityToLocationList(locationMaster, "NA"));
            } else {
                locationaListDtos.add(LocationMasterMapper.entityToLocationList(locationMaster, locationMasterDao.findById(locationMaster.getParent().intValue()).get().getName()));
            }
        }
        return ResponseEntity.ok(locationaListDtos);
    }

    @Override
    public ResponseEntity<Object> updateLocation(LocationMasterDto locationMasterDto) {
        LocationMaster locationMaster = LocationMasterMapper.dtoToEntityForLocationMasterUpdate(locationMasterDto);
        LocationMaster updatedLocation = locationMasterDao.save(locationMaster);
        return ResponseEntity.ok(updatedLocation);
    }

    @Override
    public ResponseEntity<Object> deleteLocationById(Integer locationId) {
        List<LocationMaster> childLocations = locationMasterDao.getChildLocation(locationId);
        if (!childLocations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This Location Have Child Location, You Can Not Delete");
        } else {
            locationMasterDao.deleteById(locationId);
        }
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<Object> getLocationById(Integer locationId) {
        LocationMaster locationMaster = locationMasterDao.findById(locationId).get();
        return ResponseEntity.ok(locationMaster);
    }

}
