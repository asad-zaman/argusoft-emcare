package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.OrganizationResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationResourceRepository extends JpaRepository<OrganizationResource, Long> {

    OrganizationResource findByResourceId(String resourceId);
}
