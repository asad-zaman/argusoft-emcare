package com.argusoft.who.emcare.web.fhir.resourceprovider;

import java.util.*;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import com.argusoft.who.emcare.web.fhir.service.ObservationResourceService;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BundleResourceProviderTest {

    @Mock
    private ObservationResourceService observationResourceService;

    @Mock
    private EmcareResourceService emcareResourceService;

    @InjectMocks
    private BundleResourceProvider bundleResourceProvider;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }
    @Test
    void getResourceType() {
        Class<Bundle> resultClass = bundleResourceProvider.getResourceType();

        assertEquals(Bundle.class,resultClass);
    }

//    @Test
//    public void testCreateResourcesFromBundle() {
//        String observationId = "hello";
//        Bundle testBundle = new Bundle();
//        List<Bundle.BundleEntryComponent> bundleEntries = new ArrayList<>();
//        Bundle.BundleEntryComponent bundleEntry1 = new Bundle.BundleEntryComponent();
//        bundleEntry1.setRequest(new Bundle.BundleEntryRequestComponent().setMethod(Bundle.HTTPVerb.PUT));
//        bundleEntry1.setResource(new Patient());
//        String testId1 = bundleEntry1.getRequest().getUrlElement().getIdElement().getId();
//
//        Bundle.BundleEntryComponent bundleEntry2 = new Bundle.BundleEntryComponent();
//        bundleEntry2.setRequest(new Bundle.BundleEntryRequestComponent().setMethod(Bundle.HTTPVerb.DELETE));
//
//        String testId2 = bundleEntry2.getRequest().getUrlElement().getIdElement().getId();
//        bundleEntries.add(bundleEntry1);
//        bundleEntries.add(bundleEntry2);
//        testBundle.setEntry(bundleEntries);
//        testBundle.setId(observationId);
//
//        when(emcareResourceService.saveOrUpdateResourceByRequestType(any(), any(), any())).thenReturn("resourceId");
//
//        Bundle resultBundle = bundleResourceProvider.createResourcesFromBundle(testBundle);
////        verify(observationResourceService, times(1)).deleteObservation("observationId");
//
//        assertNotNull(resultBundle);
//        assertEquals(1, resultBundle.getEntry().size());
//        assertNotNull(resultBundle.getEntryFirstRep().getResponse());
//        assertEquals("1", resultBundle.getEntryFirstRep().getResponse().getEtag());
//        assertEquals("Patient/resourceId", resultBundle.getEntryFirstRep().getResponse().getLocation());
//    }


    @Test
    public void testCreateResourcesFromBundle() {
        Bundle testBundle = new Bundle();
        List<Bundle.BundleEntryComponent> bundleEntries = new ArrayList<>();

        Bundle.BundleEntryComponent bundleEntry1 = new Bundle.BundleEntryComponent();
        bundleEntry1.setRequest(new Bundle.BundleEntryRequestComponent().setMethod(Bundle.HTTPVerb.PUT));
        bundleEntry1.setResource(new Patient());
        bundleEntries.add(bundleEntry1);

        Bundle.BundleEntryComponent bundleEntry2 = new Bundle.BundleEntryComponent();
        bundleEntry2.setRequest(new Bundle.BundleEntryRequestComponent().setMethod(Bundle.HTTPVerb.DELETE));
        bundleEntry2.getRequest().getUrlElement().setId("123456");
        bundleEntries.add(bundleEntry2);

        testBundle.setId("hello");
        testBundle.setEntry(bundleEntries);

        when(emcareResourceService.saveOrUpdateResourceByRequestType(any(), any(), any())).thenReturn("resourceId");

        Bundle resultBundle = bundleResourceProvider.createResourcesFromBundle(testBundle);

//        verify(observationResourceService, times(1)).deleteObservation("hello");

        assertNotNull(resultBundle);
        assertEquals(1, resultBundle.getEntry().size());
        assertNotNull(resultBundle.getEntryFirstRep().getResponse());
        assertEquals("1", resultBundle.getEntryFirstRep().getResponse().getEtag());
        assertEquals("Patient/resourceId", resultBundle.getEntryFirstRep().getResponse().getLocation());
    }

}