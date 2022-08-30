package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.PlanDefinitionResource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanDefinitionResourceRepository extends JpaRepository<PlanDefinitionResource, Long> {

    PlanDefinitionResource findByResourceId(String resourceId);

}
