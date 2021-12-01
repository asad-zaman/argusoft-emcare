package com.argusoft.who.emcare.web.fhir.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import java.util.List;

@Repository
public interface EmcareResourceRepository extends JpaRepository<EmcareResource,Long>{
    
    List<EmcareResource> findAllByType(String type);
}
