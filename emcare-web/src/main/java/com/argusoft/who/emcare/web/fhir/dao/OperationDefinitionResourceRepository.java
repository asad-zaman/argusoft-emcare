package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.OperationDefinitionResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface OperationDefinitionResourceRepository extends JpaRepository<OperationDefinitionResource, Long> {

    public OperationDefinitionResource findByResourceId(String id);

    public Page<OperationDefinitionResource> findByTextContainingIgnoreCase(String searchString, Pageable page);

    public List<OperationDefinitionResource> findByTextContainingIgnoreCase(String searchString);

    List<OperationDefinitionResource> findByModifiedOnGreaterThanOrCreatedOnGreaterThan(Date startDate, Date endDate);

    @Query(value = "SELECT COUNT(*) FROM operation_definition_resource WHERE (CREATED_ON > :date OR MODIFIED_ON > :date)", nativeQuery = true)
    Long getCountBasedOnDate(@Param("date") Date date);
}
