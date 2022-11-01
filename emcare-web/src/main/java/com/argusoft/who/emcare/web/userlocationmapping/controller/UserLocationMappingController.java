package com.argusoft.who.emcare.web.userlocationmapping.controller;

import com.argusoft.who.emcare.web.userlocationmapping.model.UserLocationMapping;
import com.argusoft.who.emcare.web.userlocationmapping.service.UserLocationMappingService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api/user-location")
public class UserLocationMappingController {
    
    @Autowired
    UserLocationMappingService userLocationMappingService;
    
    @PostMapping("/save")
    public ResponseEntity<Object> updateUserLocationMapping(
        @RequestBody List<UserLocationMapping> userLocationMappingList 
    ) {
        return userLocationMappingService.saveOrUpdateUserLocationMapping(
            userLocationMappingList
        );
    }
    
    @GetMapping("")
    public ResponseEntity<Object> getUserLocationMappingByUserId(
        @RequestParam(value = "userId", required = false) String userId
    ) {
        return userLocationMappingService.getUserLocationMappingByUserId(userId);
    }
}
