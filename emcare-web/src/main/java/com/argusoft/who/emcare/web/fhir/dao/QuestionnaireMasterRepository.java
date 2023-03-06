package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.QuestionnaireMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface QuestionnaireMasterRepository extends JpaRepository<QuestionnaireMaster, Integer> {

    QuestionnaireMaster findByResourceId(String resourceId);

    List<QuestionnaireMaster> findByModifiedOnGreaterThanOrCreatedOnGreaterThan(Date updatedOn, Date createdOn);

    @Query(value = "SELECT COUNT(*) FROM questionnaire_master WHERE (CREATED_ON > :date OR MODIFIED_ON > :date)", nativeQuery = true)
    Long getCountBasedOnDate(@Param("date") Date date);

    @Query(value = "SELECT COUNT(*) FROM questionnaire_master", nativeQuery = true)
    Long getCount();

}
