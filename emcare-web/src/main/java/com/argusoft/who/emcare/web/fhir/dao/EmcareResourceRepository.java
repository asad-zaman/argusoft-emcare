package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EmcareResourceRepository extends JpaRepository<EmcareResource, Integer> {

    List<EmcareResource> findAllByType(String type);

    List<EmcareResource> findAllByType(String type, Pageable pageable);

    List<EmcareResource> findByTypeContainingAndTextContainingIgnoreCase(String type, String searchString, Pageable pageable);

    List<EmcareResource> findByTypeContainingAndTextContainingIgnoreCase(String type, String searchString);

    List<EmcareResource> findByModifiedOnGreaterThanOrCreatedOnGreaterThan(Date modifiedOn, Date createdOn);

    EmcareResource findByResourceId(String resourceId);

    List<EmcareResource> findByFacilityIdIn(List<String> ids, Pageable pageable);

    List<EmcareResource> findByFacilityIdIn(List<String> ids);
}