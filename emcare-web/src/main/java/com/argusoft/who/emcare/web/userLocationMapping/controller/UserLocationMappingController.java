package com.argusoft.who.emcare.web.userLocationMapping.controller;

import com.argusoft.who.emcare.web.userLocationMapping.model.UserLocationMapping;
import com.argusoft.who.emcare.web.userLocationMapping.service.UserLocationMappingService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api/user-location")
public class UserLocationMappingController {
    
    @Autowired
    UserLocationMappingService userLocationMappingService;
    
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<Object> updateUserLocationMapping(
        @RequestBody List<UserLocationMapping> userLocationMappingList 
    ) {
        return userLocationMappingService.saveOrUpdateUserLocationMapping(
            userLocationMappingList
        );
    }
    
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Object> getUserLocationMappingByUserId(
        @RequestParam(value = "userId", required = false) String userId
    ) {
        return userLocationMappingService.getUserLocationMappingByUserId(userId);
    }
}
