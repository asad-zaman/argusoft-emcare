package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.StructureDefinitionResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StructureDefinitionRepository extends JpaRepository<StructureDefinitionResource, Long> {

    public StructureDefinitionResource findByResourceId(String id);

    public Page<StructureDefinitionResource> findByTextContainingIgnoreCase(String searchString, Pageable page);

    public List<StructureDefinitionResource> findByTextContainingIgnoreCase(String searchString);
}
