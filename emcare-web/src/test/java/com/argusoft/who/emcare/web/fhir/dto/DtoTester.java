package com.argusoft.who.emcare.web.fhir.dto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meanbean.test.BeanTester;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DtoTester {
    @Test
    void testAllDtos(){
        BeanTester beanTester = new BeanTester();
        beanTester.testBean(ActivityDefinitionDto.class);
        beanTester.testBean(CodeSystemDto.class);
        beanTester.testBean(FacilityDto.class);
        beanTester.testBean(FacilityMapDto.class);
        beanTester.testBean(LibraryDto.class);
        beanTester.testBean(MedicationCodeDto.class);
        beanTester.testBean(OperationDefinitionDto.class);
        beanTester.testBean(OrganizationDto.class);
        beanTester.testBean(PatientDto.class);
        beanTester.testBean(QuestionnaireDto.class);
        beanTester.testBean(StructureDefinitionDto.class);
        beanTester.testBean(StructureMapDto.class);
        beanTester.testBean(MedicationDto.class);
    }
}
