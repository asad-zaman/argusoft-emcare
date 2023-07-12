package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.BinaryResource;
import com.argusoft.who.emcare.web.fhir.model.CodeSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BinaryResourceRepository extends JpaRepository<BinaryResource, Long> {

    public BinaryResource findByResourceId(String id);

    public Page<BinaryResource> findByTextContainingIgnoreCase(String searchString, Pageable page);

    public List<BinaryResource> findByTextContainingIgnoreCase(String searchString);

    List<BinaryResource> findByModifiedOnGreaterThanOrCreatedOnGreaterThan(Date startDate, Date endDate);
}
