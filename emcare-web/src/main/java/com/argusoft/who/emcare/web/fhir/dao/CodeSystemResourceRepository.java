package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.CodeSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeSystemResourceRepository extends JpaRepository<CodeSystemResource, Long> {

    public CodeSystemResource findByResourceId(String id);

    public Page<CodeSystemResource> findByTextContainingIgnoreCase(String searchString, Pageable page);

    public List<CodeSystemResource> findByTextContainingIgnoreCase(String searchString);
}
