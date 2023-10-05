package com.argusoft.who.emcare.web.fhir.resourceprovider;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.CodeSystemResourceService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.Enumerations;
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
class CodeSystemResourceProviderTest {
    @Mock
    private CodeSystemResourceService codeSystemResourceService;
    @InjectMocks
    private CodeSystemResourceProvider codeSystemResourceProvider;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testGetResourceType() {
        Class<? extends IBaseResource> expectedResourceType = CodeSystem.class;
        Class<? extends IBaseResource> result = codeSystemResourceProvider.getResourceType();
        assertEquals(expectedResourceType, result);
    }
    @Test
    void testCreateStructureMap() {
        CodeSystem codeSystem = getCodeSystem();
        MethodOutcome expectedResult = new MethodOutcome();
        expectedResult.setId(new IdType(CommonConstant.LOCATION_TYPE_STRING, codeSystem.getId(), "1"));
        expectedResult.setResource(codeSystem);
        when(codeSystemResourceService.saveResource(any(CodeSystem.class))).thenReturn(codeSystem);
        MethodOutcome result = codeSystemResourceProvider.createStructureMap(codeSystem);
        assertEquals(expectedResult.getId(), result.getId());
        assertEquals(expectedResult.getResource(), result.getResource());
        verify(codeSystemResourceService, times(1)).saveResource(codeSystem);
    }
    @Test
    public void testGetResourceById() {
        String resourceId = "123";
        CodeSystem codeSystem = getCodeSystem();
        when(codeSystemResourceService.getResourceById(resourceId)).thenReturn(codeSystem);
        IdType id = new IdType(resourceId);
        CodeSystem result = codeSystemResourceProvider.getResourceById(id);
        assertEquals(codeSystem, result);
        assertEquals("123", result.getId());
        assertEquals("SampleCodeSystem", result.getName());
        assertEquals("Sample Title", result.getTitle());
        assertEquals(Enumerations.PublicationStatus.ACTIVE, result.getStatus());
        verify(codeSystemResourceService, times(1)).getResourceById(resourceId);
    }
    @Test
    public void testUpdateStructureMapResource() {
        String resourceId = "123";
        CodeSystem codeSystem = getCodeSystem();
        MethodOutcome expectedResult = new MethodOutcome();
        expectedResult.setId(new IdType(CommonConstant.CODE_SYSTEM, resourceId, "1"));
        expectedResult.setResource(codeSystem);
        when(codeSystemResourceService.updateCodeSystem(any(IdType.class), eq(codeSystem))).thenReturn(expectedResult);
        IdType id = new IdType(resourceId);
        MethodOutcome result = codeSystemResourceProvider.updateStructureMapResource(id, codeSystem);
        assertEquals(expectedResult.getId(), result.getId());
        assertEquals(expectedResult.getResource(), result.getResource());
        verify(codeSystemResourceService, times(1)).updateCodeSystem(id, codeSystem);
    }
    @Test
    public void testGetAllActivityDefinition() {
        DateParam dateParam = new DateParam();
        List<CodeSystem> codeSystems = new ArrayList<>();
        CodeSystem codeSystem1 = getCodeSystem();
        CodeSystem codeSystem2 = getCodeSystem();
        codeSystem2.setId("1234");
        codeSystems.add(codeSystem1);
        codeSystems.add(codeSystem2);
        when(codeSystemResourceService.getAllCodeSystem(dateParam)).thenReturn(codeSystems);
        List<CodeSystem> result = codeSystemResourceProvider.getAllStructureMap(dateParam);
        assertEquals(codeSystems, result);
        assertEquals(codeSystems.get(0),result.get(0));
        assertEquals(codeSystems.get(1),result.get(1));
        verify(codeSystemResourceService, times(1)).getAllCodeSystem(dateParam);
    }
    private CodeSystem getCodeSystem(){
        CodeSystem codeSystem = new CodeSystem();
        codeSystem.setId("123");
        codeSystem.setName("SampleCodeSystem");
        codeSystem.setTitle("Sample Title");
        codeSystem.setStatus(Enumerations.PublicationStatus.ACTIVE);
        return codeSystem;
    }
}