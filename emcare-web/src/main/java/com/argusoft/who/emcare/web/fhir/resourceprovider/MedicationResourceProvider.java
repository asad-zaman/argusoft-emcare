package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.MedicationResourceService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Medication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MedicationResourceProvider implements IResourceProvider {

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Medication.class;
    }

    @Autowired
    MedicationResourceService medicationResourceService;

    @Create
    public MethodOutcome createStructureMap(@ResourceParam Medication medication) {
        medicationResourceService.saveResource(medication);
        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.LOCATION_TYPE_STRING, medication.getId(), "1"));
        retVal.setResource(medication);
        return retVal;
    }

    @Read()
    public Medication getResourceById(@IdParam IdType theId) {
        return medicationResourceService.getResourceById(theId.getIdPart());
    }

    @Update
    public MethodOutcome updateStructureMapResource(@IdParam IdType theId, @ResourceParam Medication medication) {
        return medicationResourceService.updateMedicationResource(theId, medication);
    }

    @Search()
    public List<Medication> getAllStructureMap() {
        return medicationResourceService.getAllMedication();
    }
}
