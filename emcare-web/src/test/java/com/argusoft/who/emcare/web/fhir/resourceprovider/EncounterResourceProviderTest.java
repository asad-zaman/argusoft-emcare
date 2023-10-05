package com.argusoft.who.emcare.web.fhir.resourceprovider;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.EncounterResourceService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class EncounterResourceProviderTest {
    @Mock
    private EncounterResourceService encounterResourceService;
    @InjectMocks
    private EncounterResourceProvider encounterResourceProvider;
    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testGetResourceType() {
        Class<? extends IBaseResource> expectedResourceType = Encounter.class;
        Class<? extends IBaseResource> result = encounterResourceProvider.getResourceType();
        assertEquals(expectedResourceType, result);
    }
    @Test
    void testCreateEncounter() {
        Encounter encounter = getEncounter();
        MethodOutcome expectedResult = new MethodOutcome();
        expectedResult.setId(new IdType(CommonConstant.ENCOUNTER, encounter.getId(), "1"));
        expectedResult.setResource(encounter);
        when(encounterResourceService.saveResource(any(Encounter.class))).thenReturn(encounter);
        MethodOutcome result = encounterResourceProvider.createEncounter(encounter);
        assertEquals(expectedResult.getId(), result.getId());
        assertEquals(expectedResult.getResource(), result.getResource());
        verify(encounterResourceService, times(1)).saveResource(encounter);
    }
    @Test
    void testGetResourceById() {
        String resourceId = "1";
        Encounter encounter = getEncounter();
        when(encounterResourceService.getResourceById(resourceId)).thenReturn(encounter);
        IdType id = new IdType(resourceId);
        Encounter result = encounterResourceProvider.getResourceById(id);
        assertEquals(encounter, result);
        assertEquals("1", result.getId());
        assertEquals("Rules", result.getImplicitRules());
        assertEquals("English", result.getLanguage());
        verify(encounterResourceService, times(1)).getResourceById(resourceId);
    }
    @Test
    public void testUpdateStructureMapResource() {
        String resourceId = "1";
        Encounter encounter = getEncounter();
        MethodOutcome expectedResult = new MethodOutcome();
        expectedResult.setId(new IdType(CommonConstant.ENCOUNTER, resourceId, "1"));
        expectedResult.setResource(encounter);
        when(encounterResourceService.updateEncounterResource(any(IdType.class), eq(encounter))).thenReturn(expectedResult);
        IdType id = new IdType(resourceId);
        MethodOutcome result = encounterResourceProvider.updateEncounterResource(id, encounter);
        assertEquals(expectedResult.getId(), result.getId());
        assertEquals(expectedResult.getResource(), result.getResource());
        verify(encounterResourceService, times(1)).updateEncounterResource(id, encounter);
    }
    @Test
    void testGetAllEncounter() {
        DateParam dateParam = new DateParam();
        String searchText= null;
        String theId = "1";
        List<Encounter> encounters = new ArrayList<>();
        Encounter encounter1 = getEncounter();
        Encounter encounter2 = getEncounter();
        encounter2.setId("2");
        encounters.add(encounter1);
        encounters.add(encounter2);
        when(encounterResourceService.getAllEncounter(dateParam, searchText, theId)).thenReturn(encounters);
        List<Encounter> result = encounterResourceProvider.getAllEncounter(dateParam,searchText,theId);
        assertEquals(encounters, result);
        assertEquals(encounters.get(0),result.get(0));
        assertEquals(encounters.get(1),result.get(1));
        verify(encounterResourceService, times(1)).getAllEncounter(dateParam,searchText, theId);
    }
    @Test
    void testGetEncounterCountBasedOnDate() {
        DateParam dateParam = new DateParam();
        String theId = "1";
        String type = "type";
        List<Encounter> encounters = new ArrayList<>();
        Bundle bundle = new Bundle();
        Encounter encounter1 = getEncounter();
        Encounter encounter2 = getEncounter();
        encounter2.setId("2");
        encounters.add(encounter1);
        encounters.add(encounter2);
        bundle.setTotal(encounters.size());
        when(encounterResourceService.getEncounterCountBasedOnDate(type,dateParam,theId)).thenReturn(bundle);
        Bundle result = encounterResourceProvider.getEncounterCountBasedOnDate(type,dateParam,theId);
        assertEquals(result.getTotal(),2);
    }
    private Encounter getEncounter(){
        Encounter encounter = new Encounter();
        encounter.setId("1");
        encounter.setIdBase("1");
        encounter.setImplicitRules("Rules");
        encounter.setLanguage("English");
        return encounter;
    }
}