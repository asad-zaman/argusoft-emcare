package com.argusoft.who.emcare.web.userLocationMapping.dao;

import com.argusoft.who.emcare.web.userLocationMapping.model.UserLocationMapping;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLocationMappingRepository extends 
        JpaRepository<UserLocationMapping, Integer> {

    List<UserLocationMapping> findByUserId(String userId);
    
    List<UserLocationMapping> findByRegRequestFromAndIsFirst(
            String regRequestFrom, boolean isFirst);
    
}
