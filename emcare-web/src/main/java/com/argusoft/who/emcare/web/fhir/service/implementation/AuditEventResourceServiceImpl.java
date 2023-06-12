package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.dao.AuditEventResourceRepository;
import com.argusoft.who.emcare.web.fhir.model.AuditEventResource;
import com.argusoft.who.emcare.web.fhir.model.LibraryResource;
import com.argusoft.who.emcare.web.fhir.service.AuditEventResourceService;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class AuditEventResourceServiceImpl implements AuditEventResourceService {

    @Autowired
    AuditEventResourceRepository auditEventResourceRepository;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(true);

    @Override
    public AuditEvent saveResource(AuditEvent auditEvent) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        auditEvent.setMeta(m);

        String auditEventId = null;
        if (auditEvent.getId() != null) {
            auditEventId = auditEvent.getIdElement().getIdPart();
        } else {
            auditEventId = UUID.randomUUID().toString();
        }

        auditEvent.setId(auditEventId);

        String auditEventString = parser.encodeResourceToString(auditEvent);

        AuditEventResource auditEventResource= new AuditEventResource();
        auditEventResource.setText(auditEventString);
        auditEventResource.setResourceId(auditEventId);

        auditEventResourceRepository.save(auditEventResource);

        return auditEvent;
    }

    @Override
    public AuditEvent getResourceById(String id){
        AuditEventResource auditEventResource = auditEventResourceRepository.findByResourceId(id);
        AuditEvent auditEvent = null;
        if (auditEventResource != null){
            auditEvent = parser.parseResource(AuditEvent.class,auditEventResource.getText());
        }
        return auditEvent;
    }

    @Override
    public MethodOutcome updateAuditEventResource(IdType idType, AuditEvent auditEvent) {

        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        auditEvent.setMeta(m);

        String encodeResource = parser.encodeResourceToString(auditEvent);
        AuditEventResource auditEventResource = auditEventResourceRepository.findByResourceId(idType.getIdPart());
        auditEventResource.setText(encodeResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.AUDIT_EVENT,auditEvent.getId(),"1"));
        retVal.setResource(auditEvent);
        return retVal;
    }
}
