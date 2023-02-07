package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.model.OrganizationResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;

import java.util.List;

public interface OrganizationResourceService {

    public OrganizationResource saveResource(OrganizationResource organizationResource);

    public Organization getByResourceId(String resourceId);

    public List<Organization> getAllOrganizations();

    public void deleteOrganizationResource(String resourceId);

    public MethodOutcome updateOrganizationResource(IdType theId, Organization theOrganization);

    public PageDto getOrganizationPage(Integer pageNo, String searchString);
}
