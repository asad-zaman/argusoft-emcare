package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.ConditionResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ConditionResourceRepository extends JpaRepository<ConditionResource, Long> {

    public ConditionResource findByResourceId(String id);

    public Page<ConditionResource> findByTextContainingIgnoreCase(String searchString, Pageable page);

    public List<ConditionResource> findByPatientIdOrResourceIdOrEncounterId(String theId, String resourceId, String encounterId);

    public List<ConditionResource> findByTextContainingIgnoreCase(String searchString);

    List<ConditionResource> findByModifiedOnGreaterThanOrCreatedOnGreaterThan(Date startDate, Date endDate);

    @Query(value = "select * from condition_resource where text ilike %:searchText% and (created_on >= :minDate or modified_on >= :minDate)", nativeQuery = true)
    List<ConditionResource> fetchByDateAndText(@Param("searchText") String searchText, @Param("minDate") Date minDate);
}
