package com.argusoft.who.emcare.web.indicators.codes.repository;

import com.argusoft.who.emcare.web.indicators.codes.entity.EmCareCustomCodeSystem;
import com.argusoft.who.emcare.web.indicators.codes.service.impl.CustomCodeServiceImplementation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class EmCareCustomCodeSystemRepositoryTest {

    @Mock
    private EmCareCustomCodeSystemRepository emCareCustomCodeSystemRepository;

    @InjectMocks
    private CustomCodeServiceImplementation customCodeServiceImplementation;

    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void testFindByCode() {
        String code = "TEST_CODE";
        String codeDescription = "Test Code Description";
        String valueType = "String";
        String[] condition = {"=Equal To", ">Greater Than"};
        String[] value = {"TestValue1", "TestValue2"};
        EmCareCustomCodeSystem expectedCodeSystem = new EmCareCustomCodeSystem();

        when(emCareCustomCodeSystemRepository.findByCode(code)).thenReturn(expectedCodeSystem);

        EmCareCustomCodeSystem result = emCareCustomCodeSystemRepository.findByCode(code);

        assertSame(expectedCodeSystem, result);
        assertEquals(expectedCodeSystem.getCode(),result.getCode());
        assertEquals(expectedCodeSystem.getCodeDescription(),result.getCodeDescription());
        assertEquals(expectedCodeSystem.getCondition(),result.getCondition());
    }

    @Test
    public void testFindByCodeAndCodeIdNot() {
        String code = "Emcare.B12.A3";
        Long codeId = 1L;
        String codeDescription = "Test Code Description";
        String valueType = "String";
        String[] condition = {"=Equal To", ">Greater Than"};
        String[] value = {"TestValue1", "TestValue2"};
        EmCareCustomCodeSystem expectedCodeSystem = new EmCareCustomCodeSystem();

        when(emCareCustomCodeSystemRepository.findByCodeAndCodeIdNot(code, codeId)).thenReturn(expectedCodeSystem);

        EmCareCustomCodeSystem result = emCareCustomCodeSystemRepository.findByCodeAndCodeIdNot(code, codeId);

        assertSame(expectedCodeSystem, result);
        assertEquals(expectedCodeSystem.getCode(),result.getCode());
        assertEquals(expectedCodeSystem.getCodeDescription(),result.getCodeDescription());
        assertEquals(expectedCodeSystem.getCondition(),result.getCondition());
    }

    @Test
    public void testFindByCodeContainingIgnoreCaseOrCodeDescriptionContainingIgnoreCase() {
        String searchString = "Vomit";

        List<EmCareCustomCodeSystem> expectedCodeSystems = new ArrayList<>();
        List<EmCareCustomCodeSystem> allCodeSystems = new ArrayList<>();

        EmCareCustomCodeSystem expectedCodeSystem1 = new EmCareCustomCodeSystem();
        expectedCodeSystem1.setCode("EmCare.256.23");
        expectedCodeSystem1.setCodeId(5160L);
        expectedCodeSystem1.setCodeDescription("High Fever");
        expectedCodeSystem1.setValueType("Boolean");
        expectedCodeSystem1.setCondition(new String[]{"=Equal To"});
        expectedCodeSystem1.setValue(new String[]{"true", "false", "null"});

        EmCareCustomCodeSystem expectedCodeSystem2 = new EmCareCustomCodeSystem();
        expectedCodeSystem2.setCode("EmCare.B23");
        expectedCodeSystem2.setCodeId(12340L);
        expectedCodeSystem2.setCodeDescription("Is Vomiting?");
        expectedCodeSystem2.setValueType("Boolean");
        expectedCodeSystem2.setCondition(new String[]{"=Equal To"});
        expectedCodeSystem2.setValue(new String[]{"true", "false", "null"});

        expectedCodeSystems.add(expectedCodeSystem2);

        allCodeSystems.add(expectedCodeSystem1);
        allCodeSystems.add(expectedCodeSystem2);

        when(emCareCustomCodeSystemRepository.findByCodeContainingIgnoreCaseOrCodeDescriptionContainingIgnoreCase(
                searchString, searchString, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(expectedCodeSystems));

        Page<EmCareCustomCodeSystem> result = emCareCustomCodeSystemRepository.findByCodeContainingIgnoreCaseOrCodeDescriptionContainingIgnoreCase(searchString, searchString, Pageable.unpaged());

        assertEquals(expectedCodeSystems, result.getContent());
        assertFalse(result.getContent().contains(expectedCodeSystem1));
        assertTrue(result.hasContent());
        assertEquals(1, result.getNumberOfElements());
    }

    @Test
    public void testFindByCodeContainingIgnoreCaseOrCodeDescriptionContainingIgnoreCaseWithoutPagination() {
        String searchString = "temperature";

        List<EmCareCustomCodeSystem> expectedCodeSystems = new ArrayList<>();
        List<EmCareCustomCodeSystem> allCodeSystems = new ArrayList<>();

        EmCareCustomCodeSystem expectedCodeSystem = new EmCareCustomCodeSystem();
        expectedCodeSystem.setCode("EmCare.C21.23");
        expectedCodeSystem.setCodeId(31860L);
        expectedCodeSystem.setCodeDescription("Severe Illness");
        expectedCodeSystem.setValueType("Boolean");
        expectedCodeSystem.setCondition(new String[]{"=Equal To"});
        expectedCodeSystem.setValue(new String[]{"true", "false", "null"});

        EmCareCustomCodeSystem expectedCodeSystem2 = new EmCareCustomCodeSystem();
        expectedCodeSystem2.setCode("EmCare.B6.D12");
        expectedCodeSystem2.setCodeId(12340L);
        expectedCodeSystem2.setCodeDescription("Is body temperature above 98.4 Degree?");
        expectedCodeSystem2.setValueType("Boolean");
        expectedCodeSystem2.setCondition(new String[]{"=Equal To"});
        expectedCodeSystem2.setValue(new String[]{"true", "false", "null"});

        expectedCodeSystems.add(expectedCodeSystem2);

        allCodeSystems.add(expectedCodeSystem);
        allCodeSystems.add(expectedCodeSystem2);

        when(emCareCustomCodeSystemRepository.findByCodeContainingIgnoreCaseOrCodeDescriptionContainingIgnoreCase(searchString, searchString)).thenReturn(expectedCodeSystems);

        List<EmCareCustomCodeSystem> result = emCareCustomCodeSystemRepository.findByCodeContainingIgnoreCaseOrCodeDescriptionContainingIgnoreCase(searchString, searchString);

        assertEquals(expectedCodeSystems, result);
        assertFalse(result.contains(expectedCodeSystem));
        assertTrue(result.contains(expectedCodeSystem2));
        assertEquals(1, result.size());
    }
}