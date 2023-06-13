package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.AuditEventResourceService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuditEventResourceProvider implements IResourceProvider {

    @Autowired
    AuditEventResourceService auditEventResourceService;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);


    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return AuditEvent.class;
    }

    @Create
    public MethodOutcome createAuditEvent(@ResourceParam AuditEvent auditEvent) {

        auditEventResourceService.saveResource(auditEvent);
        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.AUDIT_EVENT, auditEvent.getId(), "1"));
        retVal.setResource(auditEvent);
        return retVal;
    }

    @Update
    public MethodOutcome updateAuditEventResource(@IdParam IdType theId, @ResourceParam AuditEvent auditEvent) {
        return auditEventResourceService.updateAuditEventResource(theId, auditEvent);
    }


}
