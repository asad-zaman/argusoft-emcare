package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.PlanDefinitionResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PlanDefinitionResourceRepository extends JpaRepository<PlanDefinitionResource, Long> {

    PlanDefinitionResource findByResourceId(String resourceId);

    List<PlanDefinitionResource> findByModifiedOnGreaterThanOrCreatedOnGreaterThan(Date startDate, Date endDate);

    @Query(value = "SELECT COUNT(*) FROM plan_definition_resources WHERE (CREATED_ON > :date OR MODIFIED_ON > :date)", nativeQuery = true)
    Long getCountBasedOnDate(@Param("date") Date date);

}
