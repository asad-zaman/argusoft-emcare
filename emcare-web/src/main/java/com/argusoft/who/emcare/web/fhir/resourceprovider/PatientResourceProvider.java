package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import com.google.gson.Gson;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

        //Adding meta to the patient resource
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());
        thePatient.setMeta(m);

        //Adding id to the patient
        thePatient.setId(UUID.randomUUID().toString());

        FhirContext fhirCtx = FhirContext.forR4();
        IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);
        String patientString = parser.encodeResourceToString(thePatient);

        EmcareResource emcareResource = new EmcareResource();
        emcareResource.setText(patientString);
        emcareResource.setType("PATIENT");

        emcareResourceService.saveResource(emcareResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType("Patient", thePatient.getId(), "1"));
        retVal.setResource(new Patient());

        return retVal;
    }

    /*
     * Update Method & for creating resources with id provided.
     * Reference for update: https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html#instance_update
     */
    @Update
    public MethodOutcome update(@IdParam IdType theId, @ResourceParam Patient thePatient) {

        //Adding meta to the patient resource
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());

        Integer versionId = 1;

        if (thePatient.getMeta() == null || thePatient.getMeta().getVersionId() == null) {
            thePatient.setMeta(m);
        } else {
            versionId = Integer.parseInt(thePatient.getMeta().getVersionId()) + 1;
            m.setVersionId(String.valueOf(versionId));
        }

        FhirContext fhirCtx = FhirContext.forR4();
        IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);
        String patientString = parser.encodeResourceToString(thePatient);

        String id = thePatient.getId();

        EmcareResource emcareResource = new EmcareResource();
        emcareResource.setText(patientString);
        emcareResource.setType("PATIENT");

        emcareResourceService.saveResource(emcareResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType("Patient", id, String.valueOf(versionId)));
        retVal.setResource(new Patient());

        return retVal;

    }

    /*
     * For the search (GET) method
     * Reference for sort: https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#sorting-sort
     * Reference for param: https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#combining-multiple-parameters
     */
    @Search()
    public List<Patient> getAllPatients() {
        List<Patient> patientsList = new ArrayList<>();

        List<EmcareResource> resourcesList = emcareResourceService.retrieveResourcesByType("PATIENT");
        for (EmcareResource emcareResource : resourcesList) {
            FhirContext fhirCtx = FhirContext.forR4();
            IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);
            Patient patient = parser.parseResource(Patient.class, emcareResource.getText());
            patientsList.add(patient);
        }

        return patientsList;
    }
}
