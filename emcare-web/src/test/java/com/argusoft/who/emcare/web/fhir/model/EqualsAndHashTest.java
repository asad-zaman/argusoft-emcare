package com.argusoft.who.emcare.web.fhir.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(MockitoExtension.class)
public class EqualsAndHashTest {

    @Test
    public void testActivityDefinition(){
        ActivityDefinitionResource activity1 = new ActivityDefinitionResource();
        ActivityDefinitionResource activity2 = new ActivityDefinitionResource();
        ActivityDefinitionResource activity3 = new ActivityDefinitionResource();

        activity1.setId(1L);
        activity1.setText("text1");
        activity1.setResourceId("ID1");

        activity2.setId(1L);
        activity2.setText("text1");
        activity2.setResourceId("ID1");

        activity3.setId(2L);
        activity3.setText("Another text");
        activity3.setResourceId("Another ID");

        assertEquals(activity1,activity2);
        assertEquals(activity1.hashCode(),activity2.hashCode());

        assertNotEquals(activity1,activity3);
    }

    @Test
    public void testAuditEvent(){
        AuditEventResource auditEvent1 = new AuditEventResource();
        AuditEventResource auditEvent2 = new AuditEventResource();
        AuditEventResource auditEvent3 = new AuditEventResource();

        auditEvent1.setId(1L);
        auditEvent1.setStatus("Status1");
        auditEvent1.setCnsltStage("Stage1");
        auditEvent1.setRecorded(new Date());
        auditEvent1.setText("Text1");
        auditEvent1.setEncounterId("ID1");
        auditEvent1.setPatientId("ID1");
        auditEvent1.setResourceId("ID1");

        auditEvent2.setId(1L);
        auditEvent2.setStatus("Status1");
        auditEvent2.setCnsltStage("Stage1");
        auditEvent2.setRecorded(new Date());
        auditEvent2.setText("Text1");
        auditEvent2.setEncounterId("ID1");
        auditEvent2.setPatientId("ID1");
        auditEvent2.setResourceId("ID1");

        auditEvent3.setId(2L);
        auditEvent3.setStatus("Status");
        auditEvent3.setCnsltStage("Stage");
        auditEvent3.setRecorded(new Date());
        auditEvent3.setText("Text");
        auditEvent3.setEncounterId("ID");
        auditEvent3.setPatientId("ID");
        auditEvent3.setResourceId("ID");

        assertEquals(auditEvent1,auditEvent2);
        assertEquals(auditEvent1.hashCode(),auditEvent2.hashCode());

        assertNotEquals(auditEvent1,auditEvent3);
    }

    @Test
    public void testCodeSystemResource(){
        CodeSystemResource codeSystem1 = new CodeSystemResource();
        CodeSystemResource codeSystem2 = new CodeSystemResource();
        CodeSystemResource codeSystem3 = new CodeSystemResource();

        codeSystem1.setId(1L);
        codeSystem1.setText("text1");
        codeSystem1.setResourceId("ID1");

        codeSystem2.setId(1L);
        codeSystem2.setText("text1");
        codeSystem2.setResourceId("ID1");

        codeSystem3.setId(2L);
        codeSystem3.setText("Another text");
        codeSystem3.setResourceId("Another ID");

        assertEquals(codeSystem1,codeSystem2);
        assertEquals(codeSystem1.hashCode(),codeSystem2.hashCode());

        assertNotEquals(codeSystem1,codeSystem3);
    }

    @Test
    public void testConditionResource(){
        ConditionResource condition1 = new ConditionResource();
        ConditionResource condition2 = new ConditionResource();
        ConditionResource condition3 = new ConditionResource();

        condition1.setId(1L);
        condition1.setText("text1");
        condition1.setResourceId("ID1");
        condition1.setEncounterId("ID1");
        condition1.setPatientId("ID1");

        condition2.setId(1L);
        condition2.setText("text1");
        condition2.setResourceId("ID1");
        condition2.setEncounterId("ID1");
        condition2.setPatientId("ID1");

        condition3.setId(2L);
        condition3.setText("text2");
        condition3.setResourceId("ID2");
        condition3.setEncounterId("ID2");
        condition3.setPatientId("ID2");

        assertEquals(condition1,condition2);
        assertEquals(condition1.hashCode(),condition2.hashCode());

        assertNotEquals(condition1,condition3);
    }

    @Test
    public void testEmacareResource(){
        EmcareResource emcareResource1 = new EmcareResource();
        EmcareResource emcareResource2 = new EmcareResource();
        EmcareResource emcareResource3 = new EmcareResource();

        emcareResource1.setId(1);
        emcareResource1.setText("text1");
        emcareResource1.setResourceId("ID1");
        emcareResource1.setType("type1");
        emcareResource1.setFacilityId("ID1");

        emcareResource2.setId(1);
        emcareResource2.setText("text1");
        emcareResource2.setResourceId("ID1");
        emcareResource2.setType("type1");
        emcareResource2.setFacilityId("ID1");

        emcareResource3.setId(2);
        emcareResource3.setText("text2");
        emcareResource3.setResourceId("ID2");
        emcareResource3.setType("type2");
        emcareResource3.setFacilityId("ID1");

        assertEquals(emcareResource1,emcareResource2);
        assertEquals(emcareResource1.hashCode(),emcareResource2.hashCode());

        assertNotEquals(emcareResource1,emcareResource3);
    }

    @Test
    public void testEncounterResource(){
        EncounterResource encounter1 = new EncounterResource();
        EncounterResource encounter2 = new EncounterResource();
        EncounterResource encounter3 = new EncounterResource();

        encounter1.setId(1L);
        encounter1.setText("text1");
        encounter1.setResourceId("ID1");
        encounter1.setPatientId("ID1");

        encounter2.setId(1L);
        encounter2.setText("text1");
        encounter2.setResourceId("ID1");
        encounter2.setPatientId("ID1");

        encounter3.setId(2L);
        encounter3.setText("text2");
        encounter3.setResourceId("ID2");
        encounter3.setPatientId("ID2");

        assertEquals(encounter1,encounter2);
        assertEquals(encounter1.hashCode(),encounter2.hashCode());

        assertNotEquals(encounter1,encounter3);
    }

    @Test
    public void testLibraryResource(){
        LibraryResource library1 = new LibraryResource();
        LibraryResource library2 = new LibraryResource();
        LibraryResource library3 = new LibraryResource();

        library1.setId(1L);
        library1.setText("text1");
        library1.setResourceId("ID1");

        library2.setId(1L);
        library2.setText("text1");
        library2.setResourceId("ID1");

        library3.setId(2L);
        library3.setText("Another text");
        library3.setResourceId("Another ID");

        assertEquals(library1,library2);
        assertEquals(library1.hashCode(),library2.hashCode());

        assertNotEquals(library1,library3);
    }

    @Test
    public void testLocationResource(){
        LocationResource location1 = new LocationResource();
        LocationResource location2 = new LocationResource();
        LocationResource location3 = new LocationResource();

        location1.setLocationId(1L);
        location1.setLocationName("Name1");
        location1.setType("Type1");
        location1.setResourceId("ID1");
        location1.setText("text1");
        location1.setId(1L);
        location1.setOrganizationName("Name1");
        location1.setOrgId("ID1");

        location2.setLocationId(1L);
        location2.setLocationName("Name1");
        location2.setType("Type1");
        location2.setResourceId("ID1");
        location2.setText("text1");
        location2.setId(1L);
        location2.setOrganizationName("Name1");
        location2.setOrgId("ID1");

        location3.setLocationId(3L);
        location3.setLocationName("Name3");
        location3.setType("Type3");
        location3.setResourceId("ID3");
        location3.setText("text2");
        location3.setId(3L);
        location3.setOrganizationName("Name2");
        location3.setOrgId("ID2");

        assertEquals(location1,location2);
        assertEquals(location1.hashCode(),location2.hashCode());

        assertNotEquals(location1,location3);
    }

    @Test
    public void testMedicationResource(){
        MedicationResource medication1 = new MedicationResource();
        MedicationResource medication2 = new MedicationResource();
        MedicationResource medication3 = new MedicationResource();

        medication1.setId(1L);
        medication1.setText("text1");
        medication1.setResourceId("ID1");

        medication2.setId(1L);
        medication2.setText("text1");
        medication2.setResourceId("ID1");

        medication3.setId(2L);
        medication3.setText("Another text");
        medication3.setResourceId("Another ID");

        assertEquals(medication1,medication2);
        assertEquals(medication1.hashCode(),medication2.hashCode());

        assertNotEquals(medication1,medication3);
    }

    @Test
    public void testObservationResource(){
        ObservationResource observation1 = new ObservationResource();
        ObservationResource observation2 = new ObservationResource();
        ObservationResource observation3 = new ObservationResource();

        observation1.setId(1L);
        observation1.setText("text1");
        observation1.setResourceId("ID1");
        observation1.setSubjectId("ID1");
        observation1.setSubjectType("Type1");

        observation2.setId(1L);
        observation2.setText("text1");
        observation2.setResourceId("ID1");
        observation2.setSubjectId("ID1");
        observation2.setSubjectType("Type1");

        observation3.setId(2L);
        observation3.setText("text2");
        observation3.setResourceId("ID2");
        observation3.setSubjectId("ID2");
        observation3.setSubjectType("Type2");

        assertEquals(observation1,observation2);
        assertEquals(observation1.hashCode(),observation2.hashCode());

        assertNotEquals(observation1,observation3);
    }

    @Test
    public void testOperationDefinitionResource(){
        OperationDefinitionResource operation1 = new OperationDefinitionResource();
        OperationDefinitionResource operation2 = new OperationDefinitionResource();
        OperationDefinitionResource operation3 = new OperationDefinitionResource();

        operation1.setId(1L);
        operation1.setText("text1");
        operation1.setResourceId("ID1");

        operation2.setId(1L);
        operation2.setText("text1");
        operation2.setResourceId("ID1");

        operation3.setId(2L);
        operation3.setText("Another text");
        operation3.setResourceId("Another ID");

        assertEquals(operation1,operation2);
        assertEquals(operation1.hashCode(),operation2.hashCode());

        assertNotEquals(operation1,operation3);
    }

    @Test
    public void testPlanDefinitionResource(){
        PlanDefinitionResource planDefinition1 = new PlanDefinitionResource();
        PlanDefinitionResource planDefinition2 = new PlanDefinitionResource();
        PlanDefinitionResource planDefinition3 = new PlanDefinitionResource();

        planDefinition1.setId(1L);
        planDefinition1.setText("text1");
        planDefinition1.setResourceId("ID1");
        planDefinition1.setType("Type1");

        planDefinition2.setId(1L);
        planDefinition2.setText("text1");
        planDefinition2.setResourceId("ID1");
        planDefinition2.setType("Type1");

        planDefinition3.setId(2L);
        planDefinition3.setText("Another text");
        planDefinition3.setResourceId("Another ID");
        planDefinition3.setType("Type2");

        assertEquals(planDefinition1,planDefinition2);
        assertEquals(planDefinition1.hashCode(),planDefinition2.hashCode());

        assertNotEquals(planDefinition1,planDefinition3);
    }

    @Test
    public void testQuestionnaireMaster(){
        QuestionnaireMaster questionnaire1 = new QuestionnaireMaster();
        QuestionnaireMaster questionnaire2 = new QuestionnaireMaster();
        QuestionnaireMaster questionnaire3 = new QuestionnaireMaster();

        questionnaire1.setId(1);
        questionnaire1.setText("text1");
        questionnaire1.setResourceId("ID1");
        questionnaire1.setVersion("V1");

        questionnaire2.setId(1);
        questionnaire2.setText("text1");
        questionnaire2.setResourceId("ID1");
        questionnaire2.setVersion("V1");

        questionnaire3.setId(2);
        questionnaire3.setText("text2");
        questionnaire3.setResourceId("ID2");
        questionnaire3.setVersion("V2");

        assertEquals(questionnaire1,questionnaire2);
        assertEquals(questionnaire1.hashCode(),questionnaire2.hashCode());

        assertNotEquals(questionnaire1,questionnaire3);
    }

    @Test
    public void testRelatedPersonResource(){
        RelatedPersonResource relatedPerson1 = new RelatedPersonResource();
        RelatedPersonResource relatedPerson2 = new RelatedPersonResource();
        RelatedPersonResource relatedPerson3 = new RelatedPersonResource();

        relatedPerson1.setId(1L);
        relatedPerson1.setText("text1");
        relatedPerson1.setResourceId("ID1");
        relatedPerson1.setPatientId("ID1");

        relatedPerson2.setId(1L);
        relatedPerson2.setText("text1");
        relatedPerson2.setResourceId("ID1");
        relatedPerson2.setPatientId("ID1");

        relatedPerson3.setId(2L);
        relatedPerson3.setText("text2");
        relatedPerson3.setResourceId("ID2");
        relatedPerson3.setPatientId("ID2");

        assertEquals(relatedPerson1,relatedPerson2);
        assertEquals(relatedPerson1.hashCode(),relatedPerson2.hashCode());

        assertNotEquals(relatedPerson1,relatedPerson3);
    }

    @Test
    public void testStructureDefinitionResource(){
        StructureDefinitionResource structure1 = new StructureDefinitionResource();
        StructureDefinitionResource structure2 = new StructureDefinitionResource();
        StructureDefinitionResource structure3 = new StructureDefinitionResource();

        structure1.setId(1L);
        structure1.setText("text1");
        structure1.setResourceId("ID1");

        structure2.setId(1L);
        structure2.setText("text1");
        structure2.setResourceId("ID1");

        structure3.setId(2L);
        structure3.setText("Another text");
        structure3.setResourceId("Another ID");

        assertEquals(structure1,structure2);
        assertEquals(structure1.hashCode(),structure2.hashCode());

        assertNotEquals(structure1,structure3);
    }

    @Test
    public void testStructureMapResource(){
        StructureMapResource structure1 = new StructureMapResource();
        StructureMapResource structure2 = new StructureMapResource();
        StructureMapResource structure3 = new StructureMapResource();

        structure1.setId(1L);
        structure1.setText("text1");
        structure1.setResourceId("ID1");

        structure2.setId(1L);
        structure2.setText("text1");
        structure2.setResourceId("ID1");

        structure3.setId(2L);
        structure3.setText("Another text");
        structure3.setResourceId("Another ID");

        assertEquals(structure1,structure2);
        assertEquals(structure1.hashCode(),structure2.hashCode());

        assertNotEquals(structure1,structure3);
    }

    @Test
    public void testValueSetResource(){
        ValueSetResource valueSet1 = new ValueSetResource();
        ValueSetResource valueSet2 = new ValueSetResource();
        ValueSetResource valueSet3 = new ValueSetResource();

        valueSet1.setId(1L);
        valueSet1.setText("text1");
        valueSet1.setResourceId("ID1");
        valueSet1.setType("Type1");

        valueSet2.setId(1L);
        valueSet2.setText("text1");
        valueSet2.setResourceId("ID1");
        valueSet2.setType("Type1");

        valueSet3.setId(2L);
        valueSet3.setText("Another text");
        valueSet3.setResourceId("Another ID");
        valueSet3.setType("Type2");

        assertEquals(valueSet1,valueSet2);
        assertEquals(valueSet1.hashCode(),valueSet2.hashCode());

        assertNotEquals(valueSet1,valueSet3);
    }

    @Test
    public void testOrganizationResource(){
        OrganizationResource organization1 = new OrganizationResource();
        OrganizationResource organization2 = new OrganizationResource();
        OrganizationResource organization3 = new OrganizationResource();

        organization1.setId(1L);
        organization1.setText("text1");
        organization1.setResourceId("ID1");
        organization1.setType("Type1");

        organization2.setId(1L);
        organization2.setText("text1");
        organization2.setResourceId("ID1");
        organization2.setType("Type1");

        organization3.setId(2L);
        organization3.setText("Another text");
        organization3.setResourceId("Another ID");
        organization3.setType("Type2");

        assertEquals(organization1,organization2);
        assertEquals(organization1.hashCode(),organization2.hashCode());

        assertNotEquals(organization1,organization3);
    }
}
