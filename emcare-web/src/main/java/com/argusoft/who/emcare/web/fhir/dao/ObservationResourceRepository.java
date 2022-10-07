package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.ObservationResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ObservationResourceRepository extends JpaRepository<ObservationResource, Long> {

    public ObservationResource findByResourceId(String id);

    public Page<ObservationResource> findByTextContainingIgnoreCase(String searchString, Pageable page);

    public List<ObservationResource> findByTextContainingIgnoreCase(String searchString);

    public List<ObservationResource> findByModifiedOnGreaterThanOrCreatedOnGreaterThan(Date startDate, Date endDate);
}
