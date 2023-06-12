package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.Transaction;
import ca.uhn.fhir.rest.annotation.TransactionParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.argusoft.who.emcare.web.fhir.dao.ObservationResourceRepository;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import com.argusoft.who.emcare.web.fhir.service.ObservationResourceService;
import com.google.gson.Gson;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleEntryResponseComponent;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
public class BundleResourceProvider implements IResourceProvider {

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);
    @Autowired
    private EmcareResourceService emcareResourceService;

    Gson gson = new Gson();
    @Autowired
    private ObservationResourceRepository observationResourceRepository;

    @Autowired
    ObservationResourceService observationResourceService;

    /**
     * The getResourceType method comes from IResourceProvider, and must be
     * overridden to indicate what type of resource this provider supplies.
     *
     * @return
     */
    @Override
    public Class<Bundle> getResourceType() {
        return Bundle.class;
    }

    @Transaction
    public Bundle createResourcesFromBundle(@TransactionParam Bundle theBundle) {

        List<BundleEntryComponent> bundleEntries = theBundle.getEntry();
        Bundle retVal = new Bundle();

        for (BundleEntryComponent bundleEntry : bundleEntries) {
            String resourceType = "";
            String resourceId = "";
            String requestType = bundleEntry.getRequest().getMethod().getDisplay();
            if (requestType.equalsIgnoreCase("delete")) {
                String resId = bundleEntry.getRequest().getUrlElement().getIdElement().getId();
                if (Objects.nonNull(resId)) {
                    observationResourceService.deleteObservation(resId);
                }
            } else {
                Resource resource = bundleEntry.getResource();
                resourceType = resource.fhirType();
                resourceId = emcareResourceService.saveOrUpdateResourceByRequestType(resource, resourceType, requestType);

            }
            //Adding resource to return Bundle if it is created.
            if (requestType.equals("PUT")) {
                BundleEntryResponseComponent bundleResponse = new BundleEntryResponseComponent();
                bundleResponse.setEtag("1");
                bundleResponse.setLocation(resourceType + "/" + resourceId);
                bundleResponse.setLastModified(new Date());
                BundleEntryComponent bundleEntryComponent = new BundleEntryComponent();
                bundleEntryComponent.setResponse(bundleResponse);
                retVal.addEntry(bundleEntryComponent);
            }
        }

        return retVal;
    }

}
