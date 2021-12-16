package com.argusoft.who.emcare.web.userLocationMapping.service;

import com.argusoft.who.emcare.web.userLocationMapping.model.UserLocationMapping;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface UserLocationMappingService {
    
    public ResponseEntity<Object> saveOrUpdateUserLocationMapping(
        List<UserLocationMapping> userLocationMappingList
    );
    
    public ResponseEntity<Object> getUserLocationMappingByUserId(String userId);
    
}
