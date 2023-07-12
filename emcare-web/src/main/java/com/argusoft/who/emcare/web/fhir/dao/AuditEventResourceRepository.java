package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.AuditEventResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditEventResourceRepository extends JpaRepository<AuditEventResource,Long> {

    public  AuditEventResource findByResourceId(String id);


}
