package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.ActivityDefinitionResourceService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.IdType;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ActivityDefinitionResourceProviderTest {

    @Mock
    private ActivityDefinitionResourceService activityDefinitionResourceService;

    @InjectMocks
    private ActivityDefinitionResourceProvider activityDefinitionResourceProvider;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateActivityDefinition() {
        ActivityDefinition activityDefinitionData = new ActivityDefinition();
        activityDefinitionData.setId("1");
        activityDefinitionData.setName("SampleActivityDefinition");
        activityDefinitionData.setTitle("Sample Title");
        activityDefinitionData.setStatus(Enumerations.PublicationStatus.ACTIVE);
        activityDefinitionData.setSubtitle("Sample Subtitle");

        MethodOutcome expectedResult = new MethodOutcome();
        expectedResult.setId(new IdType(CommonConstant.LOCATION_TYPE_STRING, "1", "1"));
        expectedResult.setResource(activityDefinitionData);

        when(activityDefinitionResourceService.saveResource(activityDefinitionData)).thenReturn(activityDefinitionData);

        MethodOutcome result = activityDefinitionResourceProvider.createActivityDefinition(activityDefinitionData);

        assertEquals(expectedResult.getId(), result.getId());
        assertEquals(expectedResult.getResource(), result.getResource());
        verify(activityDefinitionResourceService, times(1)).saveResource(activityDefinitionData);
    }

    @Test
    public void testGetResourceById() {
        String resourceId = "123";
        ActivityDefinition expectedDefinition = new ActivityDefinition();
        expectedDefinition.setId("123");
        expectedDefinition.setName("SampleActivityDefinition");
        expectedDefinition.setTitle("Sample Title");
        expectedDefinition.setStatus(Enumerations.PublicationStatus.ACTIVE);
        expectedDefinition.setSubtitle("Sample Subtitle");

        when(activityDefinitionResourceService.getResourceById(resourceId)).thenReturn(expectedDefinition);

        IdType id = new IdType(resourceId);
        ActivityDefinition result = activityDefinitionResourceProvider.getResourceById(id);

        assertEquals(expectedDefinition, result);
        Assertions.assertEquals("123", result.getId());
        Assertions.assertEquals("SampleActivityDefinition", result.getName());
        Assertions.assertEquals("Sample Title", result.getTitle());
        Assertions.assertEquals(Enumerations.PublicationStatus.ACTIVE, result.getStatus());
        Assertions.assertEquals("Sample Subtitle", result.getSubtitle());
        verify(activityDefinitionResourceService, times(1)).getResourceById(resourceId);
    }

    @Test
    public void testUpdateStructureMapResource() {
        String resourceId = "123";
        ActivityDefinition updatedDefinition = new ActivityDefinition();
        updatedDefinition.setId("123");
        updatedDefinition.setName("SampleActivityDefinition");
        updatedDefinition.setTitle("Sample Title");
        updatedDefinition.setStatus(Enumerations.PublicationStatus.ACTIVE);
        updatedDefinition.setSubtitle("Sample Subtitle");

        MethodOutcome expectedResult = new MethodOutcome();
        expectedResult.setId(new IdType(CommonConstant.LOCATION_TYPE_STRING, resourceId, "1"));
        expectedResult.setResource(updatedDefinition);

        when(activityDefinitionResourceService.updateActivityDefinitionResource(any(IdType.class), eq(updatedDefinition))).thenReturn(expectedResult);

        IdType id = new IdType(resourceId);
        MethodOutcome result = activityDefinitionResourceProvider.updateStructureMapResource(id, updatedDefinition);

        assertEquals(expectedResult.getId(), result.getId());
        assertEquals(expectedResult.getResource(), result.getResource());
        verify(activityDefinitionResourceService, times(1)).updateActivityDefinitionResource(id, updatedDefinition);
    }

    @Test
    public void testGetAllActivityDefinition() {
        DateParam dateParam = new DateParam();
        List<ActivityDefinition> expectedDefinitions = new ArrayList<>();

        //ActivityDefinition data 1
        ActivityDefinition ad1 = new ActivityDefinition();
        ad1.setId("1");
        ad1.setName("SampleActivityDefinition");
        ad1.setTitle("Sample Title");
        ad1.setStatus(Enumerations.PublicationStatus.ACTIVE);
        ad1.setSubtitle("Sample Subtitle");

        //ActivityDefinition data 2
        ActivityDefinition ad2 = new ActivityDefinition();
        ad2.setId("2");
        ad2.setName("temp");
        ad2.setTitle("temp");
        ad2.setStatus(Enumerations.PublicationStatus.ACTIVE);
        ad2.setSubtitle("temp");

        expectedDefinitions.add(ad1);
        expectedDefinitions.add(ad2);

        when(activityDefinitionResourceService.getAllActivityDefinition(dateParam)).thenReturn(expectedDefinitions);

        List<ActivityDefinition> result = activityDefinitionResourceProvider.getAllActivityDefinition(dateParam);

        assertEquals(expectedDefinitions, result);
        assertEquals(expectedDefinitions.get(0),result.get(0));
        assertEquals(expectedDefinitions.get(1),result.get(1));
        verify(activityDefinitionResourceService, times(1)).getAllActivityDefinition(dateParam);
    }

    @Test
    public void testGetResourceType() {
        Class<? extends IBaseResource> expectedResourceType = ActivityDefinition.class;
        Class<? extends IBaseResource> result = activityDefinitionResourceProvider.getResourceType();
        assertEquals(expectedResourceType, result);
    }
}
