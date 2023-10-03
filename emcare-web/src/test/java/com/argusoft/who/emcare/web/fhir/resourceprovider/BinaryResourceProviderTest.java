package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import com.argusoft.who.emcare.web.fhir.service.BinaryResourceService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Binary;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BinaryResourceProviderTest {

    @Mock
    private BinaryResourceService binaryResourceService;
    @InjectMocks
    private BinaryResourceProvider binaryResourceProvider;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }
    @Test
    void testGetResourceType() {
        Class<? extends IBaseResource> resultClass = binaryResourceProvider.getResourceType();
        assertEquals(Binary.class,resultClass);
    }

    @Test
    void testAddBinaryResource() {
        Binary binary = new Binary();
        binary.setContentType("testContentType");
        binary.setData(new byte[] { 0x48, 0x65, 0x6C, 0x6C, 0x6F });
        binary.setId("123");
        binary.getMeta().setVersionId("1");

        when(binaryResourceService.saveResource(any(Binary.class))).thenReturn(binary);

        MethodOutcome result = binaryResourceProvider.addBinaryResource(binary);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("1",result.getId().getVersionIdPart());
        assertEquals(binary,result.getResource());
        verify(binaryResourceService,times(1)).saveResource(binary);
    }

    @Test
    void testGetResourceById() {
        String id = "123";

        Binary binary = new Binary();
        binary.setId(id);

        when(binaryResourceService.getResourceById(any())).thenReturn(binary);

        Binary result =  binaryResourceProvider.getResourceById(new IdType(id));

        assertNotNull(result);
        assertEquals(binary.getId(),result.getId());
        assertEquals(binary,result);

    }

    @Test
    void testUpdateBinaryResource() {
        String id = "123";
        Binary binary = new Binary();
        binary.setId(id);

        MethodOutcome expectedMethodOutcome = new MethodOutcome();
        expectedMethodOutcome.setId(new IdType(binary.getId(), "1"));
        expectedMethodOutcome.setResource(binary);


        when(binaryResourceService.updateBinaryResource(any(IdType.class),any(Binary.class))).thenReturn(expectedMethodOutcome);

        MethodOutcome methodOutcome = binaryResourceProvider.updateBinaryResource(new IdType(id), binary);

        assertNotNull(methodOutcome);
        assertEquals(expectedMethodOutcome.getId(), methodOutcome.getId());
        assertEquals(expectedMethodOutcome.getResource(), methodOutcome.getResource());
        verify(binaryResourceService).updateBinaryResource(new IdType(id), binary);

    }

    @Test
    public void testGetAllBinaryResource() {
        DateParam dateParam = new DateParam("2023-09-20");

        Binary binary1 = new Binary();
        binary1.setId("123");
        Binary binary2 = new Binary();
        binary1.setId("456");


        List<Binary> expectedList = new ArrayList<>();
        expectedList.add(binary1);
        expectedList.add(binary2);

        when(binaryResourceService.getAllBinaryResource(dateParam)).thenReturn(expectedList);

        List<Binary> actualBinaries = binaryResourceProvider.getAllBinaryResource(dateParam);

        assertNotNull(actualBinaries);
        assertEquals(expectedList.size(), actualBinaries.size());

        for (int i = 0; i < expectedList.size(); i++) {
            Binary expectedBinary = expectedList.get(i);
            Binary actualBinary = actualBinaries.get(i);
            assertNotNull(actualBinary);
            assertEquals(expectedBinary.getId(), actualBinary.getId());
        }

        verify(binaryResourceService).getAllBinaryResource(dateParam);
    }
}