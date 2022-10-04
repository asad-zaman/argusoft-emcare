package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.StructureDefinitionRepository;
import com.argusoft.who.emcare.web.fhir.dto.StructureDefinitionDto;
import com.argusoft.who.emcare.web.fhir.mapper.EmcareResourceMapper;
import com.argusoft.who.emcare.web.fhir.model.StructureDefinitionResource;
import com.argusoft.who.emcare.web.fhir.service.StructureDefinitionService;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.StructureDefinition;
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
public class StructureDefinitionServiceImpl implements StructureDefinitionService {

    @Autowired
    StructureDefinitionRepository structureDefinitionRepository;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(true);

    @Override
    public StructureDefinition saveResource(StructureDefinition structureDefinition) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        structureDefinition.setMeta(m);

        String structureMapId = null;
        if (structureDefinition.getId() != null) {
            structureMapId = structureDefinition.getIdElement().getIdPart();
        } else {
            structureMapId = UUID.randomUUID().toString();
        }

        structureDefinition.setId(structureMapId);

        String locationString = parser.encodeResourceToString(structureDefinition);

        StructureDefinitionResource structureDefinitionResource = new StructureDefinitionResource();
        structureDefinitionResource.setText(locationString);
        structureDefinitionResource.setResourceId(structureMapId);

        structureDefinitionResource = structureDefinitionRepository.save(structureDefinitionResource);

        return structureDefinition;
    }

    @Override
    public StructureDefinition getResourceById(String id) {
        StructureDefinitionResource structureDefinitionResource = structureDefinitionRepository.findByResourceId(id);
        StructureDefinition structureDefinition = null;
        if (structureDefinitionResource != null) {
            structureDefinition = parser.parseResource(StructureDefinition.class, structureDefinitionResource.getText());
        }
        return structureDefinition;
    }

    @Override
    public MethodOutcome updateStructureDefinition(IdType theId, StructureDefinition structureDefinition) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        structureDefinition.setMeta(m);


        String encodeResource = parser.encodeResourceToString(structureDefinition);
        StructureDefinitionResource structureDefinitionResource = structureDefinitionRepository.findByResourceId(theId.getIdPart());
        structureDefinitionResource.setText(encodeResource);

        structureDefinitionRepository.save(structureDefinitionResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.STRUCTURE_DEFINITION, structureDefinition.getId(), "1"));
        retVal.setResource(structureDefinition);
        return retVal;
    }

    @Override
    public List<StructureDefinition> getAllStructureMap(DateParam theDate) {
        List<StructureDefinition> structureDefinitions = new ArrayList<>();

        List<StructureDefinitionResource> structureMapResources = new ArrayList<>();

        if (theDate == null) {
            structureMapResources =  structureDefinitionRepository.findAll();
        } else {
            structureMapResources = structureDefinitionRepository.findByModifiedOnGreaterThanOrCreatedOnGreaterThan(theDate.getValue(), theDate.getValue());
        }

        for (StructureDefinitionResource structureDefinitionResource : structureMapResources) {
            StructureDefinition structureDefinition = parser.parseResource(StructureDefinition.class, structureDefinitionResource.getText());
            structureDefinitions.add(structureDefinition);
        }
        return structureDefinitions;
    }

    @Override
    public PageDto getStructureDefinitionPage(Integer pageNo, String searchString) {
        List<StructureDefinitionDto> structureDefinitionDtos = new ArrayList<>();
        Page<StructureDefinitionResource> structureDefinitionResources = null;
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE);
        Long count = 0L;

        if (searchString != null && !searchString.isEmpty()) {
            structureDefinitionResources = structureDefinitionRepository.findByTextContainingIgnoreCase(searchString, page);
            count = Long.valueOf(structureDefinitionRepository.findByTextContainingIgnoreCase(searchString).size());
        } else {
            structureDefinitionResources = structureDefinitionRepository.findAll(page);
            count = Long.valueOf(structureDefinitionRepository.findAll().size());
        }


        for (StructureDefinitionResource structureDefinitionResource : structureDefinitionResources) {
            StructureDefinition structureDefinition = parser.parseResource(StructureDefinition.class, structureDefinitionResource.getText());
            structureDefinitionDtos.add(EmcareResourceMapper.getStructureDefinitionDto(structureDefinition));
        }
        PageDto pageDto = new PageDto();
        pageDto.setList(structureDefinitionDtos);
        pageDto.setTotalCount(count);

        return pageDto;
    }
}
