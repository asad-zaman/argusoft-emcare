package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.StructureMapResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StructureMapResourceRepository extends JpaRepository<StructureMapResource, Long> {

    public StructureMapResource findByResourceId(String id);

    public Page<StructureMapResource> findByTextContainingIgnoreCase(String searchString, Pageable page);

    public List<StructureMapResource> findByTextContainingIgnoreCase(String searchString);
}
