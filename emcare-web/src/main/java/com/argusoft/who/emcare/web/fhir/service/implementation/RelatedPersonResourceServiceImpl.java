package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.dao.EmcareResourceRepository;
import com.argusoft.who.emcare.web.fhir.dao.RelatedPersonResourceRepository;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import com.argusoft.who.emcare.web.fhir.model.RelatedPersonResource;
import com.argusoft.who.emcare.web.fhir.service.RelatedPersonResourceService;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class RelatedPersonResourceServiceImpl implements RelatedPersonResourceService {

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(true);

    @Autowired
    RelatedPersonResourceRepository relatedPersonResourceRepository;

    @Autowired
    EmcareResourceRepository emcareResourceRepository;

    @Override
    public RelatedPerson saveResource(RelatedPerson relatedPerson) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        relatedPerson.setMeta(m);

        String relatedPersonId = null;
        if (relatedPerson.getId() != null) {
            relatedPersonId = relatedPerson.getIdElement().getIdPart();
        } else {
            relatedPersonId = UUID.randomUUID().toString();
        }
        relatedPerson.setId(relatedPersonId);

        String locationString = parser.encodeResourceToString(relatedPerson);

        RelatedPersonResource relatedPersonResource = new RelatedPersonResource();
        relatedPersonResource.setText(locationString);
        String patientId = relatedPerson.getPatient().getId();
        if (patientId == null) {
            List<EmcareResource> emcareResources = emcareResourceRepository.findByTypeContainingAndTextContainingIgnoreCase(CommonConstant.FHIR_PATIENT, relatedPersonId);
            patientId = !emcareResources.isEmpty() ? emcareResources.get(1).getResourceId() : null;
        }
        relatedPersonResource.setPatientId(patientId);
        relatedPersonResource.setResourceId(relatedPersonId);

        relatedPersonResourceRepository.save(relatedPersonResource);

        return relatedPerson;
    }

    @Override
    public RelatedPerson getResourceById(String id) {
        RelatedPersonResource relatedPersonResource = relatedPersonResourceRepository.findByResourceId(id);
        RelatedPerson relatedPerson = null;
        if (relatedPersonResource != null) {
            relatedPerson = parser.parseResource(RelatedPerson.class, relatedPersonResource.getText());
        }
        return relatedPerson;
    }

    @Override
    public MethodOutcome updateRelatedPersonResource(IdType idType, RelatedPerson relatedPerson) {
        Integer version = 1;
        version = Integer.parseInt(relatedPerson.getMeta().getVersionId());
        if (version > 0) {
            version++;
        }
        Meta m = new Meta();
        m.setVersionId(version.toString());
        m.setLastUpdated(new Date());
        relatedPerson.setMeta(m);


        String encodeResource = parser.encodeResourceToString(relatedPerson);
        RelatedPersonResource personResource = relatedPersonResourceRepository.findByResourceId(idType.getIdPart());
        personResource.setText(encodeResource);
        personResource.setPatientId(relatedPerson.getPatient().getTypeElement().getId());

        relatedPersonResourceRepository.save(personResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.RELATED_PERSON, relatedPerson.getId(), version.toString()));
        retVal.setResource(relatedPerson);
        return retVal;
    }

    @Override
    public Bundle getRelatedPersonCountBasedOnDate(String summaryType, DateParam theDate) {
        Long count = 0l;
        if (summaryType.equalsIgnoreCase(CommonConstant.SUMMARY_TYPE_COUNT)) {
            if (theDate.isEmpty()) {
                count = relatedPersonResourceRepository.count();
            } else {
                count = relatedPersonResourceRepository.getCountBasedOnDate(theDate.getValue());
            }
        } else {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.setTotal(count.intValue());
        return bundle;
    }

    @Override
    public List<RelatedPerson> getAllRelatedPerson(DateParam theDate) {
        List<RelatedPerson> relatedPeople = new ArrayList<>();
        List<RelatedPersonResource> relatedPersonResources;

        if (theDate == null) {
            relatedPersonResources = relatedPersonResourceRepository.findAll();
        } else {
            relatedPersonResources = relatedPersonResourceRepository.findByModifiedOnGreaterThanOrCreatedOnGreaterThan(theDate.getValue(), theDate.getValue());
        }

        for (RelatedPersonResource personResource : relatedPersonResources) {
            RelatedPerson person = parser.parseResource(RelatedPerson.class, personResource.getText());
            relatedPeople.add(person);
        }
        return relatedPeople;
    }
}
