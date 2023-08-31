package com.argusoft.who.emcare.web.fhir.mapper;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dto.*;
import com.argusoft.who.emcare.web.fhir.model.LocationResource;
import org.hl7.fhir.r4.model.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when;

public class EmcareResourceMapperTest {
    @InjectMocks
    EmcareResourceMapper emcareResourceMapper;

    String patient1 = "Patient1";
    String patient2 = "Patient2";
    @Test
    void testPatientEntityToDtoMapper() throws IOException {
        Patient  patientData1 = getPatientData(patient1);

        PatientDto result = EmcareResourceMapper.patientEntityToDtoMapper(patientData1);

        assertNotNull(result);
        assertEquals("example", result.getId());
        assertEquals("12345", result.getIdentifier());
        assertEquals("John", result.getGivenName());
        assertEquals("Doe", result.getFamilyName());
        assertEquals("Male", result.getGender());
        assertEquals("123 Main St", result.getAddressLine());
        assertEquals("Anytown", result.getAddressCity());
        assertEquals("USA", result.getAddressCountry());
        assertEquals("12345", result.getAddressPostalCode());

    }
    @Test
    void testPatientEntitiesToDtoMapper() throws IOException {
        List<Patient> patientList = new ArrayList<>();
        Patient  patientData1 = getPatientData(patient1);
        Patient  patientData2 = getPatientData(patient2);
        patientList.add(patientData1);
        patientList.add(patientData2);


        List<PatientDto> result = EmcareResourceMapper.patientEntitiesToDtoMapper(patientList);

        assertNotNull(result);
        //testing for patientData1
        assertEquals("example", result.get(0).getId());
        assertEquals("12345", result.get(0).getIdentifier());
        assertEquals("John", result.get(0).getGivenName());
        assertEquals("Doe", result.get(0).getFamilyName());
        assertEquals("Male", result.get(0).getGender());
        assertEquals("123 Main St", result.get(0).getAddressLine());
        assertEquals("Anytown", result.get(0).getAddressCity());
        assertEquals("USA", result.get(0).getAddressCountry());
        assertEquals("12345", result.get(0).getAddressPostalCode());
        //testing for patientData2
        assertEquals("example2", result.get(1).getId());
        assertEquals("67890", result.get(1).getIdentifier());
        assertEquals("Jane", result.get(1).getGivenName());
        assertEquals("Smith", result.get(1).getFamilyName());
        assertEquals("Female", result.get(1).getGender());
        assertEquals("456 Oak Ave", result.get(1).getAddressLine());
        assertEquals("Smallville", result.get(1).getAddressCity());
        assertEquals("USA", result.get(1).getAddressCountry());
        assertEquals("98765", result.get(1).getAddressPostalCode());

    }

    @Test
    void testQuestionnaireEntityToDtoMapper(){
        Questionnaire questionnaireData = getQuestionnaire("1","Sample","Sample Title","Description123");

        QuestionnaireDto result = EmcareResourceMapper.questionnaireEntityToDtoMapper(questionnaireData);

        assertNotNull(result);
        assertEquals("1",result.getId());
        assertEquals("Sample",result.getName());
        assertEquals("Sample Title",result.getTitle());
        assertEquals("Description123",result.getDescription());
    }
    @Test
    void testQuestionnaireEntitiesToDtoMapper(){

        Questionnaire questionnaireData1 = getQuestionnaire("1","Sample","Sample Title","Description123");
        Questionnaire questionnaireData2 = getQuestionnaire("2","Test","TestTitle","TestDesc");

        List<Questionnaire> questionnaireList = new ArrayList<>();

        questionnaireList.add(questionnaireData1);
        questionnaireList.add(questionnaireData2);

        List<QuestionnaireDto> result = EmcareResourceMapper.questionnaireEntitiesToDtoMapper(questionnaireList);

        assertNotNull(result);
        //test for questionnaire
        assertEquals("1",result.get(0).getId());
        assertEquals("Sample",result.get(0).getName());
        assertEquals("Sample Title",result.get(0).getTitle());
        assertEquals("Description123",result.get(0).getDescription());

        //test for questionnaire2
        assertEquals("2",result.get(1).getId());
        assertEquals("Test",result.get(1).getName());
        assertEquals("TestTitle",result.get(1).getTitle());
        assertEquals("TestDesc",result.get(1).getDescription());
    }

    @Test
    void testGetStructureMapDto(){
        StructureMap structureMapData = new StructureMap();
        structureMapData.setId("1");
        structureMapData.setName("Sample");
        structureMapData.setTitle("SampleTitle");
        structureMapData.setDescription("SampleDescription");
        structureMapData.setPublisher("SamplePublisher");

        StructureMapDto result = EmcareResourceMapper.getStructureMapDto(structureMapData);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Sample", result.getName());
        assertEquals("SampleTitle", result.getTitle());
        assertEquals("SampleDescription", result.getDescription());
        assertEquals("SamplePublisher", result.getPublisher());
    }

    @Test
    void testGetStructureDefinitionDto(){
        StructureDefinition structureDefinitionData = new StructureDefinition();
        structureDefinitionData.setId("1");
        structureDefinitionData.setName("Sample");
        structureDefinitionData.setTitle("SampleTitle");
        structureDefinitionData.setDescription("SampleDescription");
        structureDefinitionData.setPublisher("SamplePublisher");

        StructureDefinitionDto result = EmcareResourceMapper.getStructureDefinitionDto(structureDefinitionData);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Sample", result.getName());
        assertEquals("SampleTitle", result.getTitle());
        assertEquals("SampleDescription", result.getDescription());
        assertEquals("SamplePublisher", result.getPublisher());
    }

    @Test
    void testGetCodeSystemDto(){
        CodeSystem codeSystemData = new CodeSystem();
        codeSystemData.setId("1");
        codeSystemData.setName("Sample");
        codeSystemData.setTitle("SampleTitle");
        codeSystemData.setDescription("SampleDescription");
        codeSystemData.setPublisher("SamplePublisher");

        CodeSystemDto result = EmcareResourceMapper.getCodeSystemDto(codeSystemData);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Sample", result.getName());
        assertEquals("SampleTitle", result.getTitle());
        assertEquals("SampleDescription", result.getDescription());
        assertEquals("SamplePublisher", result.getPublisher());
    }

    @Test
    void testGetStructureMapDto1(){
        ActivityDefinition activityDefinitionData = new ActivityDefinition();
        activityDefinitionData.setId("example");
        activityDefinitionData.setName("SampleActivityDefinition");
        activityDefinitionData.setTitle("Sample Title");
        activityDefinitionData.setStatus(Enumerations.PublicationStatus.ACTIVE);
        activityDefinitionData.setSubtitle("Sample Subtitle");

        // Map ActivityDefinition object to DTO
        ActivityDefinitionDto activityDefinitionDto = EmcareResourceMapper.getStructureMapDto(activityDefinitionData);

        // Assertions
        assertEquals("example", activityDefinitionDto.getId());
        assertEquals("SampleActivityDefinition", activityDefinitionDto.getName());
        assertEquals("Sample Title", activityDefinitionDto.getTitle());
        assertEquals("Active", activityDefinitionDto.getStatus());
        assertEquals("Sample Subtitle", activityDefinitionDto.getSubTitle());
    }

    @Test
    void testGetFacilityDto(){
        Location location = new Location();
        location.setName("Sample123");
        location.setId("123");

        Address address = new Address();
        address.addLine("ABC Street");
        location.setAddress(address);

        Reference managingOrganization = new Reference();
        managingOrganization.setId("org1");
        managingOrganization.setDisplay("Sample Organization");
        location.setManagingOrganization(managingOrganization);

        String id = "facility1";

        // Map Location object to FacilityDto
        FacilityDto facilityDto = EmcareResourceMapper.getFacilityDto(location, id);

        // Assertions
        assertEquals("Sample123", facilityDto.getFacilityName());
        assertEquals("facility1", facilityDto.getFacilityId());
        assertEquals("ABC Street", facilityDto.getAddress());
        assertEquals("org1", facilityDto.getOrganizationId());
        assertEquals("Sample Organization", facilityDto.getOrganizationName());
    }

    @Test
    void testGetOrganizationDto(){
        Organization organization = new Organization();
        organization.setId("org1");
        organization.setName("Sample Organization");
        organization.setActive(true);

        OrganizationDto organizationDto = EmcareResourceMapper.getOrganizationDto(organization);

        assertEquals("org1", organizationDto.getId());
        assertEquals("Sample Organization", organizationDto.getName());
        assertEquals(true, organizationDto.getActive());
    }

    @Test
    void testGetFacilityDtoForList() {
        Location location = new Location();
        location.setId("example");
        location.setName("Sample Facility");
        location.setStatus(Location.LocationStatus.ACTIVE);

        Address address = new Address();
        address.addLine("123 Facility Street");
        location.setAddress(address);

        Reference managingOrganization = new Reference();
        managingOrganization.setId("org1");
        managingOrganization.setDisplay("Sample Organization");
        location.setManagingOrganization(managingOrganization);

        LocationResource locationResource = new LocationResource();
        locationResource.setLocationName("LocationResource Name");
        locationResource.setLocationId(1L);

        FacilityDto facilityDto = EmcareResourceMapper.getFacilityDtoForList(location, locationResource);

        assertEquals("Sample Facility", facilityDto.getFacilityName());
        assertEquals("example", facilityDto.getFacilityId());
        assertEquals("123 Facility Street", facilityDto.getAddress());
        assertEquals("org1", facilityDto.getOrganizationId());
        assertEquals("Sample Organization", facilityDto.getOrganizationName());
        assertEquals("LocationResource Name", facilityDto.getLocationName());
        assertEquals(1L, facilityDto.getLocationId());
        assertEquals("Active", facilityDto.getStatus());
    }

    @Test
    void testGetLibraryDto() {
        Library library = new Library();
        library.setId("library1");
        library.setName("Sample Library");
        library.setDescription("Sample Description");
        library.setTitle("Sample Title");
        library.setPublisher("Sample Publisher");
        library.setStatus(Enumerations.PublicationStatus.ACTIVE); // Set the status to ACTIVE

        LibraryDto libraryDto = EmcareResourceMapper.getLibraryDto(library);

        assertEquals("library1", libraryDto.getId());
        assertEquals("Sample Library", libraryDto.getName());
        assertEquals("Sample Description", libraryDto.getDescription());
        assertEquals("Sample Title", libraryDto.getTitle());
        assertEquals("Sample Publisher", libraryDto.getPublisher());
        assertEquals("Active", libraryDto.getStatus()); // Check the status
    }

    private static Questionnaire getQuestionnaire(String id,String name,String title,String description) {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setId(id);
        questionnaire.setName(name);
        questionnaire.setTitle(title);
        questionnaire.setDescription(description);

        return questionnaire;
    }


    private Patient getPatientData(String patientPath) throws IOException {
        File file = new File("src/test/resources/mockdata/patient/"+patientPath+".json");
        InputStream fileInputStream = new FileInputStream(file);
        String jsonString = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8);
        return convertJsonToPatient(jsonString);
    }
    public static Patient convertJsonToPatient(String jsonString) {
        FhirContext fhirContext = FhirContext.forR4();
        IParser parser = fhirContext.newJsonParser();
        Patient patient = parser.parseResource(Patient.class, jsonString);
        return patient;
    }
}
