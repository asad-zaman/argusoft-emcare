package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.EncounterResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EncounterResourceRepository extends JpaRepository<EncounterResource, Long> {

    public EncounterResource findByResourceId(String id);

    public Page<EncounterResource> findByTextContainingIgnoreCase(String searchString, Pageable page);

    public List<EncounterResource> findByTextContainingIgnoreCase(String searchString);

    List<EncounterResource> findByModifiedOnGreaterThanOrCreatedOnGreaterThan(Date startDate, Date endDate);
}
