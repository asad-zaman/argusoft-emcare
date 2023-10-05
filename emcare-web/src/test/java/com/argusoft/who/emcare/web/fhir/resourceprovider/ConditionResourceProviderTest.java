package com.argusoft.who.emcare.web.fhir.resourceprovider;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.ConditionResourceService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.IdType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
class ConditionResourceProviderTest {
    @Mock
    private ConditionResourceService conditionResourceService;
    @InjectMocks
    private  ConditionResourceProvider conditionResourceProvider;
    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testGetResourceType() {
        Class<? extends IBaseResource> expectedResourceType = Condition.class;
        Class<? extends IBaseResource> result = conditionResourceProvider.getResourceType();
        assertEquals(expectedResourceType, result);
    }
    @Test
    void testCreateCondition() {
        Condition condition = getCondition();
        MethodOutcome expectedResult = new MethodOutcome();
        expectedResult.setId(new IdType(CommonConstant.CONDITION, condition.getId(), "1"));
        expectedResult.setResource(condition);
        when(conditionResourceService.saveResource(any(Condition.class))).thenReturn(condition);
        MethodOutcome result = conditionResourceProvider.createCondition(condition);
        assertEquals(expectedResult.getId(), result.getId());
        assertEquals(expectedResult.getResource(), result.getResource());
        verify(conditionResourceService, times(1)).saveResource(condition);
    }
    @Test
    void testGetResourceById() {
        String resourceId = "1";
        Condition condition = getCondition();
        when(conditionResourceService.getResourceById(resourceId)).thenReturn(condition);
        IdType id = new IdType(resourceId);
        Condition result = conditionResourceProvider.getResourceById(id);
        assertEquals(condition, result);
        assertEquals("1", result.getId());
        assertEquals("Rules", result.getImplicitRules());
        assertEquals("English", result.getLanguage());
        verify(conditionResourceService, times(1)).getResourceById(id.getIdPart());
    }
    @Test
    public void testUpdateStructureMapResource() {
        String resourceId = "1";
        Condition condition = getCondition();
        MethodOutcome expectedResult = new MethodOutcome();
        expectedResult.setId(new IdType(CommonConstant.CONDITION, resourceId, "1"));
        expectedResult.setResource(condition);
        when(conditionResourceService.updateConditionResource(any(IdType.class), eq(condition))).thenReturn(expectedResult);
        IdType id = new IdType(resourceId);
        MethodOutcome result = conditionResourceProvider.updateConditionResource(id, condition);
        assertEquals(expectedResult.getId(), result.getId());
        assertEquals(expectedResult.getResource(), result.getResource());
        verify(conditionResourceService, times(1)).updateConditionResource(id, condition);
    }
    @Test
    public void testGetAllActivityDefinition() {
        DateParam dateParam = new DateParam();
        String searchString = null;
        List<Condition> conditions = new ArrayList<>();
        Condition condition1 = getCondition();
        Condition condition2 = getCondition();
        condition2.setId("2");
        conditions.add(condition1);
        conditions.add(condition2);
        when(conditionResourceService.getAllCondition(dateParam, searchString)).thenReturn(conditions);
        List<Condition> result = conditionResourceProvider.getAllCondition(dateParam,searchString);
        assertEquals(conditions, result);
        assertEquals(conditions.get(0),result.get(0));
        assertEquals(conditions.get(1),result.get(1));
        verify(conditionResourceService, times(1)).getAllCondition(dateParam,searchString);
    }
    @Test
    void getConditionByPatientId() {
        String patientId = "1";
        List<Condition> conditions = new ArrayList<>();
        Condition condition = getCondition();
        conditions.add(condition);
        when(conditionResourceService.getByPatientId(patientId)).thenReturn(conditions);
        List<Condition> result = conditionResourceProvider.getConditionByPatientId(patientId);
        assertEquals(conditions,result);
        assertEquals(conditions.get(0),result.get(0));
        verify(conditionResourceService, times(1)).getByPatientId(patientId);
    }
    private Condition getCondition(){
        Condition condition = new Condition();
        condition.setId("1");
        condition.setImplicitRules("Rules");
        condition.setLanguage("English");
        return condition;
    }
}