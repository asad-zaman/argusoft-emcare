package com.argusoft.who.emcare.web.location.controller;

import com.argusoft.who.emcare.web.location.dto.HierarchyMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationMasterDto;
import com.argusoft.who.emcare.web.location.service.LocationConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
@RestController
@RequestMapping("/api/location")
public class LocationController {

    @Autowired
    LocationConfigService locationConfigService;

    @RequestMapping(value = "/hierarchy/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createHierarchyMaster(@RequestBody HierarchyMasterDto hierarchyMasterDto) {
        locationConfigService.createHierarchyMaster(hierarchyMasterDto);
        return ResponseEntity.ok("Success");
    }

    @RequestMapping(value = "/hierarchy/update", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateHierarchyMaster(@RequestBody HierarchyMasterDto hierarchyMasterDto) {
        locationConfigService.updateHierarchyMaster(hierarchyMasterDto);
        return ResponseEntity.ok("Success");
    }

    @RequestMapping(value = "/hierarchy/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteHierarchyMaster(@PathVariable(value = "id") String id) {
        locationConfigService.deleteHierarchyMaster(id);
        return ResponseEntity.ok("Success");
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

}
