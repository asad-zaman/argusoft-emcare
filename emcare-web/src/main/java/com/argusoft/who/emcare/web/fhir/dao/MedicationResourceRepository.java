package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.MedicationResource;
import com.argusoft.who.emcare.web.fhir.model.StructureMapResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationResourceRepository extends JpaRepository<MedicationResource, Long> {

    public MedicationResource findByResourceId(String id);

    public Page<MedicationResource> findByTextContainingIgnoreCase(String searchString, Pageable page);

    public List<MedicationResource> findByTextContainingIgnoreCase(String searchString);
}
