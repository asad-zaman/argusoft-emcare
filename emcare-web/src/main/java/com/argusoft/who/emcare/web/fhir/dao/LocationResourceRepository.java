package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.LocationResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationResourceRepository extends JpaRepository<LocationResource, Long> {

    LocationResource findByResourceId(String resourceId);
}
