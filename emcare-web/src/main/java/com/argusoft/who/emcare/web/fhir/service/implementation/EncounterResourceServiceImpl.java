package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.dao.EncounterResourceRepository;
import com.argusoft.who.emcare.web.fhir.model.EncounterResource;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import com.argusoft.who.emcare.web.fhir.service.EncounterResourceService;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EncounterResourceServiceImpl implements EncounterResourceService {

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(true);
    @Autowired
    EncounterResourceRepository encounterResourceRepository;

    @Autowired
    EmcareResourceService emcareResourceService;

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
        encounterResource.setPatientId(encounter.getSubject().getReference().replace("Patient/", ""));
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
    public List<Encounter> getAllEncounter(DateParam theDate, String searchText, String theId) {
        List<Encounter> encounters = new ArrayList<>();
        List<EncounterResource> encounterResources;

        List<String> patientIds = emcareResourceService.getPatientIdsUnderFacility(theId);

        if (theDate == null) {
            if (searchText == null) {
                encounterResources = encounterResourceRepository.findAll();
            } else {
                encounterResources = encounterResourceRepository.findByTextContainingIgnoreCase(searchText);
            }
        } else {
            if (searchText == null) {
                encounterResources = encounterResourceRepository.findByModifiedOnGreaterThanOrCreatedOnGreaterThan(theDate.getValue(), theDate.getValue());
            } else {
                encounterResources = encounterResourceRepository.fetchByDateAndText(searchText, theDate.getValue());
            }
        }

        for (EncounterResource encounterResource : encounterResources) {
            if (patientIds.contains(encounterResource.getPatientId())) {
                Encounter encounter = parser.parseResource(Encounter.class, encounterResource.getText());
                encounters.add(encounter);
            }
        }
        return encounters;
    }

    @Override
    public Bundle getEncounterCountBasedOnDate(String summaryType, DateParam theDate, String theId) {
        List<String> patientId = new ArrayList<>();
        if (theId != null) {
            patientId = emcareResourceService.getPatientIdsUnderFacility(theId);
        }
        if(patientId.isEmpty()){
            Bundle bundle = new Bundle();
            bundle.setTotal(0);
            return bundle;
        }
        Long count = 0l;
        if (summaryType.equalsIgnoreCase(CommonConstant.SUMMARY_TYPE_COUNT)) {
            if (Objects.isNull(theDate)) {
                if (Objects.isNull(theId)) {
                    count = encounterResourceRepository.count();
                } else {
                    count = encounterResourceRepository.getCountWithFacilityId(patientId);
                }

            } else {
                if (Objects.isNull(theId)) {
                    count = encounterResourceRepository.getCountBasedOnDate(theDate.getValue());
                } else {
                    count = encounterResourceRepository.getCountBasedOnDateWithFacilityId(theDate.getValue(), patientId);
                }

            }
        } else {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.setTotal(count.intValue());
        return bundle;
    }

    @Override
    public Bundle getEncounterDataForGoogleFhirDataPipes(String summaryType, Integer count, String total) {
        Bundle bundle = new Bundle();
        switch(summaryType) {
            case "count":
                bundle.setTotal((int) encounterResourceRepository.count());
                return bundle;
            case "data":
                List<EncounterResource> encounterResources = encounterResourceRepository.findAll();
                int x = 0;
                for (EncounterResource encounterResource : encounterResources) {
                    if(x >= count) break;
                    x++;
                    Encounter encounter = parser.parseResource(Encounter.class, encounterResource.getText());
                    bundle.addEntry(
                            new Bundle.BundleEntryComponent()
                                    .setResource(encounter)
                                    .setFullUrl("http://localhost:8080/fhir/" + encounter.getId().substring(0, 44))
                    );
                }
                bundle.setTotal(Math.min(count, encounterResources.size()));
                return bundle;
        }
        return null;
    }
}
