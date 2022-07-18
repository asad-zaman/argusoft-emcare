package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.LocationResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationResourceRepository extends JpaRepository<LocationResource, Long> {

    LocationResource findByResourceId(String resourceId);

    List<LocationResource> findByTextContainingIgnoreCase(String searchString);

    Page<LocationResource> findByTextContainingIgnoreCase(String searchString, Pageable page);

    Page<LocationResource> findByTextContainingIgnoreCaseOrOrganizationNameContainingIgnoreCaseOrLocationNameContainingIgnoreCase(String searchString, String searchString1, String searchString2, Pageable page);

    List<LocationResource> findByTextContainingIgnoreCaseOrOrganizationNameContainingIgnoreCaseOrLocationNameContainingIgnoreCase(String searchString, String searchString1, String searchString2);


    @Query(value = "select distinct location_id from location_resources where resource_id in :id ;", nativeQuery = true)
    List<Long> findAllLocationId(@Param("id") List<String> id);

    @Query(value = "select distinct resource_id from location_resources where location_id in :id ;", nativeQuery = true)
    List<String> findResourceIdIn(@Param("id") List<Integer> id);
}
