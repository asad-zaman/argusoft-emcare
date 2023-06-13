package com.argusoft.who.emcare.web.fhir.dao;

import com.argusoft.who.emcare.web.fhir.model.BundleSyncResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BundleSyncResourceRepository extends JpaRepository<BundleSyncResource,Long> {

}
