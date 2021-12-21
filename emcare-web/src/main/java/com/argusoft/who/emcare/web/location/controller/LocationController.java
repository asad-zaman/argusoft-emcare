package com.argusoft.who.emcare.web.location.controller;

import com.argusoft.who.emcare.web.location.dto.HierarchyMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationMasterDto;
import com.argusoft.who.emcare.web.location.model.LocationMaster;
import com.argusoft.who.emcare.web.location.service.LocationService;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author jay
 */
@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api/location")
public class LocationController {

    @Autowired
    LocationService locationConfigService;

    @Autowired
    private HttpServletRequest request;

    @RequestMapping(value = "/hierarchy/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createHierarchyMaster(@RequestBody HierarchyMasterDto hierarchyMasterDto) {
        return locationConfigService.createHierarchyMaster(hierarchyMasterDto);
    }

    @RequestMapping(value = "/hierarchy/update", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateHierarchyMaster(@RequestBody HierarchyMasterDto hierarchyMasterDto) {
        return locationConfigService.updateHierarchyMaster(hierarchyMasterDto);
    }

    @RequestMapping(value = "/hierarchy/delete/{id}", method = RequestMethod.DELETE)
    public void deleteHierarchyMaster(@PathVariable(value = "id") String id) {
        locationConfigService.deleteHierarchyMaster(id);
    }

    @RequestMapping(value = "/hierarchy/{type}", method = RequestMethod.GET)
    public ResponseEntity<Object> retriveHierarchyMasterById(@PathVariable(value = "type") String type) {
        return locationConfigService.getHierarchyMasterById(type);
    }

    @RequestMapping(value = "/hierarchy", method = RequestMethod.GET)
    public ResponseEntity<Object> retriveAllHierarchyMaster() {
        return locationConfigService.getAllHierarchyMaster();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createOrUpdate(@RequestBody LocationMasterDto locationMasterDto) {
        return locationConfigService.createOrUpdate(locationMasterDto);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllLocation() {
        return locationConfigService.getAllLocation();
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateLocation(@RequestBody LocationMasterDto locationMasterDto) {
        return locationConfigService.updateLocation(locationMasterDto);
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteLocation(@RequestParam("locationId") Integer locationId) {
        return locationConfigService.deleteLocationById(locationId);
    }
    
    @RequestMapping(value = "/{locationId}", method = RequestMethod.GET)
    public LocationMaster getLocationById(@PathVariable(value = "locationId") Integer locationId) {
        return locationConfigService.getLocationById(locationId);
    }
}
