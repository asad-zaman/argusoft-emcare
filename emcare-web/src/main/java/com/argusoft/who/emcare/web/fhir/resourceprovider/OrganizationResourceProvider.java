package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.model.OrganizationResource;
import com.argusoft.who.emcare.web.fhir.service.OrganizationResourceService;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class OrganizationResourceProvider implements IResourceProvider {

    @Autowired
    OrganizationResourceService organizationResourceService;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);

    @Override
    public Class<Organization> getResourceType() {
        return Organization.class;
    }


    @Create
    public MethodOutcome createOrganization(@ResourceParam Organization theOrganization) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        theOrganization.setMeta(m);

        String locationId = UUID.randomUUID().toString();
        theOrganization.setId(locationId);

        String locationString = parser.encodeResourceToString(theOrganization);

        OrganizationResource organizationResource = new OrganizationResource();
        organizationResource.setText(locationString);
        organizationResource.setType(CommonConstant.ORGANIZATION_TYPE_STRING);
        organizationResource.setResourceId(locationId);

        organizationResourceService.saveResource(organizationResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.ORGANIZATION_TYPE_STRING, theOrganization.getId(), "1"));
        retVal.setResource(theOrganization);

        return retVal;
    }

    @Read()
    public Organization getResourceById(@IdParam IdType theId) {
        return organizationResourceService.getByResourceId(theId.getIdPart());
    }

    @Search()
    public List<Organization> getAllOrganizations() {
        return organizationResourceService.getAllOrganizations();
    }

    @Delete()
    public void deleteOrganizationResource(@IdParam IdType theId) {
        organizationResourceService.deleteOrganizationResource(theId.getIdPart());
    }

    @Update
    public MethodOutcome updateOrganizationResource(@IdParam IdType theId, @ResourceParam Organization theOrganization) {
        return organizationResourceService.updateOrganizationResource(theId, theOrganization);
    }
}
