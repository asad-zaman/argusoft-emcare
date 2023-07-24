package com.argusoft.who.emcare.web.indicators.codes.service;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.indicators.codes.dto.CustomCodeRequestDto;
import com.argusoft.who.emcare.web.indicators.codes.entity.EmCareCustomCodeSystem;
import com.argusoft.who.emcare.web.indicators.codes.repository.EmCareCustomCodeSystemRepository;
import com.argusoft.who.emcare.web.indicators.codes.service.impl.CustomCodeServiceImplementation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static com.argusoft.who.emcare.web.indicators.codes.mapper.CustomCodeMapper.getEmCareCustomCodeSystem;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {CustomCodeServiceImplementation.class})
@RunWith(SpringJUnit4ClassRunner.class)
class CustomCodeServiceTest {

    @Mock
    EmCareCustomCodeSystemRepository emCareCustomCodeSystemRepository;
    @InjectMocks
    CustomCodeServiceImplementation customCodeServiceImplementation;

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
    void createCustomCode() {
        mock(CustomCodeRequestDto.class);
        mock(EmCareCustomCodeSystemRepository.class);

        CustomCodeRequestDto customCodeRequestDto = new CustomCodeRequestDto();

        customCodeRequestDto.setCode("EmCare.256.23");
        customCodeRequestDto.setCodeId(5160L);
        customCodeRequestDto.setCodeDescription("High Fever");
        customCodeRequestDto.setValueType("Boolean");
        customCodeRequestDto.setCondition(new String[]{"=Equal To"});
        customCodeRequestDto.setValue(new String[]{"true", "false", "null"});

        EmCareCustomCodeSystem expectedResponse = getEmCareCustomCodeSystem(customCodeRequestDto);
        expectedResponse.setCreatedBy("test");
        expectedResponse.setCreatedOn(new Date());
        expectedResponse.setModifiedBy("test");
        expectedResponse.setModifiedOn(new Date());

        when(emCareCustomCodeSystemRepository.findByCode(any(String.class))).thenReturn(null);
        when(emCareCustomCodeSystemRepository.save(any(EmCareCustomCodeSystem.class))).thenReturn(expectedResponse);

        ResponseEntity<Object> response = customCodeServiceImplementation.createCustomCode(customCodeRequestDto);

        verify(emCareCustomCodeSystemRepository, times(1)).save(any(EmCareCustomCodeSystem.class));

        EmCareCustomCodeSystem body = (EmCareCustomCodeSystem) response.getBody();
        assertTrue(body.getCode() == expectedResponse.getCode());
        assertTrue(body.getCodeDescription().equals(customCodeRequestDto.getCodeDescription()));
        assertTrue(body.getCode().equals(customCodeRequestDto.getCode()));
    }

    @Test
    void updateCustomCodeSystem() {
        mock(CustomCodeRequestDto.class);
        mock(EmCareCustomCodeSystemRepository.class);

        CustomCodeRequestDto customCodeRequestDto = new CustomCodeRequestDto();

        customCodeRequestDto.setCode("EmCare.B7.JP28");
        customCodeRequestDto.setCodeId(2160L);
        customCodeRequestDto.setCodeDescription("Auxiliary Temperature");
        customCodeRequestDto.setValueType("Integer");
        customCodeRequestDto.setCondition(new String[]{"=Equal To"});
        customCodeRequestDto.setValue(new String[]{"32"});

        EmCareCustomCodeSystem expectedResponse = getEmCareCustomCodeSystem(customCodeRequestDto);
        expectedResponse.setCreatedBy("test");
        expectedResponse.setCreatedOn(new Date());
        expectedResponse.setModifiedBy("test");
        expectedResponse.setModifiedOn(new Date());

        when(emCareCustomCodeSystemRepository.findByCodeAndCodeIdNot(customCodeRequestDto.getCode(), customCodeRequestDto.getCodeId())).thenReturn(null);
        when(emCareCustomCodeSystemRepository.save(any(EmCareCustomCodeSystem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Object> response = customCodeServiceImplementation.updateCustomCodeSystem(customCodeRequestDto);

        verify(emCareCustomCodeSystemRepository, times(1)).save(any(EmCareCustomCodeSystem.class));

        EmCareCustomCodeSystem body = (EmCareCustomCodeSystem) response.getBody();
        assertTrue(body.getCodeDescription().equals(customCodeRequestDto.getCodeDescription()));
        assertTrue(body.getCode().equals(customCodeRequestDto.getCode()));
    }

    @Test
    void getAllCustomCodeSystem() {
        mock(CustomCodeRequestDto.class);
        mock(EmCareCustomCodeSystemRepository.class);

        List<EmCareCustomCodeSystem> mockCodeSystems = new ArrayList<>();
        EmCareCustomCodeSystem emCareCustomCodeSystem = new EmCareCustomCodeSystem();

        emCareCustomCodeSystem.setCode("EmCare.A12.JP28");
        emCareCustomCodeSystem.setCodeId(9260L);
        emCareCustomCodeSystem.setCodeDescription("Vomits Immediately");
        emCareCustomCodeSystem.setValueType("Boolean");
        emCareCustomCodeSystem.setCondition(new String[]{"=Equal To"});
        emCareCustomCodeSystem.setValue(new String[]{"true", "false", "null"});

        mockCodeSystems.add(emCareCustomCodeSystem);

        emCareCustomCodeSystem.setCode("EmCare.B7.JP76");
        emCareCustomCodeSystem.setCodeId(2160L);
        emCareCustomCodeSystem.setCodeDescription("Auxiliary Temperature");
        emCareCustomCodeSystem.setValueType("Integer");
        emCareCustomCodeSystem.setCondition(new String[]{"=Equal To"});
        emCareCustomCodeSystem.setValue(new String[]{"32"});

        mockCodeSystems.add(emCareCustomCodeSystem);

        when(emCareCustomCodeSystemRepository.findAll()).thenReturn(mockCodeSystems);

        ResponseEntity<Object> response = customCodeServiceImplementation.getAllCustomCodeSystem();

        List<EmCareCustomCodeSystem> returnedCodeSystems = (List<EmCareCustomCodeSystem>) response.getBody();
        assertTrue(returnedCodeSystems.size() == mockCodeSystems.size());
    }

    @Test
    void getCustomCodeById() {
        mock(CustomCodeRequestDto.class);
        mock(EmCareCustomCodeSystemRepository.class);

        EmCareCustomCodeSystem emCareCustomCodeSystem = new EmCareCustomCodeSystem();

        emCareCustomCodeSystem.setCode("EmCare.D1.JP28");
        emCareCustomCodeSystem.setCodeId(13364L);
        emCareCustomCodeSystem.setCodeDescription("Have High Body Temperature");
        emCareCustomCodeSystem.setValueType("Boolean");
        emCareCustomCodeSystem.setCondition(new String[]{"=Equal To"});
        emCareCustomCodeSystem.setValue(new String[]{"true", "false", "null"});

        when(emCareCustomCodeSystemRepository.findById(13364L)).thenReturn(Optional.of(emCareCustomCodeSystem));

        ResponseEntity<Object> response = customCodeServiceImplementation.getCustomCodeById(13364L);

        EmCareCustomCodeSystem body = (EmCareCustomCodeSystem) response.getBody();

        assertTrue(body.getCodeId().equals(emCareCustomCodeSystem.getCodeId()));
        assertTrue(body.getCodeDescription().equals(emCareCustomCodeSystem.getCodeDescription()));
        assertTrue(body.getValueType().equals(emCareCustomCodeSystem.getValueType()));
        assertTrue(body.getCondition().equals(emCareCustomCodeSystem.getCondition()));
    }

    @Test
    void getCustomCodeWithPagination() {
        mock(CustomCodeRequestDto.class);
        mock(EmCareCustomCodeSystemRepository.class);

        EmCareCustomCodeSystem emCareCustomCodeSystem = new EmCareCustomCodeSystem();

        emCareCustomCodeSystem.setCode("EmCare.D1.JP28");
        emCareCustomCodeSystem.setCodeId(13364L);
        emCareCustomCodeSystem.setCodeDescription("Have High Body Temperature");
        emCareCustomCodeSystem.setValueType("Boolean");
        emCareCustomCodeSystem.setCondition(new String[]{"=Equal To"});
        emCareCustomCodeSystem.setValue(new String[]{"true", "false", "null"});

        String searchString = "EmCare.D1.JP28";
        Integer pageNo = 0;

        List<EmCareCustomCodeSystem> mockResults = new ArrayList<>();

        mockResults.add(emCareCustomCodeSystem);

        when(emCareCustomCodeSystemRepository.findByCodeContainingIgnoreCaseOrCodeDescriptionContainingIgnoreCase(searchString, searchString)).thenReturn(mockResults);
        when(emCareCustomCodeSystemRepository.findByCodeContainingIgnoreCaseOrCodeDescriptionContainingIgnoreCase(searchString, searchString, PageRequest.of(pageNo, CommonConstant.PAGE_SIZE, Sort.by("createdOn").descending()))).thenReturn(new PageImpl<>(mockResults));

        PageDto pageDto = customCodeServiceImplementation.getCustomCodeWithPagination(pageNo, searchString);

        EmCareCustomCodeSystem firstMockCodeSystem = mockResults.get(0);
        EmCareCustomCodeSystem firstPageDtoCodeSystem = (EmCareCustomCodeSystem) pageDto.getList().get(0);

        assertTrue(pageDto != null);
        assertTrue(pageDto.getList() != null);
        assertTrue(pageDto.getList().size() == mockResults.size());
        assertTrue(firstPageDtoCodeSystem.getCodeId().equals(firstMockCodeSystem.getCodeId()));
        assertTrue(firstPageDtoCodeSystem.getCodeDescription().equals(firstMockCodeSystem.getCodeDescription()));
        assertTrue(firstPageDtoCodeSystem.getValueType().equals(firstMockCodeSystem.getValueType()));
    }
}