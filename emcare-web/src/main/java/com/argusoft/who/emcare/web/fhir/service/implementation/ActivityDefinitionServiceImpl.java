package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.ActivityDefinitionResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.ActivityDefinitionDto;
import com.argusoft.who.emcare.web.fhir.mapper.EmcareResourceMapper;
import com.argusoft.who.emcare.web.fhir.model.ActivityDefinitionResource;
import com.argusoft.who.emcare.web.fhir.service.ActivityDefinitionResourceService;
import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
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
public class ActivityDefinitionServiceImpl implements ActivityDefinitionResourceService {

    @Autowired
    ActivityDefinitionResourceRepository activityDefinitionResourceRepository;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(true);

    @Override
    public ActivityDefinition saveResource(ActivityDefinition definition) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        definition.setMeta(m);

        String definitionId = null;
        if (definition.getId() != null) {
            definitionId = definition.getIdElement().getIdPart();
        } else {
            definitionId = UUID.randomUUID().toString();
        }
        definition.setId(definitionId);

        String locationString = parser.encodeResourceToString(definition);

        ActivityDefinitionResource activityDefinitionResource = new ActivityDefinitionResource();
        activityDefinitionResource.setText(locationString);
        activityDefinitionResource.setResourceId(definitionId);

        activityDefinitionResourceRepository.save(activityDefinitionResource);

        return definition;
    }

    @Override
    public ActivityDefinition getResourceById(String id) {
        ActivityDefinitionResource activityDefinitionResource = activityDefinitionResourceRepository.findByResourceId(id);
        ActivityDefinition activityDefinition = null;
        if (activityDefinitionResource != null) {
            activityDefinition = parser.parseResource(ActivityDefinition.class, activityDefinitionResource.getText());
        }
        return activityDefinition;
    }

    @Override
    public MethodOutcome updateActivityDefinitionResource(IdType idType, ActivityDefinition definition) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        definition.setMeta(m);


        String encodeResource = parser.encodeResourceToString(definition);
        ActivityDefinitionResource definitionResource = activityDefinitionResourceRepository.findByResourceId(idType.getIdPart());
        definitionResource.setText(encodeResource);

        activityDefinitionResourceRepository.save(definitionResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.ACTIVITY_DEFINITION, definition.getId(), "1"));
        retVal.setResource(definition);
        return retVal;
    }

    @Override
    public List<ActivityDefinition> getAllActivityDefinition(DateParam theDate) {
        List<ActivityDefinition> activityDefinitions = new ArrayList<>();
        List<ActivityDefinitionResource> activityDefinitionResources = new ArrayList<>();

        if (theDate == null) {
            activityDefinitionResources = activityDefinitionResourceRepository.findAll();
        } else {
            activityDefinitionResources = activityDefinitionResourceRepository.findByModifiedOnGreaterThanOrCreatedOnGreaterThan(theDate.getValue(), theDate.getValue());
        }

        for (ActivityDefinitionResource definitionResource : activityDefinitionResources) {
            ActivityDefinition activityDefinition = parser.parseResource(ActivityDefinition.class, definitionResource.getText());
            activityDefinitions.add(activityDefinition);
        }
        return activityDefinitions;
    }

    @Override
    public PageDto getActivityDefinitionPage(Integer pageNo, String searchString) {
        List<ActivityDefinitionDto> definitionDtos = new ArrayList<>();
        Page<ActivityDefinitionResource> activityDefinitionResources = null;
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE);
        Long count = 0L;

        if (searchString != null && !searchString.isEmpty()) {
            activityDefinitionResources = activityDefinitionResourceRepository.findByTextContainingIgnoreCase(searchString, page);
            count = Long.valueOf(activityDefinitionResourceRepository.findByTextContainingIgnoreCase(searchString).size());
        } else {
            activityDefinitionResources = activityDefinitionResourceRepository.findAll(page);
            count = Long.valueOf(activityDefinitionResourceRepository.findAll().size());
        }


        for (ActivityDefinitionResource activityDefinitionResource : activityDefinitionResources) {
            ActivityDefinition activityDefinition = parser.parseResource(ActivityDefinition.class, activityDefinitionResource.getText());
            definitionDtos.add(EmcareResourceMapper.getStructureMapDto(activityDefinition));
        }
        PageDto pageDto = new PageDto();
        pageDto.setList(definitionDtos);
        pageDto.setTotalCount(count);

        return pageDto;
    }
}
