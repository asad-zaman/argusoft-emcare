package com.argusoft.who.emcare.web.indicators.codes.controller;

import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.indicators.codes.dto.CustomCodeRequestDto;
import com.argusoft.who.emcare.web.indicators.codes.entity.EmCareCustomCodeSystem;
import com.argusoft.who.emcare.web.indicators.codes.service.CustomCodeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class CustomCodeControllerTest {

    @Mock
    private CustomCodeService customCodeService;

    @InjectMocks
    private CustomCodeController customCodeController;

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
    void addNewCodeSystem() {
        CustomCodeRequestDto customCodeRequestDto = new CustomCodeRequestDto();

        customCodeRequestDto.setCodeId(1L);
        customCodeRequestDto.setCode("Emcare.B12.D1");
        customCodeRequestDto.setCodeDescription("High Body Temperature");
        customCodeRequestDto.setValueType("Boolean");
        customCodeRequestDto.setCondition(new String[]{"=Equal To"});
        customCodeRequestDto.setValue(new String[]{"true", "false"});

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Code system created successfully");
        when(customCodeService.createCustomCode(customCodeRequestDto)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = customCodeController.addNewCodeSystem(customCodeRequestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse.getBody(), response.getBody());
    }

    @Test
    void updateCodeSystem() {
        CustomCodeRequestDto customCodeRequestDto = new CustomCodeRequestDto();

        customCodeRequestDto.setCodeId(11230L);
        customCodeRequestDto.setCode("Emcare.D5.F18");
        customCodeRequestDto.setCodeDescription("Low Blood Pressure");
        customCodeRequestDto.setValueType("Numeric");
        customCodeRequestDto.setCondition(new String[]{"<Less Than", ">Greater Than"});
        customCodeRequestDto.setValue(new String[]{"50", "100"});

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Code system updated successfully");
        when(customCodeService.updateCustomCodeSystem(customCodeRequestDto)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = customCodeController.updateCodeSystem(customCodeRequestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse.getBody(), response.getBody());
    }

    @Test
    void getAllCustomCode() {
        List<CustomCodeRequestDto> customCodeList = new ArrayList<>();

        CustomCodeRequestDto customCode1 = new CustomCodeRequestDto();
        customCode1.setCodeId(17892L);
        customCode1.setCode("Emcare.D5.F18");
        customCode1.setCodeDescription("Low Blood Pressure");
        customCode1.setValueType("Numeric");
        customCode1.setCondition(new String[]{"<Less Than", ">Greater Than"});
        customCode1.setValue(new String[]{"50", "100"});
        customCodeList.add(customCode1);

        CustomCodeRequestDto customCode2 = new CustomCodeRequestDto();
        customCode2.setCodeId(22013L);
        customCode2.setCode("Emcare.D8.G22");
        customCode2.setCodeDescription("High Cholesterol");
        customCode2.setValueType("Numeric");
        customCode2.setCondition(new String[]{">Greater Than"});
        customCode2.setValue(new String[]{"200"});
        customCodeList.add(customCode2);

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("List of all custom codes");
        when(customCodeService.getAllCustomCodeSystem()).thenReturn(expectedResponse);

        ResponseEntity<Object> response = customCodeController.getAllCustomCode();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse.getBody(), response.getBody());
    }

    @Test
    void getCustomCodeById() {
        Long codeId = 12345L;

        CustomCodeRequestDto customCode = new CustomCodeRequestDto();
        customCode.setCodeId(codeId);
        customCode.setCode("Emcare.AB12.CD34");
        customCode.setCodeDescription("Is Body Temperature?");
        customCode.setValueType("Boolean");
        customCode.setCondition(new String[]{"=Equal To", ">Greater Than", "<Less Than"});
        customCode.setValue(new String[]{"true", "false"});

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Custom code with ID: " + codeId);
        when(customCodeService.getCustomCodeById(codeId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = customCodeController.getCustomCodeById(codeId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse.getBody(), response.getBody());
    }

    @Test
    void getCustomCodePage() {
        Integer pageNo = 0;
        String searchString = "EmCare.D1.JP28";

        List<CustomCodeRequestDto> customCodeList = new ArrayList<>();

        CustomCodeRequestDto customCode1 = new CustomCodeRequestDto();
        customCode1.setCodeId(17688L);
        customCode1.setCode("EmCare.D1.JP28");
        customCode1.setCodeDescription("High Body Temperature");
        customCode1.setValueType("Boolean");
        customCode1.setCondition(new String[]{"=Equal To"});
        customCode1.setValue(new String[]{"true", "false"});

        CustomCodeRequestDto customCode2 = new CustomCodeRequestDto();
        customCode2.setCodeId(2123L);
        customCode2.setCode("EmCare.AB12.CD34");
        customCode2.setCodeDescription("Custom Code for Temperature");
        customCode2.setValueType("Boolean");
        customCode2.setCondition(new String[]{"=Equal To", ">Greater Than"});
        customCode2.setValue(new String[]{"true", "false"});

        customCodeList.add(customCode1);
        customCodeList.add(customCode2);

        PageDto expectedPageDto = new PageDto();
        expectedPageDto.setList(customCodeList);
        expectedPageDto.setTotalCount((long) customCodeList.size());

        when(customCodeService.getCustomCodeWithPagination(pageNo, searchString)).thenReturn(expectedPageDto);

        PageDto response = customCodeController.getCustomCodePage(pageNo, searchString);

        assertNotNull(response);
        assertEquals(expectedPageDto.getList(), response.getList());
        assertEquals(expectedPageDto.getTotalCount(), response.getTotalCount());
    }
}