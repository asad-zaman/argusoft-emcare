package com.argusoft.who.emcare.web.userLocationMapping.service.impl;

import com.argusoft.who.emcare.web.secuirty.EmCareSecurityUser;
import com.argusoft.who.emcare.web.userLocationMapping.dao.UserLocationMappingRepository;
import com.argusoft.who.emcare.web.userLocationMapping.model.UserLocationMapping;
import com.argusoft.who.emcare.web.userLocationMapping.service.UserLocationMappingService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserLocationMappingServiceImpl implements UserLocationMappingService {
    
    @Autowired
    EmCareSecurityUser emCareSecurityUser;
    
    @Autowired
    UserLocationMappingRepository userLocationMappingRepository;

    @Override
    public ResponseEntity<Object> getUserLocationMappingByUserId(String userId){
        List<UserLocationMapping> userLocationMappingList 
                = userLocationMappingRepository.findByUserId(userId);
        return ResponseEntity.ok(userLocationMappingList);
    }

    @Override
    public ResponseEntity<Object> saveOrUpdateUserLocationMapping(
        List<UserLocationMapping> userLocationMappingList
    ) {
        userLocationMappingList.stream().forEach(userLocationMap -> {
            userLocationMappingRepository.save(userLocationMap);
        });
        return ResponseEntity.ok(userLocationMappingList);
    }
    
}
