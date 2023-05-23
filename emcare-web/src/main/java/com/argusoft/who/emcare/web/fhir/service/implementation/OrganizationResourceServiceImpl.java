package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.OrganizationResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.OrganizationDto;
import com.argusoft.who.emcare.web.fhir.mapper.EmcareResourceMapper;
import com.argusoft.who.emcare.web.fhir.model.OrganizationResource;
import com.argusoft.who.emcare.web.fhir.service.OrganizationResourceService;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Transactional
@Service
public class OrganizationResourceServiceImpl implements OrganizationResourceService {

    @Autowired
    OrganizationResourceRepository organizationResourceRepository;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);

    @Override
    public OrganizationResource saveResource(OrganizationResource organizationResource) {
        return organizationResourceRepository.saveAndFlush(organizationResource);
    }

    @Override
    public Organization getByResourceId(String resourceId) {
        OrganizationResource organizationResource = organizationResourceRepository.findByResourceId(resourceId);
        Organization organization = null;
        if (organizationResource != null) {
            organization = parser.parseResource(Organization.class, organizationResource.getText());
        }
        return organization;
    }

    @Override
    public List<Organization> getAllOrganizations() {
        List<Organization> locationList = new ArrayList<>();

        List<OrganizationResource> organizationResources = organizationResourceRepository.findAll();
        for (OrganizationResource organizationResource : organizationResources) {
            Organization organization = parser.parseResource(Organization.class, organizationResource.getText());
            locationList.add(organization);
        }
        return locationList;
    }

    @Override
    public void deleteOrganizationResource(String resourceId) {
        OrganizationResource organizationResource = organizationResourceRepository.findByResourceId(resourceId);

        if (organizationResource == null) {
            throw new ResourceNotFoundException("Organization Resource Not Found");
        } else {
            organizationResourceRepository.delete(organizationResource);
        }
    }

    @Override
    public MethodOutcome updateOrganizationResource(IdType theId, Organization theOrganization) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        theOrganization.setMeta(m);


        String locationString = parser.encodeResourceToString(theOrganization);
        OrganizationResource updatableOrganizationResource = organizationResourceRepository.findByResourceId(theId.getIdPart());
        OrganizationResource organizationResource = new OrganizationResource();
        organizationResource.setText(locationString);
        organizationResource.setType(CommonConstant.ORGANIZATION_TYPE_STRING);
        organizationResource.setResourceId(updatableOrganizationResource.getResourceId());
        organizationResource.setId(updatableOrganizationResource.getId());

        saveResource(organizationResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.ORGANIZATION_TYPE_STRING, theOrganization.getId(), "1"));
        retVal.setResource(theOrganization);
        return retVal;
    }

    @Override
    public PageDto getOrganizationPage(Integer pageNo, String searchString) {
        List<OrganizationDto> organizationDtos = new ArrayList<>();
        Page<OrganizationResource> organizationResources = null;
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE);
        Long count;

        if (searchString != null && !searchString.isEmpty()) {
            organizationResources = organizationResourceRepository.findByTextContainingIgnoreCase(searchString, page);
            count = Long.valueOf(organizationResourceRepository.findByTextContainingIgnoreCase(searchString).size());
        } else {
            organizationResources = organizationResourceRepository.findAll(page);
            count = Long.valueOf(organizationResourceRepository.findAll().size());
        }


        for (OrganizationResource organizationResource : organizationResources) {
            Organization organization = parser.parseResource(Organization.class, organizationResource.getText());
            organizationDtos.add(EmcareResourceMapper.getOrganizationDto(organization));
        }
        PageDto pageDto = new PageDto();
        pageDto.setList(organizationDtos);
        pageDto.setTotalCount(count);
        return pageDto;
    }
}
