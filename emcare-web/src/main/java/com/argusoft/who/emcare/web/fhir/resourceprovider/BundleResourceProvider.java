package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.Transaction;
import ca.uhn.fhir.rest.annotation.TransactionParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import com.google.gson.Gson;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleEntryResponseComponent;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class BundleResourceProvider implements IResourceProvider {

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);
    @Autowired
    private EmcareResourceService emcareResourceService;

    Gson gson = new Gson();

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
        System.out.println("==================="+ gson.toJson(theBundle));
        for (BundleEntryComponent bundleEntry : bundleEntries) {
            String requestType = bundleEntry.getRequest().getMethod().getDisplay();
            Resource resource = bundleEntry.getResource();
            String resourceType = resource.fhirType();

            System.out.println("ResourceType +++++++++++++++++" + resourceType);
            System.out.println("Request Type +++++++++++++++++" + requestType);
            String resourceId = emcareResourceService.saveOrUpdateResourceByRequestType(resource, resourceType, requestType);

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
