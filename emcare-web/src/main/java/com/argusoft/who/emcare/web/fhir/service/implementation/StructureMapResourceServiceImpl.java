package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.StructureMapResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.StructureMapDto;
import com.argusoft.who.emcare.web.fhir.mapper.EmcareResourceMapper;
import com.argusoft.who.emcare.web.fhir.model.StructureMapResource;
import com.argusoft.who.emcare.web.fhir.service.StructureMapResourceService;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.StructureMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class StructureMapResourceServiceImpl implements StructureMapResourceService {

    @Autowired
    StructureMapResourceRepository structureMapResourceRepository;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(true);

    @Override
    public StructureMap saveResource(StructureMap structureMap) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        structureMap.setMeta(m);

        String structureMapId = null;
        if (structureMap.getId() != null) {
            structureMapId = structureMap.getIdElement().getIdPart();
        } else {
            structureMapId = UUID.randomUUID().toString();
        }

        structureMap.setId(structureMapId);

        String locationString = parser.encodeResourceToString(structureMap);

        StructureMapResource structureMapResource = new StructureMapResource();
        structureMapResource.setText(locationString);
        structureMapResource.setResourceId(structureMapId);

        structureMapResource = structureMapResourceRepository.save(structureMapResource);

        return structureMap;
    }

    @Override
    public StructureMap getResourceById(String id) {
        StructureMapResource structureMapResource = structureMapResourceRepository.findByResourceId(id);
        StructureMap structureMap = null;
        if (structureMapResource != null) {
            structureMap = parser.parseResource(StructureMap.class, structureMapResource.getText());
        }
        return structureMap;
    }

    @Override
    public MethodOutcome updateStructureMapResource(IdType idType, StructureMap structureMap) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        structureMap.setMeta(m);


        String encodeResource = parser.encodeResourceToString(structureMap);
        StructureMapResource structureMapResource = structureMapResourceRepository.findByResourceId(idType.getIdPart());
        StructureMapResource definitionResource = new StructureMapResource();
        definitionResource.setText(encodeResource);
        definitionResource.setResourceId(structureMapResource.getResourceId());
        definitionResource.setId(structureMapResource.getId());

        structureMapResourceRepository.save(definitionResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.STRUCTURE_MAP, structureMap.getId(), "1"));
        retVal.setResource(structureMap);
        return retVal;
    }

    @Override
    public List<StructureMap> getAllStructureMap(DateParam theDate) {
        List<StructureMap> structureMaps = new ArrayList<>();

        List<StructureMapResource> structureMapResources = new ArrayList<>();

        if (theDate == null) {
            structureMapResources =  structureMapResourceRepository.findAll();
        } else {
            structureMapResources = structureMapResourceRepository.findByModifiedOnGreaterThanOrCreatedOnGreaterThan(theDate.getValue(), theDate.getValue());
        }
        for (StructureMapResource structureMapResource : structureMapResources) {
            StructureMap structureMap = parser.parseResource(StructureMap.class, structureMapResource.getText());
            structureMaps.add(structureMap);
        }
        return structureMaps;
    }

    @Override
    public PageDto getStructureMapPage(Integer pageNo, String searchString) {
        List<StructureMapDto> structureMaps = new ArrayList<>();
        Page<StructureMapResource> structureMapResources = null;
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE);
        Long count = 0L;

        if (searchString != null && !searchString.isEmpty()) {
            structureMapResources = structureMapResourceRepository.findByTextContainingIgnoreCase(searchString, page);
            count = Long.valueOf(structureMapResourceRepository.findByTextContainingIgnoreCase(searchString).size());
        } else {
            structureMapResources = structureMapResourceRepository.findAll(page);
            count = Long.valueOf(structureMapResourceRepository.findAll().size());
        }


        for (StructureMapResource structureMapResource : structureMapResources) {
            StructureMap structureMap = parser.parseResource(StructureMap.class, structureMapResource.getText());
            structureMaps.add(EmcareResourceMapper.getStructureMapDto(structureMap));
        }
        PageDto pageDto = new PageDto();
        pageDto.setList(structureMaps);
        pageDto.setTotalCount(count);

        return pageDto;
    }
}
