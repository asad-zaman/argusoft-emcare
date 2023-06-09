package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.LocationResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LocationResourceRepository extends JpaRepository<LocationResource, Long> {

    LocationResource findByResourceId(String resourceId);

    List<LocationResource> findByModifiedOnGreaterThanOrCreatedOnGreaterThan(Date startDate, Date endDate);

    List<LocationResource> findByTextContainingIgnoreCase(String searchString);

    Page<LocationResource> findByTextContainingIgnoreCase(String searchString, Pageable page);

    @Query(value = "select * from location_resources where cast(cast(text AS json)->> 'status' as text) in :status and (organization_name ilike %:searchString% or location_name ilike %:searchString%)",
            countQuery = "select count(*) from location_resources where cast(cast(text AS json)->> 'status' as text) = :status and (organization_name ilike %:searchString% or location_name ilike %:searchString%)",
            nativeQuery = true)
    Page<LocationResource> searchFacilityByStatus(@Param("searchString") String searchString, @Param("status") List<String> status, Pageable page);

    @Query( value =  "select count(*) from location_resources where cast(cast(text AS json)->> 'status' as text) in :status and organization_name ilike %:searchString% or location_name ilike %:searchString%",
            nativeQuery = true)
    Long searchFacilityByStatus(@Param("searchString") String searchString, @Param("status") List<String> status);


    @Query(value = "select distinct location_id from location_resources where resource_id in :id ;", nativeQuery = true)
    List<Long> findAllLocationId(@Param("id") List<String> id);

    @Query(value = "select distinct resource_id from location_resources where location_id in :id ;", nativeQuery = true)
    List<String> findResourceIdIn(@Param("id") List<Integer> id);

    @Query(value = "select * from location_resources where cast(cast(text AS json)->> 'status' as text) in :status",
            nativeQuery = true)
    Page<LocationResource> findResourceByStatus(@Param("status") List<String> status,Pageable page);

    @Query(value = "select COUNT(*) from location_resources where cast(cast(text AS json)->> 'status' as text) in :status", nativeQuery = true)
    Long findResourceByStatus(@Param("status") List<String> status);
}
