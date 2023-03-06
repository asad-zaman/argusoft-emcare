package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EmcareResourceRepository extends JpaRepository<EmcareResource, Integer> {

    List<EmcareResource> findAllByType(String type);

    List<EmcareResource> findAllByType(String type, Pageable pageable);

    List<EmcareResource> findByTypeContainingAndTextContainingIgnoreCase(String type, String searchString, Pageable pageable);

    List<EmcareResource> findByTypeContainingAndTextContainingIgnoreCase(String type, String searchString);

    List<EmcareResource> findByTypeContainingAndTextContainingIgnoreCaseOrderByCreatedOnDesc(String type, String searchString);

    @Query(value = "SELECT * FROM EMCARE_RESOURCES WHERE TYPE = :type AND (CREATED_ON > :date OR MODIFIED_ON > :date)", nativeQuery = true)
    List<EmcareResource> getByDateAndType(@Param("date") Date date, @Param("type") String type);

    List<EmcareResource> findByTypeAndModifiedOnGreaterThanOrCreatedOnGreaterThanAndFacilityIdIn(String type, Date modifiedOn, Date createdOn, List<String> ids);

    EmcareResource findByResourceId(String resourceId);

    List<EmcareResource> findByFacilityIdIn(List<String> ids, Pageable pageable);

    List<EmcareResource> findByFacilityIdIn(List<String> ids);

    List<EmcareResource> findByResourceIdIn(List<String> ids);

    @Query(value = "SELECT COUNT(*) FROM EMCARE_RESOURCES WHERE (CREATED_ON > :date OR MODIFIED_ON > :date) AND TYPE = 'PATIENT'", nativeQuery = true)
    Long getCountBasedOnDate(@Param("date") Date date);

    @Query(value = "SELECT COUNT(*) FROM EMCARE_RESOURCES WHERE TYPE = 'PATIENT'", nativeQuery = true)
    Long getCount();

}