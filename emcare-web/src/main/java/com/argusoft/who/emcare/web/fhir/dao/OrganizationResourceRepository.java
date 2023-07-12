package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.OrganizationResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationResourceRepository extends JpaRepository<OrganizationResource, Long> {

    OrganizationResource findByResourceId(String resourceId);

    public Page<OrganizationResource> findByTextContainingIgnoreCase(String searchString, Pageable page);

    public List<OrganizationResource> findByTextContainingIgnoreCase(String searchString);
}
