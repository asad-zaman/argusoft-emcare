package com.argusoft.who.emcare.web.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Medication;

import java.util.List;

public interface MedicationResourceService {


    public Medication saveResource(Medication medication);

    public Medication getResourceById(String id);

    public MethodOutcome updateMedicationResource(IdType idType, Medication medication);

    public List<Medication> getAllMedication();

    public PageDto getMedicationPage(Integer pageNo, String searchString);
}
