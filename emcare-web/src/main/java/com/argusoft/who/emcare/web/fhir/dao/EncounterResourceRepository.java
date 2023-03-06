package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.EncounterResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EncounterResourceRepository extends JpaRepository<EncounterResource, Long> {

    public EncounterResource findByResourceId(String id);

    public Page<EncounterResource> findByTextContainingIgnoreCase(String searchString, Pageable page);

    public List<EncounterResource> findByTextContainingIgnoreCase(String searchString);

    List<EncounterResource> findByModifiedOnGreaterThanOrCreatedOnGreaterThan(Date startDate, Date endDate);

    @Query(value = "select * from encounter_resource where text ilike %:searchText% and (created_on >= :minDate or modified_on >= :minDate)", nativeQuery = true)
    List<EncounterResource> fetchByDateAndText(@Param("searchText") String searchText, @Param("minDate") Date minDate);

    List<EncounterResource> findByPatientId(String patientId);

    @Query(value = "SELECT COUNT(*) FROM encounter_resource WHERE (CREATED_ON > :date OR MODIFIED_ON > :date)", nativeQuery = true)
    Long getCountBasedOnDate(@Param("date") Date date);

    @Query(value = "SELECT COUNT(*) FROM encounter_resource", nativeQuery = true)
    Long getCount();
}
