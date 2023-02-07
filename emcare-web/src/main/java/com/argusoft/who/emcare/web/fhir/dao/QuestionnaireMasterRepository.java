package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.QuestionnaireMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface QuestionnaireMasterRepository extends JpaRepository<QuestionnaireMaster, Integer> {

    QuestionnaireMaster findByResourceId(String resourceId);

    List<QuestionnaireMaster> findByModifiedOnGreaterThanOrCreatedOnGreaterThan(Date updatedOn, Date createdOn);

}
