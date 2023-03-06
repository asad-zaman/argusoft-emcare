package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.ObservationResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ObservationResourceRepository extends JpaRepository<ObservationResource, Long> {

    public ObservationResource findByResourceId(String id);

    public Page<ObservationResource> findByTextContainingIgnoreCase(String searchString, Pageable page);

    public List<ObservationResource> findByTextContainingIgnoreCase(String searchString);

    public List<ObservationResource> findByModifiedOnGreaterThanOrCreatedOnGreaterThan(Date startDate, Date endDate);

    @Query(value = "select * from observation_resource where text ilike %:searchText% and (created_on >= :minDate or modified_on >= :minDate)", nativeQuery = true)
    List<ObservationResource> fetchByDateAndText(@Param("searchText") String searchText, @Param("minDate") Date minDate);

    public List<ObservationResource> findBySubjectIdAndSubjectType(String patientId, String type);

    @Query(value = "Select obr.* from observation_resource as obr \n" +
            "left join emcare_resources as emr on obr.subject_id = emr.resource_id\n" +
            "left join location_resources as lor on emr.facility_id = lor.resource_id\n" +
            "where emr.facility_id = :facilityId and obr.text ilike %:customCode% ", nativeQuery = true)
    List<ObservationResource> fetchByCustomCode(@Param("facilityId") String facilityId, @Param("customCode") String customCode);

    @Query(value = "SELECT COUNT(*) FROM observation_resource WHERE (CREATED_ON > :date OR MODIFIED_ON > :date)", nativeQuery = true)
    Long getCountBasedOnDate(@Param("date") Date date);

}
