package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.dao.ObservationResourceRepository;
import com.argusoft.who.emcare.web.fhir.model.ObservationResource;
import com.argusoft.who.emcare.web.fhir.service.ObservationResourceService;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ObservationResourceServiceImpl implements ObservationResourceService {

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(true);

    @Autowired
    ObservationResourceRepository observationResourceRepository;

    @Override
    public Observation saveResource(Observation observation) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        observation.setMeta(m);
        String observationId = null;
        if (observation.getId() != null) {
            observationId = observation.getIdElement().getIdPart();
        } else {
            observationId = UUID.randomUUID().toString();
        }
        observation.setId(observationId);

        String locationString = parser.encodeResourceToString(observation);

        ObservationResource observationResource = new ObservationResource();
        observationResource.setText(locationString);
        observationResource.setSubjectId(observation.getSubject().getReference().split("/")[1]);
        observationResource.setSubjectType(observation.getSubject().getReference().split("/")[0]);
        observationResource.setResourceId(observationId);

        observationResourceRepository.save(observationResource);

        return observation;
    }

    @Override
    public Observation getResourceById(String id) {
        ObservationResource observationResource = observationResourceRepository.findByResourceId(id);
        Observation observation = null;
        if (observationResource != null) {
            observation = parser.parseResource(Observation.class, observationResource.getText());
        }
        return observation;
    }

    @Override
    public MethodOutcome updateObservationResource(IdType idType, Observation observation) {
        Integer version = 1;
        version = Integer.parseInt(observation.getMeta().getVersionId());
        if (version > 0) {
            version++;
        }
        Meta m = new Meta();
        m.setVersionId(version.toString());
        m.setLastUpdated(new Date());
        observation.setMeta(m);


        String encodeResource = parser.encodeResourceToString(observation);
        ObservationResource observationResource = observationResourceRepository.findByResourceId(idType.getIdPart());
        observationResource.setText(encodeResource);
        observationResource.setSubjectId(observation.getSubject().getReference().split("/")[1]);
        observationResource.setSubjectType(observation.getSubject().getReference().split("/")[0]);

        observationResourceRepository.save(observationResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.OBSERVATION, observation.getId(), version.toString()));
        retVal.setResource(observation);
        return retVal;
    }

    @Override
    public List<Observation> getAllObservation(DateParam theDate, String searchText) {
        List<Observation> observations = new ArrayList<>();
        List<ObservationResource> observationResources;

        if (theDate == null) {
            if (searchText == null) {
                observationResources = observationResourceRepository.findAll();
            } else {
                observationResources = observationResourceRepository.findByTextContainingIgnoreCase(searchText);
            }
        } else {
            if (searchText != null) {
                observationResources = observationResourceRepository.fetchByDateAndText(searchText, theDate.getValue());
            } else {
                observationResources = observationResourceRepository.findByModifiedOnGreaterThanOrCreatedOnGreaterThan(theDate.getValue(), theDate.getValue());
            }
        }


        for (ObservationResource observationResource : observationResources) {
            Observation observation = parser.parseResource(Observation.class, observationResource.getText());
            observations.add(observation);
        }
        return observations;
    }
}
