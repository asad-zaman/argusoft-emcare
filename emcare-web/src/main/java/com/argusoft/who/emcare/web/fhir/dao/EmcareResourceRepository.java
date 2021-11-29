package com.argusoft.who.emcare.web.fhir.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.argusoft.who.emcare.web.fhir.model.EmcareResource;

@Repository
public interface EmcareResourceRepository extends JpaRepository<EmcareResource,Long>{

}
