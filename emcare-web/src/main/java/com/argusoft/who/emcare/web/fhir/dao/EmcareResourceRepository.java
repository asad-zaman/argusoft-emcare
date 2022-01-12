package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmcareResourceRepository extends JpaRepository<EmcareResource, Long> {

    List<EmcareResource> findAllByType(String type);

    List<EmcareResource> findAllByType(String type, Pageable pageable);

    EmcareResource findByResourceId(String resourceId);
}