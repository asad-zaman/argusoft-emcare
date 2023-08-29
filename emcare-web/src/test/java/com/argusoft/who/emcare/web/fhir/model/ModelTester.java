package com.argusoft.who.emcare.web.fhir.model;

import com.argusoft.who.emcare.web.fhir.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meanbean.test.BeanTester;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ModelTester {
    @Test
    void testAllModelGetterAndSetter(){
        BeanTester beanTester = new BeanTester();
        beanTester.testBean(ActivityDefinitionResource.class);
        beanTester.testBean(AuditEventResource.class);
        beanTester.testBean(BinaryResource.class);
        beanTester.testBean(CodeSystemResource.class);
        beanTester.testBean(ConditionResource.class);
        beanTester.testBean(EmcareResource.class);
        beanTester.testBean(EncounterResource.class);
        beanTester.testBean(LibraryResource.class);
        beanTester.testBean(LocationResource.class);
        beanTester.testBean(MedicationResource.class);
        beanTester.testBean(ObservationResource.class);
        beanTester.testBean(OperationDefinitionResource.class);
        beanTester.testBean(PlanDefinitionResource.class);
        beanTester.testBean(QuestionnaireMaster.class);
        beanTester.testBean(RelatedPersonResource.class);
        beanTester.testBean(StructureDefinitionResource.class);
        beanTester.testBean(StructureMapResource.class);
        beanTester.testBean(ValueSetResource.class);
    }
}
