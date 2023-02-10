package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.RelatedPersonResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RelatedPersonResourceRepository extends JpaRepository<RelatedPersonResource, Long> {

    public RelatedPersonResource findByResourceId(String id);

    public Page<RelatedPersonResource> findByTextContainingIgnoreCase(String searchString, Pageable page);

    public List<RelatedPersonResource> findByTextContainingIgnoreCase(String searchString);

    List<RelatedPersonResource> findByModifiedOnGreaterThanOrCreatedOnGreaterThan(Date startDate, Date endDate);
}
