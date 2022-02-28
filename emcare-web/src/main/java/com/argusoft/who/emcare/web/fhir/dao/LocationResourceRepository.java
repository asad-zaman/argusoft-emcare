package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.LocationResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationResourceRepository extends JpaRepository<LocationResource, Long> {

    LocationResource findByResourceId(String resourceId);

    List<LocationResource> findByTextContainingIgnoreCase(String searchString);

    Page<LocationResource> findByTextContainingIgnoreCase(String searchString, Pageable page);
}
