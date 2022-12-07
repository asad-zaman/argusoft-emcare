package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.dao.EncounterResourceRepository;
import com.argusoft.who.emcare.web.fhir.model.EncounterResource;
import com.argusoft.who.emcare.web.fhir.service.EncounterResourceService;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class EncounterResourceServiceImpl implements EncounterResourceService {

    @Autowired
    EncounterResourceRepository encounterResourceRepository;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(true);

    @Override
    public Encounter saveResource(Encounter encounter) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        encounter.setMeta(m);

        String encounterId = null;
        if (encounter.getId() != null) {
            encounterId = encounter.getIdElement().getIdPart();
        } else {
            encounterId = UUID.randomUUID().toString();
        }
        encounter.setId(encounterId);

        String locationString = parser.encodeResourceToString(encounter);

        EncounterResource encounterResource = new EncounterResource();
        encounterResource.setText(locationString);
        encounterResource.setPatientId(encounter.getSubject().getReference().replace("Patient/",""));
        encounterResource.setResourceId(encounterId);

        encounterResourceRepository.save(encounterResource);

        return encounter;
    }

    @Override
    public Encounter getResourceById(String id) {
        EncounterResource encounterResource = encounterResourceRepository.findByResourceId(id);
        Encounter encounter = null;
        if (encounterResource != null) {
            encounter = parser.parseResource(Encounter.class, encounterResource.getText());
        }
        return encounter;
    }

    @Override
    public MethodOutcome updateEncounterResource(IdType idType, Encounter encounter) {
        Integer version = 1;
        version = Integer.parseInt(encounter.getMeta().getVersionId());
        if (version > 0) {
            version++;
        }
        Meta m = new Meta();
        m.setVersionId(version.toString());
        m.setLastUpdated(new Date());
        encounter.setMeta(m);


        String encodeResource = parser.encodeResourceToString(encounter);
        EncounterResource encounterResource = encounterResourceRepository.findByResourceId(idType.getIdPart());
        encounterResource.setText(encodeResource);
        encounterResource.setPatientId(encounter.getSubject().getTypeElement().getId());

        encounterResourceRepository.save(encounterResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.ENCOUNTER, encounter.getId(), version.toString()));
        retVal.setResource(encounter);
        return retVal;
    }

    @Override
    public List<Encounter> getAllEncounter(DateParam theDate, String searchText) {
        List<Encounter> encounters = new ArrayList<>();
        List<EncounterResource> encounterResources = new ArrayList<>();

        if (theDate == null && searchText == null) {
            encounterResources = encounterResourceRepository.findAll();
        } else if (theDate != null && searchText != null) {
            encounterResources = encounterResourceRepository.fetchByDateAndText(searchText, theDate.getValue());
        } else if (theDate == null && searchText != null) {
            encounterResources = encounterResourceRepository.findByTextContainingIgnoreCase(searchText);
        } else if (theDate != null && searchText == null) {
            encounterResources = encounterResourceRepository.findByModifiedOnGreaterThanOrCreatedOnGreaterThan(theDate.getValue(), theDate.getValue());
        }

        for (EncounterResource encounterResource : encounterResources) {
            Encounter encounter = parser.parseResource(Encounter.class, encounterResource.getText());
            encounters.add(encounter);
        }
        return encounters;
    }
}
