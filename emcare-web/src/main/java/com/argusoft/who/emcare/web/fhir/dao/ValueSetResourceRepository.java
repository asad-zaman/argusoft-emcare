package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.ValueSetResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValueSetResourceRepository extends JpaRepository<ValueSetResource, Long> {

    ValueSetResource findByResourceId(String resourceId);

}
