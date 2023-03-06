package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.OperationDefinitionResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.OperationDefinitionDto;
import com.argusoft.who.emcare.web.fhir.mapper.EmcareResourceMapper;
import com.argusoft.who.emcare.web.fhir.model.OperationDefinitionResource;
import com.argusoft.who.emcare.web.fhir.service.OperationDefinitionResourceService;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.OperationDefinition;
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
public class OperationDefinitionResourceServiceImpl implements OperationDefinitionResourceService {

    @Autowired
    OperationDefinitionResourceRepository operationDefinitionResourceRepository;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(true);


    @Override
    public OperationDefinition saveResource(OperationDefinition operationDefinition) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        operationDefinition.setMeta(m);

        String odId = null;
        if (operationDefinition.getId() != null) {
            odId = operationDefinition.getIdElement().getIdPart();
        } else {
            odId = UUID.randomUUID().toString();
        }

        operationDefinition.setId(odId);

        String odString = parser.encodeResourceToString(operationDefinition);

        OperationDefinitionResource operationDefinitionResource = new OperationDefinitionResource();
        operationDefinitionResource.setText(odString);
        operationDefinitionResource.setResourceId(odId);

        operationDefinitionResourceRepository.save(operationDefinitionResource);

        return operationDefinition;
    }

    @Override
    public OperationDefinition getResourceById(String id) {
        OperationDefinitionResource operationDefinitionResource = operationDefinitionResourceRepository.findByResourceId(id);
        OperationDefinition operationDefinition = null;
        if (operationDefinitionResource != null) {
            operationDefinition = parser.parseResource(OperationDefinition.class, operationDefinitionResource.getText());
        }
        return operationDefinition;
    }

    @Override
    public MethodOutcome updateOperationDefinitionResource(IdType idType, OperationDefinition operationDefinition) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        operationDefinition.setMeta(m);


        String encodeResource = parser.encodeResourceToString(operationDefinition);
        OperationDefinitionResource operationDefinitionResource = operationDefinitionResourceRepository.findByResourceId(idType.getIdPart());
        operationDefinitionResource.setText(encodeResource);

        operationDefinitionResourceRepository.save(operationDefinitionResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.OPERATION_DEFINITION, operationDefinition.getId(), "1"));
        retVal.setResource(operationDefinition);
        return retVal;
    }

    @Override
    public List<OperationDefinition> getAllOperationDefinition(DateParam theDate) {
        List<OperationDefinition> operationDefinitions = new ArrayList<>();


        List<OperationDefinitionResource> operationDefinitionResources;

        if (theDate == null) {
            operationDefinitionResources =  operationDefinitionResourceRepository.findAll();
        } else {
            operationDefinitionResources = operationDefinitionResourceRepository.findByModifiedOnGreaterThanOrCreatedOnGreaterThan(theDate.getValue(), theDate.getValue());
        }

        for (OperationDefinitionResource operationDefinitionResource : operationDefinitionResources) {
            OperationDefinition operationDefinition = parser.parseResource(OperationDefinition.class, operationDefinitionResource.getText());
            operationDefinitions.add(operationDefinition);
        }
        return operationDefinitions;
    }

    @Override
    public PageDto getOperationDefinitionPage(Integer pageNo, String searchString) {
        List<OperationDefinitionDto> definitionDtos = new ArrayList<>();
        Page<OperationDefinitionResource> operationDefinitionResources = null;
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE);
        Long count;

        if (searchString != null && !searchString.isEmpty()) {
            operationDefinitionResources = operationDefinitionResourceRepository.findByTextContainingIgnoreCase(searchString, page);
            count = Long.valueOf(operationDefinitionResourceRepository.findByTextContainingIgnoreCase(searchString).size());
        } else {
            operationDefinitionResources = operationDefinitionResourceRepository.findAll(page);
            count = Long.valueOf(operationDefinitionResourceRepository.findAll().size());
        }


        for (OperationDefinitionResource operationDefinitionResource : operationDefinitionResources) {
            OperationDefinition operationDefinition = parser.parseResource(OperationDefinition.class, operationDefinitionResource.getText());
            definitionDtos.add(EmcareResourceMapper.getOperationDefinitionDto(operationDefinition));
        }
        PageDto pageDto = new PageDto();
        pageDto.setList(definitionDtos);
        pageDto.setTotalCount(count);

        return pageDto;
    }

    @Override
    public Bundle getOperationDefinitionCountBasedOnDate(String summaryType, DateParam theDate) {
        Long count = 0l;
        if (summaryType.equalsIgnoreCase(CommonConstant.SUMMARY_TYPE_COUNT)) {
            if (theDate.isEmpty()) {
                count = operationDefinitionResourceRepository.count();
            } else {
                count = operationDefinitionResourceRepository.getCountBasedOnDate(theDate.getValue());
            }
        } else {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.setTotal(count.intValue());
        return bundle;
    }
}
