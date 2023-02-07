package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.dao.ValueSetResourceRepository;
import com.argusoft.who.emcare.web.fhir.model.ValueSetResource;
import com.argusoft.who.emcare.web.fhir.service.ValueSetResourceService;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.hl7.fhir.r4.model.ValueSet;

@Transactional
@Service
public class ValueSetResourceServiceImpl implements ValueSetResourceService {

    @Autowired
    ValueSetResourceRepository valueSetResourceRepository;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(true);

    @Override
    public ValueSetResource saveResource(ValueSet valueSet) {

        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        valueSet.setMeta(m);

        String valueSetId = null;
        if (valueSet.getId() != null) {
            valueSetId = valueSet.getIdElement().getIdPart();
        } else {
            valueSetId = UUID.randomUUID().toString();
        }

        valueSet.setId(valueSetId);


        String valueSetString = parser.encodeResourceToString(valueSet);

        ValueSetResource valueSetResource = new ValueSetResource();
        valueSetResource.setText(valueSetString);
        valueSetResource.setType(CommonConstant.VALUESET_TYPE_STRING);
        valueSetResource.setResourceId(valueSetId);

        valueSetResource = valueSetResourceRepository.save(valueSetResource);

        return valueSetResource;
    }

    @Override
    public ValueSet getByResourceId(String resourceId) {
        ValueSetResource valueSetResource = valueSetResourceRepository.findByResourceId(resourceId);
        ValueSet valueSet = null;
        if (valueSetResource != null) {
            valueSet = parser.parseResource(ValueSet.class, valueSetResource.getText());
        }
        return valueSet;
    }

    @Override
    public List<ValueSet> getAllValueSets(DateParam theDate) {
        List<ValueSet> valueSets = new ArrayList<>();

        List<ValueSetResource> valueSetResources;

        if (theDate == null) {
            valueSetResources =  valueSetResourceRepository.findAll();
        } else {
            valueSetResources = valueSetResourceRepository.findByModifiedOnGreaterThanOrCreatedOnGreaterThan(theDate.getValue(), theDate.getValue());
        }
        for (ValueSetResource valueSetResource : valueSetResources) {
            ValueSet valueSet = parser.parseResource(ValueSet.class, valueSetResource.getText());
            valueSets.add(valueSet);
        }
        return valueSets;
    }

    @Override
    public void deleteValueSet(String resourceId) {
        ValueSetResource valueSetResource = valueSetResourceRepository.findByResourceId(resourceId);

        if (valueSetResource == null) {
            throw new ResourceNotFoundException("Value Set Not Found");
        } else {
            valueSetResourceRepository.delete(valueSetResource);
        }
    }

    @Override
    public MethodOutcome updateValueSetResource(IdType theId, ValueSet valueSet) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());

        Integer versionId = 1;

        if (valueSet.getMeta() != null && valueSet.getMeta().getVersionId() != null) {
            versionId = Integer.parseInt(valueSet.getMeta().getVersionId()) + 1;
            m.setVersionId(String.valueOf(versionId));
        }
        valueSet.setMeta(m);

        String valueSetString = parser.encodeResourceToString(valueSet);
        String valueSetId = valueSet.getIdElement().getIdPart();

        ValueSetResource valueSetResource = valueSetResourceRepository.findByResourceId(valueSetId);

        if (valueSetResource == null) {
            valueSetResource = new ValueSetResource();
        }

        valueSetResource.setText(valueSetString);
        valueSetResource.setResourceId(valueSetId);
        valueSetResource.setType(CommonConstant.VALUESET_TYPE_STRING);

        valueSetResourceRepository.save(valueSetResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.VALUESET_TYPE_STRING, valueSetId, String.valueOf(versionId)));
        retVal.setResource(valueSet);

        return retVal;
    }
}
