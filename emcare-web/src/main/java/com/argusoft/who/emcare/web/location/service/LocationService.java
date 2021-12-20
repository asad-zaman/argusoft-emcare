package com.argusoft.who.emcare.web.location.service;

import com.argusoft.who.emcare.web.location.dto.HierarchyMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationMasterDto;
import com.argusoft.who.emcare.web.location.model.LocationMaster;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author jay
 */
public interface LocationService {

    public ResponseEntity<Object> createHierarchyMaster(HierarchyMasterDto hierarchyMasterDto);

    public ResponseEntity<Object> updateHierarchyMaster(HierarchyMasterDto hierarchyMasterDto);

    public void deleteHierarchyMaster(String id);

    public ResponseEntity<Object> getAllHierarchyMaster();

    public ResponseEntity<Object> getHierarchyMasterById(String type);

    public ResponseEntity<Object> createOrUpdate(LocationMasterDto locationMasterDto);

    public ResponseEntity<Object> getAllLocation();

    public ResponseEntity<Object> updateLocation(LocationMasterDto locationMasterDto);

    public ResponseEntity<Object> deleteLocationById(Integer locationId);

    public ResponseEntity<Object> getLocationById(Integer locationId);
    
    public LocationMaster getLocationMasterById(Integer locationId);
}
