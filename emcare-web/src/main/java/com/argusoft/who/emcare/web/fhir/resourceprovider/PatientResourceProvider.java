package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import com.google.gson.Gson;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Component
public class PatientResourceProvider implements IResourceProvider {
	
	@Autowired
	private EmcareResourceService emcareResourceService;
	
	
    /**
     * The getResourceType method comes from IResourceProvider, and must be
     * overridden to indicate what type of resource this provider supplies.
     */
    @Override
    public Class<Patient> getResourceType() {
        return Patient.class;
    }

    /**
     * The "@Read" annotation indicates that this method supports the read
     * operation. Read operations should return a single resource instance.
     *
     * @param theId The read operation takes one parameter, which must be of
     * type IdType and must be annotated with the "@Read.IdParam" annotation.
     * @return Returns a resource matching this identifier, or null if none
     * exists.
     */
    @Read()
    public Patient getResourceById(@IdParam IdType theId) {
        Patient patient = new Patient();
        patient.addIdentifier();
        patient.getIdentifier().get(0).setSystem("urn:hapitest:mrns");
        patient.getIdentifier().get(0).setValue("00002");
        patient.addName().setFamily("Test");
        patient.getName().get(0).addGiven("PatientOne");
        patient.setGender(Enumerations.AdministrativeGender.FEMALE);
        return patient;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search
     * operation. You may have many different methods annotated with this
     * annotation, to support many different search criteria. This example
     * searches by family name.
     *
     * @param theFamilyName This operation takes one parameter which is the
     * search criteria. It is annotated with the "@Required" annotation. This
     * annotation takes one argument, a string containing the name of the search
     * criteria. The datatype here is StringParam, but there are other possible
     * parameter types depending on the specific search criteria.
     * @return This method returns a list of Patients. This list may contain
     * multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<Patient> getPatient(@RequiredParam(name = Patient.SP_FAMILY) StringParam theFamilyName) {
    	Patient patient = new Patient();
        patient.addIdentifier();
        patient.getIdentifier().get(0).setUse(Identifier.IdentifierUse.OFFICIAL);
        patient.getIdentifier().get(0).setSystem("urn:hapitest:mrns");
        patient.getIdentifier().get(0).setValue("00001");
        patient.addName();
        patient.getName().get(0).setFamily(theFamilyName.getValue());
        patient.getName().get(0).addGiven("PatientOne");
        patient.setGender(Enumerations.AdministrativeGender.MALE);
        return Collections.singletonList(patient);
    }

    @Create
    public MethodOutcome createPatient(@ResourceParam Patient thePatient) {
    	
        Gson gson = new Gson();
        String patientString = gson.toJson(thePatient).toString();
        
        EmcareResource emcareResource = new EmcareResource();
        emcareResource.setText(patientString);
        
        emcareResourceService.saveResource(emcareResource);
        
        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType("Patient", "3746", "1"));
        retVal.setResource(new Patient());
        
        return retVal;
    }
}
