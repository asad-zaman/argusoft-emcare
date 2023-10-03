package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.rest.api.MethodOutcome;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.resourceprovider.AuditEventResourceProvider;
import com.argusoft.who.emcare.web.fhir.service.AuditEventResourceService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.IdType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditEventResourceProvideTest {
    @InjectMocks
    private AuditEventResourceProvider auditEventResourceProvider;

    @Mock
    private AuditEventResourceService auditEventResourceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetResourceType(){
        Class<? extends IBaseResource> resultClass = auditEventResourceProvider.getResourceType();

        assertEquals(AuditEvent.class,resultClass);
    }

    @Test
    public void testCreateAuditEvent() {
        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setId("1");
        auditEvent.setOutcomeDesc("Test");
        // Set necessary properties of auditEvent

        when(auditEventResourceService.saveResource(any(AuditEvent.class))).thenReturn(auditEvent);

        MethodOutcome methodOutcome = auditEventResourceProvider.createAuditEvent(auditEvent);

        // Assertions
        assertNotNull(methodOutcome);
        assertNotNull(methodOutcome.getId());
        assertEquals(CommonConstant.AUDIT_EVENT, methodOutcome.getId().getResourceType());
        assertEquals("1", methodOutcome.getId().getVersionIdPart());
        assertEquals(auditEvent, methodOutcome.getResource());
        verify(auditEventResourceService, times(1)).saveResource(auditEvent);
    }

    @Test
    public void testUpdateAuditEventResource() {

        IdType idType = new IdType("AuditEvent", "123", "1");
        AuditEvent auditEvent = new AuditEvent();
        // Set necessary properties of auditEvent

        // Mock the behavior of auditEventResourceService.updateAuditEventResource(theId, auditEvent)
        when(auditEventResourceService.updateAuditEventResource(any(IdType.class), any(AuditEvent.class))).thenReturn(new MethodOutcome());

        MethodOutcome methodOutcome = auditEventResourceProvider.updateAuditEventResource(idType, auditEvent);

        // Assertions
        assertNotNull(methodOutcome);
        verify(auditEventResourceService, times(1)).updateAuditEventResource(idType, auditEvent);
    }
}
