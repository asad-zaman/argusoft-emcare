package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.IdType;

public interface AuditEventResourceService {

    public AuditEvent saveResource(AuditEvent auditEvent);

    public AuditEvent getResourceById(String id);

    public MethodOutcome updateAuditEventResource(IdType idType,AuditEvent auditEvent);
}
