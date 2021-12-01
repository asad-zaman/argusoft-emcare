package com.argusoft.who.emcare.web.location.service.impl;

import com.argusoft.who.emcare.web.location.dao.HierarchyMasterDao;
import com.argusoft.who.emcare.web.location.dao.LocationMasterDao;
import com.argusoft.who.emcare.web.location.dto.HierarchyMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationMasterDto;
import com.argusoft.who.emcare.web.location.mapper.HierarchyMasterMapper;
import com.argusoft.who.emcare.web.location.mapper.LocationMasterMapper;
import com.argusoft.who.emcare.web.location.model.HierarchyMaster;
import com.argusoft.who.emcare.web.location.model.LocationMaster;
import com.argusoft.who.emcare.web.location.service.LocationConfigService;
import com.argusoft.who.emcare.web.secuirty.EmCareSecurityUser;
import java.util.LinkedList;
import java.util.List;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author jay
 */
@Service
public class LocationConfigServiceImpl implements LocationConfigService {

    @Autowired
    HierarchyMasterDao hierarchyMasterDao;

    @Autowired
    LocationMasterDao locationMasterDao;

    @Autowired
    EmCareSecurityUser emCareSecurityUser;

    @Override
    public void createHierarchyMaster(HierarchyMasterDto hierarchyMasterDto) {
        String userId = emCareSecurityUser.getLoggedInUser().getSubject();
        hierarchyMasterDao.save(HierarchyMasterMapper.dtoToEntityForHierarchyMasterCreate(hierarchyMasterDto, userId));
    }

    @Override
    public void updateHierarchyMaster(HierarchyMasterDto hierarchyMasterDto) {
        String userId = emCareSecurityUser.getLoggedInUser().getSubject();
        HierarchyMaster hierarchyMaster = hierarchyMasterDao.findById(hierarchyMasterDto.getHierarchyType()).get();
        hierarchyMasterDao.save(HierarchyMasterMapper.dtoToEntityForHierarchyMasterUpdate(hierarchyMasterDto,hierarchyMaster, userId));
    }

    @Override
    public void deleteHierarchyMaster(String id) {
        hierarchyMasterDao.deleteById(id);
    }

    @Override
    public ResponseEntity<Object> getAllHierarchyMaster() {
        List<HierarchyMaster> hierarchyMasterList = hierarchyMasterDao.findAll();

        List<HierarchyMasterDto> hierarchyMasterDtos = new LinkedList<>();
        for (HierarchyMaster hierarchyMaster : hierarchyMasterList) {
            hierarchyMasterDtos.add(HierarchyMasterMapper.entityToDtoForHierarchyMaster(hierarchyMaster));
        }
        return ResponseEntity.ok(hierarchyMasterDtos);
    }

    @Override
    public ResponseEntity<Object> getHierarchyMasterById(String type) {
        return ResponseEntity.ok(HierarchyMasterMapper.entityToDtoForHierarchyMaster(hierarchyMasterDao.findById(type).get()));
    }

    @Override
    public ResponseEntity<Object> createOrUpdate(LocationMasterDto locationMasterDto) {
        String userId = emCareSecurityUser.getLoggedInUser().getSubject();
        List<LocationMaster> locations = locationMasterDao.findAll();
        if (locations.isEmpty()) {
            locationMasterDao.save(LocationMasterMapper.firstEntity(locationMasterDto, userId));
            return ResponseEntity.ok("Success");
        }
        LocationMaster locationMaster = LocationMasterMapper.dtoToEntityForLocationMasterCreate(locationMasterDto, userId);
        LocationMaster lm = locationMasterDao.save(locationMaster);

        return ResponseEntity.ok(lm);
    }

    @Override
    public ResponseEntity<Object> getAllLocation() {
        AccessToken accessToken = emCareSecurityUser.getLoggedInUser();
        return ResponseEntity.ok(locationMasterDao.findAll());
    }

    @Override
    public ResponseEntity<Object> updateLocation(LocationMasterDto locationMasterDto) {
        String userId = emCareSecurityUser.getLoggedInUser().getSubject();
        LocationMaster lm = locationMasterDao.findById(locationMasterDto.getId()).get();
        LocationMaster locationMaster = LocationMasterMapper.dtoToEntityForLocationMasterUpdate(locationMasterDto,lm, userId);
        LocationMaster updatedLocation = locationMasterDao.save(locationMaster);
        return ResponseEntity.ok(updatedLocation);
    }

    @Override
    public ResponseEntity<Object> deleteLocationById(Integer locationId) {
        LocationMaster locationMaster = locationMasterDao.findById(locationId).get();
        List<LocationMaster> childLocations = locationMasterDao.getChildLocation(locationId);
        if (!childLocations.isEmpty()) {
//            throw new EmCareException("This Location Have Child Location", 400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This Location Have Child Location, You Can Not Delete");
        } else {
            locationMasterDao.deleteById(locationId);
        }
        return ResponseEntity.ok("Success");
    }

}
