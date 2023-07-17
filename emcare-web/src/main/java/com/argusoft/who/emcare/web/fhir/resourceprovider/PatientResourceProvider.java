package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.model.EmcareResource;
import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PatientResourceProvider implements IResourceProvider {

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);
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
     *              type IdType and must be annotated with the "@Read.IdParam" annotation.
     * @return Returns a resource matching this identifier, or null if none
     * exists.
     */
    @Read()
    public Patient getResourceById(@IdParam IdType theId) {

        EmcareResource emcareResource = emcareResourceService.findByResourceId(theId.getIdPart());
        Patient patient = null;
        if (emcareResource != null) {
            patient = parser.parseResource(Patient.class, emcareResource.getText());
        }
        return patient;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search
     * operation. You may have many different methods annotated with this
     * annotation, to support many different search criteria. This example
     * searches by family name.
     *
     * @param theFamilyName This operation takes one parameter which is the
     *                      search criteria. It is annotated with the "@Required" annotation. This
     *                      annotation takes one argument, a string containing the name of the search
     *                      criteria. The datatype here is StringParam, but there are other possible
     *                      parameter types depending on the specific search criteria.
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
        String patientId = UUID.randomUUID().toString();
        thePatient.setId(patientId);

        String patientString = parser.encodeResourceToString(thePatient);

        Extension facilityExtension = thePatient.getExtensionByUrl(CommonConstant.LOCATION_EXTENSION_URL);
        String facilityId = ((Identifier) facilityExtension.getValue()).getValue();
        EmcareResource emcareResource = new EmcareResource();
        emcareResource.setText(patientString);
        emcareResource.setResourceId(patientId);
        emcareResource.setType(CommonConstant.FHIR_PATIENT);
        emcareResource.setFacilityId(facilityId);

        emcareResourceService.saveResource(emcareResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType("Patient", thePatient.getId(), "1"));
        retVal.setResource(thePatient);

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

        if (thePatient.getMeta() != null && thePatient.getMeta().getVersionId() != null) {
            versionId = Integer.parseInt(thePatient.getMeta().getVersionId()) + 1;
            m.setVersionId(String.valueOf(versionId));
        }
        thePatient.setMeta(m);

        String patientString = parser.encodeResourceToString(thePatient);
        String patientId = thePatient.getIdElement().getIdPart();

        EmcareResource emcareResource = emcareResourceService.findByResourceId(patientId);

        if (emcareResource == null) {
            emcareResource = new EmcareResource();
        }
        Extension facilityExtension = thePatient.getExtensionByUrl(CommonConstant.LOCATION_EXTENSION_URL);
        String facilityId = ((Identifier) facilityExtension.getValue()).getValue();
        emcareResource.setText(patientString);
        emcareResource.setResourceId(patientId);
        emcareResource.setType("PATIENT");
        emcareResource.setFacilityId(facilityId);

        emcareResourceService.saveResource(emcareResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType("Patient", patientId, String.valueOf(versionId)));
        retVal.setResource(thePatient);

        return retVal;

    }

    /*
     * For the search (GET) method
     * Reference for sort: https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#sorting-sort
     * Reference for param: https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html#combining-multiple-parameters
     */
    @Search(queryName="bundle")
    public List<Patient> getAllPatients(
            @OptionalParam(name = CommonConstant.RESOURCE_LAST_UPDATED_AT) DateParam theDate,
            @OptionalParam(name = IAnyResource.SP_RES_ID) IdType theId) {
        List<Patient> patientsList = new ArrayList<>();
        List<EmcareResource> resourcesList = emcareResourceService.retrieveResourcesByType("PATIENT", theDate, theId);
        for (EmcareResource emcareResource : resourcesList) {
            Patient patient = parser.parseResource(Patient.class, emcareResource.getText());
            patientsList.add(patient);
        }

        return patientsList;
    }

    @Delete()
    public void deletePatient(@IdParam IdType theId) {

        EmcareResource emcareResource = emcareResourceService.findByResourceId(theId.getIdPart());

        if (emcareResource == null) {
            throw new ResourceNotFoundException("Unknown version");
        } else {
            emcareResourceService.remove(emcareResource);
        }

    }

    /*
     * Related Person APIs
     */
    @Update
    public MethodOutcome updateRelatedPerson(@IdParam IdType theId, @ResourceParam RelatedPerson theRelatedPerson) {

        //Adding meta to the related person resource
        Meta m = new Meta();
        m.setVersionId("1");
        m.setLastUpdated(new Date());

        Integer versionId = 1;

        if (theRelatedPerson.getMeta() != null && theRelatedPerson.getMeta().getVersionId() != null) {
            versionId = Integer.parseInt(theRelatedPerson.getMeta().getVersionId()) + 1;
            m.setVersionId(String.valueOf(versionId));
        }
        theRelatedPerson.setMeta(m);

        String relatedPersonString = parser.encodeResourceToString(theRelatedPerson);
        String relatedPersonId = theRelatedPerson.getIdElement().getIdPart();

        EmcareResource emcareResource = emcareResourceService.findByResourceId(relatedPersonId);

        if (emcareResource == null) {
            emcareResource = new EmcareResource();
        }

        emcareResource.setText(relatedPersonString);
        emcareResource.setResourceId(relatedPersonId);
        emcareResource.setType("RELATED_PERSON");

        emcareResourceService.saveResource(emcareResource);

        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType("RelatedPerson", relatedPersonId, String.valueOf(versionId)));
        retVal.setResource(theRelatedPerson);

        return retVal;

    }

    @Search()
    public Bundle getPatientBundle(@RequiredParam(name = CommonConstant.RESOURCE_ID) IdType theId) {
        return emcareResourceService.getPatientBundle(theId.getIdPart());
    }

    @Search(queryName="summary")
    public Bundle getPatientCountBasedOnDate(
            @RequiredParam(name = CommonConstant.SUMMARY) String type,
            @OptionalParam(name = CommonConstant.RESOURCE_LAST_UPDATED_AT) DateParam theDate,
            @OptionalParam(name = CommonConstant.RESOURCE_FACILITY_ID) String theId) {
        return emcareResourceService.getPatientCountBasedOnDate(type, theDate, theId);
    }

    @Search(allowUnknownParams = true)
    public Bundle getPatientDataForGoogleFhirDataPipes(
            @RequiredParam(name = CommonConstant.SUMMARY) StringAndListParam type,
            @OptionalParam(name = "_count") StringAndListParam count,
            @OptionalParam(name = "_total") String total) {
        String x = type.getValuesAsQueryTokens().get(0).getValuesAsQueryTokens().get(0).getValue();
        String _count = "10";
        if(count != null) {
            _count = count.getValuesAsQueryTokens().get(0).getValuesAsQueryTokens().get(0).getValue();
        }
        return emcareResourceService.getPatientDataForGoogleFhirDataPipes(
                x,
                Integer.parseInt(_count),
                total
        );
    }
}