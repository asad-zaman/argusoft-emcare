package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.StructureMapResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StructureMapResourceRepository extends JpaRepository<StructureMapResource, Long> {

    public StructureMapResource findByResourceId(String id);

    public Page<StructureMapResource> findByTextContainingIgnoreCase(String searchString, Pageable page);

    public List<StructureMapResource> findByTextContainingIgnoreCase(String searchString);

    List<StructureMapResource> findByModifiedOnGreaterThanOrCreatedOnGreaterThan(Date startDate, Date endDate);

    @Query(value = "SELECT COUNT(*) FROM structure_map_resource WHERE (CREATED_ON > :date OR MODIFIED_ON > :date)", nativeQuery = true)
    Long getCountBasedOnDate(@Param("date") Date date);

    @Query(value = "SELECT COUNT(*) FROM structure_map_resource", nativeQuery = true)
    Long getCount();
}
