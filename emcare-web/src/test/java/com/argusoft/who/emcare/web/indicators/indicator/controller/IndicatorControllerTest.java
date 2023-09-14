package com.argusoft.who.emcare.web.indicators.indicator.controller;

import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorDto;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorFilterDto;
import com.argusoft.who.emcare.web.indicators.indicator.entity.Indicator;
import com.argusoft.who.emcare.web.indicators.indicator.mapper.IndicatorMapper;
import com.argusoft.who.emcare.web.indicators.indicator.service.IndicatorService;
import com.argusoft.who.emcare.web.menu.model.MenuConfig;
import com.vladsch.flexmark.util.collection.MapEntry;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {IndicatorController.class})
@RunWith(MockitoJUnitRunner.class)
class IndicatorControllerTest {
    @Mock
    IndicatorService indicatorService;

    @InjectMocks
    private IndicatorController indicatorController;

    ObjectMapper objectMapper = new ObjectMapper();

    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Nested
    class testAddNewCodeSystem {
        @BeforeEach
        void setUpAddNewCodeSystem() {
            when(indicatorService.addOrUpdateIndicator(any(IndicatorDto.class))).thenAnswer(i -> {
                IndicatorDto indicatorDto = i.getArgument(0);
                if (indicatorDto.getIndicatorId() == null) indicatorDto.setIndicatorId(404L);
                return ResponseEntity.ok().body(IndicatorMapper.getIndicator(indicatorDto));
            });
        }

        @Test
        void newIndicator() throws Exception {
            IndicatorDto mockIndicator = new IndicatorDto();
            mockIndicator.setAge("A");
            mockIndicator.setGender("G");
            mockIndicator.setQuery("Q");
            mockIndicator.setDescription("D");
            mockIndicator.setColourSchema("C");
            mockIndicator.setQueryConfigure(false);
            mockIndicator.setIndicatorName("N");
            mockIndicator.setIndicatorCode("C");
            mockIndicator.setNumeratorEquations(List.of());
            mockIndicator.setDenominatorEquations(List.of());


            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post("/api/indicator/add")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mockIndicator));

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(indicatorController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);

            Indicator actualIndicator = objectMapper.readValue(response, Indicator.class);
            assertNotNull(actualIndicator);
            assertEquals(404L, actualIndicator.getIndicatorId());
            assertEquals(mockIndicator.getDescription(), actualIndicator.getDescription());
            assertEquals(mockIndicator.getAge(), actualIndicator.getAge());
            assertEquals(mockIndicator.getGender(), actualIndicator.getGender());
            assertEquals(mockIndicator.getQuery(), actualIndicator.getQuery());
            assertEquals(mockIndicator.getQueryConfigure(), actualIndicator.getQueryConfigure());
            assertEquals(mockIndicator.getIndicatorName(), actualIndicator.getIndicatorName());
            assertEquals(mockIndicator.getIndicatorCode(), actualIndicator.getIndicatorCode());
        }

        @Test
        void updateIndicator() throws Exception {
            IndicatorDto mockIndicator = new IndicatorDto();
            mockIndicator.setIndicatorId(1L);
            mockIndicator.setAge("A");
            mockIndicator.setGender("G");
            mockIndicator.setQuery("Q");
            mockIndicator.setDescription("D");
            mockIndicator.setColourSchema("C");
            mockIndicator.setQueryConfigure(false);
            mockIndicator.setIndicatorName("N");
            mockIndicator.setIndicatorCode("C");
            mockIndicator.setNumeratorEquations(List.of());
            mockIndicator.setDenominatorEquations(List.of());

            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post("/api/indicator/add")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mockIndicator));

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(indicatorController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);

            Indicator actualIndicator = objectMapper.readValue(response, Indicator.class);
            assertNotNull(actualIndicator);
            assertEquals(mockIndicator.getIndicatorId(), actualIndicator.getIndicatorId());
            assertEquals(mockIndicator.getDescription(), actualIndicator.getDescription());
            assertEquals(mockIndicator.getAge(), actualIndicator.getAge());
            assertEquals(mockIndicator.getGender(), actualIndicator.getGender());
            assertEquals(mockIndicator.getQuery(), actualIndicator.getQuery());
            assertEquals(mockIndicator.getQueryConfigure(), actualIndicator.getQueryConfigure());
            assertEquals(mockIndicator.getIndicatorName(), actualIndicator.getIndicatorName());
            assertEquals(mockIndicator.getIndicatorCode(), actualIndicator.getIndicatorCode());
        }
    }

    @Test
    void testGetAllIndicator() throws Exception {
        when(indicatorService.getAllIndicatorData()).thenReturn(ResponseEntity.ok(List.of(new Indicator(), new Indicator())));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/indicator/all")
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(indicatorController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        List<Indicator> actualIndicator = objectMapper.readValue(response, new TypeReference<List<Indicator>>() {
        });
        assertNotNull(actualIndicator);
        assertEquals(2, actualIndicator.size());
    }

    @Nested
    class testGetIndicatorById {
        @Test
        void validID() throws Exception {
            when(indicatorService.getIndicatorById(1L)).thenReturn(ResponseEntity.ok(new Indicator()));

            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get("/api/indicator/1")
                    .accept(MediaType.APPLICATION_JSON);

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(indicatorController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);

            Indicator actualIndicator = objectMapper.readValue(response, Indicator.class);
            assertNotNull(actualIndicator);
        }

        @Test
        void validIDNotExisting() throws Exception {
            when(indicatorService.getIndicatorById(2L)).thenReturn(ResponseEntity.ok(null));

            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get("/api/indicator/2")
                    .accept(MediaType.APPLICATION_JSON);

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(indicatorController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);
            assertEquals("", response);
        }

        @Test
        void invalidID() throws Exception {
            when(indicatorService.getIndicatorById(404L)).thenReturn(ResponseEntity.ok(null));

            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get("/api/indicator/test")
                    .accept(MediaType.APPLICATION_JSON);

            MockMvcBuilders.standaloneSetup(indicatorController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
        }
    }

    @Nested
    class testGetIndicatorPage {
        @Test
        void page1NoSearch() throws Exception {
            PageDto mockPageDto = new PageDto();
            mockPageDto.setTotalCount(100L);
            mockPageDto.setList(List.of());
            when(indicatorService.getIndicatorDataPage(1, null)).thenReturn(mockPageDto);

            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get("/api/indicator/page")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("pageNo", "1");

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(indicatorController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);

            PageDto actualPageDto = objectMapper.readValue(response, PageDto.class);
            assertNotNull(actualPageDto);
            assertEquals(mockPageDto.getTotalCount(), actualPageDto.getTotalCount());
            assertEquals(mockPageDto.getList().size(), actualPageDto.getList().size());
        }

        @Test
        void page2WithSearch() throws Exception {
            PageDto mockPageDto = new PageDto();
            mockPageDto.setTotalCount(100L);
            mockPageDto.setList(List.of());
            when(indicatorService.getIndicatorDataPage(2, "test")).thenReturn(mockPageDto);

            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get("/api/indicator/page")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("pageNo", "2")
                    .param("search", "test");

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(indicatorController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);

            PageDto actualPageDto = objectMapper.readValue(response, PageDto.class);
            assertNotNull(actualPageDto);
            assertEquals(mockPageDto.getTotalCount(), actualPageDto.getTotalCount());
            assertEquals(mockPageDto.getList().size(), actualPageDto.getList().size());
        }

        @Test
        void invalidParams() throws Exception {
            PageDto mockPageDto = new PageDto();
            mockPageDto.setTotalCount(100L);
            mockPageDto.setList(List.of());
            when(indicatorService.getIndicatorDataPage(2, "test")).thenReturn(mockPageDto);

            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get("/api/indicator/page")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("pageNo", "test")
                    .param("search", "test");

            MockMvcBuilders.standaloneSetup(indicatorController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
        }
    }

    @Test
    void getIndicatorCompileValue() throws Exception {
        Map<String, Object> row1 = new HashMap<>();
        row1.put("A", "a1");
        row1.put("B", "b1");
        Map<String, Object> row2 = new HashMap<>();
        row2.put("A", "a2");
        row2.put("B", "b2");
        Map<String, Object> row3 = new HashMap<>();
        row3.put("A", "a3");
        row3.put("B", "b3");
        List<Map<String, Object>> mockResult = List.of(row1, row2, row3);
        when(indicatorService.getIndicatorsCompileValue(anyList())).thenReturn(ResponseEntity.ok(mockResult));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/indicator/compile/value")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(1L, 2L)));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(indicatorController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        List<Map<String, Object>> actualData = objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {
        });
        assertNotNull(actualData);
        assertEquals(mockResult.size(), actualData.size());
        assertEquals(mockResult.get(0).get("A"), actualData.get(0).get("A"));
        assertNotEquals(mockResult.get(2).get("B"), actualData.get(1).get("B"));
    }

    @Test
    void testGetIndicatorFilteredCompileValue() throws Exception {
        Map<String, Object> row1 = new HashMap<>();
        row1.put("A", "a1");
        row1.put("B", "b1");
        Map<String, Object> row2 = new HashMap<>();
        row2.put("A", "a2");
        row2.put("B", "b2");
        Map<String, Object> row3 = new HashMap<>();
        row3.put("A", "a3");
        row3.put("B", "b3");
        List<Map<String, Object>> mockResult = List.of(row1, row2, row3);

        Date mockDate = new Date();
        IndicatorFilterDto mockFilterDto = new IndicatorFilterDto();
        mockFilterDto.setAge("0-5");
        mockFilterDto.setGender("M");
        mockFilterDto.setFacilityIds(List.of("F1"));
        mockFilterDto.setStartDate(mockDate);
        mockFilterDto.setEndDate(mockDate);

        when(indicatorService.getIndicatorFilteredCompileValue(any(IndicatorFilterDto.class))).thenAnswer(i -> {
            IndicatorFilterDto filter = i.getArgument(0);
            if(!filter.getAge().equals("0-5") || !filter.getGender().equals("M") || !filter.getFacilityIds().get(0).equals("F1")) return ResponseEntity.ok(null);
            if(!filter.getStartDate().equals(mockDate) || !filter.getEndDate().equals(mockDate)) return ResponseEntity.ok(null);
            return ResponseEntity.ok(mockResult);
        });

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/indicator/filter/value")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockFilterDto));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(indicatorController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);
        assertNotEquals("", response);

        List<Map<String, Object>> actualData = objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {
        });
        assertNotNull(actualData);
        assertEquals(mockResult.size(), actualData.size());
        assertEquals(mockResult.get(0).get("A"), actualData.get(0).get("A"));
        assertNotEquals(mockResult.get(2).get("B"), actualData.get(1).get("B"));
    }
}