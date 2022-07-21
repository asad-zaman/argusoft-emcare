package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.LibraryResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibraryResourceRepository extends JpaRepository<LibraryResource, Long> {

    public LibraryResource findByResourceId(String id);

    public Page<LibraryResource> findByTextContainingIgnoreCase(String searchString, Pageable page);

    public List<LibraryResource> findByTextContainingIgnoreCase(String searchString);
}
