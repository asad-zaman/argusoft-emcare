package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.ActivityDefinitionResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ActivityDefinitionResourceRepository extends JpaRepository<ActivityDefinitionResource, Long> {

    public ActivityDefinitionResource findByResourceId(String id);

    public Page<ActivityDefinitionResource> findByTextContainingIgnoreCase(String searchString, Pageable page);

    public List<ActivityDefinitionResource> findByTextContainingIgnoreCase(String searchString);

    List<ActivityDefinitionResource> findByModifiedOnGreaterThanOrCreatedOnGreaterThan(Date startDate, Date endDate);
}
