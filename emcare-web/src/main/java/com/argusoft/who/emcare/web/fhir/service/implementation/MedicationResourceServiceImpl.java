package com.argusoft.who.emcare.web.fhir.service.implementation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.MedicationResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.MedicationDto;
import com.argusoft.who.emcare.web.fhir.mapper.EmcareResourceMapper;
import com.argusoft.who.emcare.web.fhir.model.MedicationResource;
import com.argusoft.who.emcare.web.fhir.service.MedicationResourceService;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Medication;
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
public class MedicationResourceServiceImpl implements MedicationResourceService {


    @Autowired
    MedicationResourceRepository medicationResourceRepository;

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(true);

    @Override
    public Medication saveResource(Medication medication) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        medication.setMeta(m);

        String medicationId = null;
        if (medication.getId() != null) {
            medicationId = medication.getIdElement().getIdPart();
        } else {
            medicationId = UUID.randomUUID().toString();
        }
        medication.setId(medicationId);

        String medicationString = parser.encodeResourceToString(medication);

        MedicationResource medicationResource = new MedicationResource();
        medicationResource.setText(medicationString);
        medicationResource.setResourceId(medicationId);

        medicationResourceRepository.save(medicationResource);

        return medication;
    }

    @Override
    public Medication getResourceById(String id) {
        MedicationResource medicationResource = medicationResourceRepository.findByResourceId(id);
        Medication medication = null;
        if (medicationResource != null) {
            medication = parser.parseResource(Medication.class, medicationResource.getText());
        }
        return medication;
    }

    @Override
    public MethodOutcome updateMedicationResource(IdType idType, Medication medication) {
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        medication.setMeta(m);


        String encodeResource = parser.encodeResourceToString(medication);
        MedicationResource medicationResource = medicationResourceRepository.findByResourceId(idType.getIdPart());
        medicationResource.setText(encodeResource);

        medicationResourceRepository.save(medicationResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.MEDICATION, medication.getId(), "1"));
        retVal.setResource(medication);
        return retVal;
    }

    @Override
    public List<Medication> getAllMedication(DateParam theDate) {
        List<Medication> medications = new ArrayList<>();

        List<MedicationResource> medicationResources;

        if (theDate == null) {
            medicationResources =  medicationResourceRepository.findAll();
        } else {
            medicationResources = medicationResourceRepository.findByModifiedOnGreaterThanOrCreatedOnGreaterThan(theDate.getValue(), theDate.getValue());
        }

        for (MedicationResource medicationResource : medicationResources) {
            Medication structureMap = parser.parseResource(Medication.class, medicationResource.getText());
            medications.add(structureMap);
        }
        return medications;
    }

    @Override
    public PageDto getMedicationPage(Integer pageNo, String searchString) {
        List<MedicationDto> medicationDtos = new ArrayList<>();
        Page<MedicationResource> medicationResources = null;
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE);
        Long count;

        if (searchString != null && !searchString.isEmpty()) {
            medicationResources = medicationResourceRepository.findByTextContainingIgnoreCase(searchString, page);
            count = Long.valueOf(medicationResourceRepository.findByTextContainingIgnoreCase(searchString).size());
        } else {
            medicationResources = medicationResourceRepository.findAll(page);
            count = Long.valueOf(medicationResourceRepository.count());
        }


        for (MedicationResource medicationResource : medicationResources) {
            Medication medication = parser.parseResource(Medication.class, medicationResource.getText());
            medicationDtos.add(EmcareResourceMapper.getMedicationDto(medication));
        }
        PageDto pageDto = new PageDto();
        pageDto.setList(medicationDtos);
        pageDto.setTotalCount(count);

        return pageDto;
    }
}
