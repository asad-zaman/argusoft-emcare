package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.StructureDefinitionResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StructureDefinitionRepository extends JpaRepository<StructureDefinitionResource, Long> {

    public StructureDefinitionResource findByResourceId(String id);

    public Page<StructureDefinitionResource> findByTextContainingIgnoreCase(String searchString, Pageable page);

    public List<StructureDefinitionResource> findByTextContainingIgnoreCase(String searchString);

    List<StructureDefinitionResource> findByModifiedOnGreaterThanOrCreatedOnGreaterThan(Date startDate, Date endDate);

    @Query(value = "SELECT COUNT(*) FROM structure_definition_resource WHERE (CREATED_ON > :date OR MODIFIED_ON > :date)", nativeQuery = true)
    Long getCountBasedOnDate(@Param("date") Date date);

    @Query(value = "SELECT COUNT(*) FROM structure_definition_resource", nativeQuery = true)
    Long getCount();
}
