package com.argusoft.who.emcare.web.location.controller;

import com.argusoft.who.emcare.web.location.dto.HierarchyMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationaListDto;
import com.argusoft.who.emcare.web.location.model.LocationMaster;
import com.argusoft.who.emcare.web.location.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
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

    @PostMapping("/hierarchy/create")
    public ResponseEntity<Object> createHierarchyMaster(@RequestBody HierarchyMasterDto hierarchyMasterDto) {
        return locationConfigService.createHierarchyMaster(hierarchyMasterDto);
    }

    @PutMapping("/hierarchy/update")
    public ResponseEntity<Object> updateHierarchyMaster(@RequestBody HierarchyMasterDto hierarchyMasterDto) {
        return locationConfigService.updateHierarchyMaster(hierarchyMasterDto);
    }

    @DeleteMapping("/hierarchy/delete/{id}")
    public void deleteHierarchyMaster(@PathVariable(value = "id") String id) {
        locationConfigService.deleteHierarchyMaster(id);
    }

    @GetMapping("/hierarchy/{type}")
    public ResponseEntity<Object> retriveHierarchyMasterById(@PathVariable(value = "type") String type) {
        return locationConfigService.getHierarchyMasterById(type);
    }

    @GetMapping("/hierarchy")
    public ResponseEntity<Object> retriveAllHierarchyMaster() {
        return locationConfigService.getAllHierarchyMaster();
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createOrUpdate(@RequestBody LocationMasterDto locationMasterDto) {
        return locationConfigService.createOrUpdate(locationMasterDto);
    }

    @GetMapping("")
    public ResponseEntity<Object> getAllLocation() {
        return locationConfigService.getAllLocation();
    }

    @GetMapping("/page")
    public ResponseEntity<Object> getLocationPage(@RequestParam(value = "pageNo") Integer pageNo,
                                                  @RequestParam(value = "orderBy", defaultValue = "name") String orderBy,
                                                  @RequestParam(value = "order") String order,
                                                  @Nullable @RequestParam(value = "search",required = false) String searchString) {
        return locationConfigService.getLocationPage(pageNo, orderBy, order,searchString);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateLocation(@RequestBody LocationMasterDto locationMasterDto) {
        return locationConfigService.updateLocation(locationMasterDto);
    }

    @DeleteMapping("")
    public ResponseEntity<Object> deleteLocation(@RequestParam("locationId") Integer locationId) {
        return locationConfigService.deleteLocationById(locationId);
    }

    @GetMapping("/{locationId}")
    public LocationMaster getLocationById(@PathVariable(value = "locationId") Integer locationId) {
        return locationConfigService.getLocationById(locationId);
    }

    @GetMapping("/type/{type}")
    public List<LocationMaster> getLocationByType(@PathVariable(value = "type") String locationType) {
        return locationConfigService.getLocationByType(locationType);
    }

    @GetMapping("/child/{locationId}")
    public List<LocationaListDto> getChildLocation(@PathVariable(value = "locationId") Integer locationId) {
        return locationConfigService.getChildLocation(locationId);
    }

    @GetMapping("/parent/{locationId}")
    public List<LocationaListDto> getAllParent(@PathVariable(value = "locationId") Integer locationId) {
        return locationConfigService.getAllParent(locationId);
    }
}
