package com.argusoft.who.emcare.web.indicators.indicator.service;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.fhir.dao.ObservationCustomResourceRepository;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.service.LocationResourceService;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorDenominatorEquationDto;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorDto;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorFilterDto;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorNumeratorEquationDto;
import com.argusoft.who.emcare.web.indicators.indicator.entity.Indicator;
import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorDenominatorEquation;
import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorNumeratorEquation;
import com.argusoft.who.emcare.web.indicators.indicator.query_builder.IndicatorQueryBuilder;
import com.argusoft.who.emcare.web.indicators.indicator.repository.IndicatorDenominatorEquationRepository;
import com.argusoft.who.emcare.web.indicators.indicator.repository.IndicatorNumeratorEquationRepository;
import com.argusoft.who.emcare.web.indicators.indicator.repository.IndicatorRepository;
import com.argusoft.who.emcare.web.indicators.indicator.service.impl.IndicatorServiceImpl;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IndicatorServiceTest {

    @Mock
    IndicatorRepository indicatorRepository;

    @Mock
    IndicatorNumeratorEquationRepository indicatorNumeratorEquationRepository;

    @Mock
    IndicatorDenominatorEquationRepository indicatorDenominatorEquationRepository;

    @Mock
    ObservationCustomResourceRepository observationCustomResourceRepository;

    @Mock
    IndicatorQueryBuilder indicatorQueryBuilder;

    @Mock
    UserService userService;

    @Mock
    LocationResourceService locationResourceService;

    @InjectMocks
    IndicatorServiceImpl indicatorService;


    AutoCloseable autoCloseable;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Nested
    class testAddOrUpdateIndicator {
        @BeforeEach
        void setUpTestAddOrUpdateIndicator() {
            when(indicatorRepository.save(any(Indicator.class))).thenAnswer(i -> {
                Indicator indicator = i.getArgument(0);
                if (indicator.getIndicatorId() == null) indicator.setIndicatorId(404L);
                return indicator;
            });
        }

        @Test
        void newIndicator() {
            IndicatorDto mockIndicator = new IndicatorDto();
            mockIndicator.setIndicatorId(null);
            mockIndicator.setNumeratorEquations(List.of(new IndicatorNumeratorEquationDto()));
            mockIndicator.setDenominatorEquations(List.of(new IndicatorDenominatorEquationDto()));
            ResponseEntity<Object> actualResponse = indicatorService.addOrUpdateIndicator(mockIndicator);
            Indicator actualIndicator = (Indicator) actualResponse.getBody();
            assertNotNull(actualIndicator);
            assertEquals(404L, actualIndicator.getIndicatorId());
            verify(indicatorNumeratorEquationRepository, times(1)).saveAll(any());
            verify(indicatorDenominatorEquationRepository, times(1)).saveAll(any());
        }

        @Test
        void existingIndicator() {
            IndicatorDto mockIndicator = new IndicatorDto();
            mockIndicator.setIndicatorId(1L);
            mockIndicator.setNumeratorEquations(List.of(new IndicatorNumeratorEquationDto()));
            mockIndicator.setDenominatorEquations(List.of(new IndicatorDenominatorEquationDto()));
            ResponseEntity<Object> actualResponse = indicatorService.addOrUpdateIndicator(mockIndicator);
            Indicator actualIndicator = (Indicator) actualResponse.getBody();
            assertNotNull(actualIndicator);
            assertEquals(mockIndicator.getIndicatorId(), actualIndicator.getIndicatorId());
            verify(indicatorNumeratorEquationRepository, times(1)).deleteByNumeratorIndicator(mockIndicator.getIndicatorId());
            verify(indicatorDenominatorEquationRepository, times(1)).deleteByDenominatorIndicator(mockIndicator.getIndicatorId());
            verify(indicatorNumeratorEquationRepository, times(1)).saveAll(any());
            verify(indicatorDenominatorEquationRepository, times(1)).saveAll(any());
        }
    }

    @Test
    void testGetAllIndicatorData() {
        Indicator i1 = new Indicator();
        i1.setIndicatorId(1L);
        i1.setAge("0-8 Months");
        i1.setIndicatorName("Indicator 1");
        i1.setColourSchema("Color 1");
        i1.setQueryConfigure(false);
        i1.setNumeratorIndicatorEquation("Numerator");
        i1.setDenominatorIndicatorEquation("Denominator");

        Indicator i2 = new Indicator();
        i2.setIndicatorId(2L);
        i2.setAge("9-18 Months");
        i2.setIndicatorName("Indicator 2");
        i2.setColourSchema("Color 2");
        i2.setQueryConfigure(true);
        i2.setNumeratorEquation(List.of(new IndicatorNumeratorEquation()));
        i2.setDenominatorEquation(List.of(new IndicatorDenominatorEquation()));

        when(indicatorRepository.findAll()).thenReturn(List.of(i1, i2));

        ResponseEntity<Object> actualResponse = indicatorService.getAllIndicatorData();
        assertNotNull(actualResponse);

        List<Indicator> actualData = (List<Indicator>) actualResponse.getBody();
        assertNotNull(actualData);
        assertEquals(2, actualData.size());
    }

    @Nested
    class testGetIndicatorById {
        Indicator mockIndicator;

        @BeforeEach
        void setUpGetIndicatorById() {
            when(indicatorRepository.findById(anyLong()))
                    .thenAnswer(i -> {
                        mockIndicator = new Indicator();
                        mockIndicator.setIndicatorId(1L);
                        mockIndicator.setIndicatorName("IndicatorRRR");
                        mockIndicator.setAge("18yrs+");
                        mockIndicator.setGender("M");
                        Long id = i.getArgument(0);
                        if (id == 1) return Optional.of(mockIndicator);
                        else return Optional.empty();
                    });
        }

        @Test
        void existingId() {
            ResponseEntity<Object> actualResponse = indicatorService.getIndicatorById(1L);
            assertNotNull(actualResponse);
            Indicator actualIndicator = ((Optional<Indicator>) actualResponse.getBody()).get();
            assertNotNull(actualIndicator);
            assertEquals(mockIndicator.getIndicatorId(), actualIndicator.getIndicatorId());
            assertEquals(mockIndicator.getIndicatorName(), actualIndicator.getIndicatorName());
            assertEquals(mockIndicator.getAge(), actualIndicator.getAge());
            assertEquals(mockIndicator.getGender(), actualIndicator.getGender());
        }

        @Test
        void nonExistingId() {
            ResponseEntity<Object> actualResponse = indicatorService.getIndicatorById(2L);
            assertNotNull(actualResponse);
            assertFalse(((Optional<Indicator>) actualResponse.getBody()).isPresent());
        }
    }

    @Nested
    class testGetIndicatorDataPage {
        @BeforeEach
        void setUpGetIndicatorDataPage() throws IOException {
            List<Indicator> mockIndicators = getMockIndicators();

            when(indicatorRepository.count()).thenReturn((long) mockIndicators.size());

            when(indicatorRepository.findAll(any(Pageable.class))).thenAnswer(i -> {
                Pageable p = i.getArgument(0);
                int startIndex = (p.getPageNumber() - 1) * p.getPageSize();
                int endIndex = startIndex + p.getPageSize();
                return new PageImpl<Indicator>(startIndex >= mockIndicators.size() ?
                        List.of()
                        :
                        mockIndicators.subList(startIndex, Math.min(mockIndicators.size(), endIndex)));
            });

            when(
                    indicatorRepository.findByIndicatorCodeContainingIgnoreCaseOrIndicatorNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(any(), any(), any())
            ).thenAnswer(i -> {
                String s1 = ((String) i.getArgument(0)).toLowerCase();
                String s2 = ((String) i.getArgument(1)).toLowerCase();
                String s3 = ((String) i.getArgument(2)).toLowerCase();
                return mockIndicators.stream().filter(indicator -> {
                    if (indicator.getIndicatorCode() != null && indicator.getIndicatorCode().toLowerCase().contains(s1))
                        return true;
                    if (indicator.getIndicatorName() != null && indicator.getIndicatorName().toLowerCase().contains(s2))
                        return true;
                    return indicator.getDescription() != null && indicator.getDescription().toLowerCase().contains(s3);
                }).collect(Collectors.toList());
            });

            when(
                    indicatorRepository.findByIndicatorCodeContainingIgnoreCaseOrIndicatorNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(any(), any(), any(), any())
            ).thenAnswer(i -> {
                String s1 = ((String) i.getArgument(0)).toLowerCase();
                String s2 = ((String) i.getArgument(1)).toLowerCase();
                String s3 = ((String) i.getArgument(2)).toLowerCase();
                Pageable p = i.getArgument(3);
                int startIndex = (p.getPageNumber() - 1) * p.getPageSize();
                int endIndex = startIndex + p.getPageSize();

                List<Indicator> filteredList = mockIndicators.stream().filter(indicator -> {
                    if (indicator.getIndicatorCode() != null && indicator.getIndicatorCode().toLowerCase().contains(s1))
                        return true;
                    if (indicator.getIndicatorName() != null && indicator.getIndicatorName().toLowerCase().contains(s2))
                        return true;
                    return indicator.getDescription() != null && indicator.getDescription().toLowerCase().contains(s3);
                }).collect(Collectors.toList());
                return new PageImpl<Indicator>(startIndex >= filteredList.size() ?
                        List.of()
                        :
                        filteredList.subList(startIndex, Math.min(filteredList.size(), endIndex)));
            });
        }

        @Test
        void validPageNoSearch() {
            PageDto actualPage = indicatorService.getIndicatorDataPage(1, null);
            assertNotNull(actualPage);
            assertEquals(12, actualPage.getTotalCount());
            assertEquals(10, actualPage.getList().size());
        }

        @Test
        void validPage2NoSearch() {
            PageDto actualPage = indicatorService.getIndicatorDataPage(2, null);
            assertNotNull(actualPage);
            assertEquals(12, actualPage.getTotalCount());
            assertEquals(2, actualPage.getList().size());
        }

        @Test
        void validPageAndSearch() {
            PageDto actualPage = indicatorService.getIndicatorDataPage(1, "Breathing");
            assertNotNull(actualPage);
            assertEquals(1, actualPage.getTotalCount());
            assertEquals(1, actualPage.getList().size());
        }

        @Test
        void validPage2AndSearch() {
            PageDto actualPage = indicatorService.getIndicatorDataPage(3, "Breathing");
            assertNotNull(actualPage);
            assertEquals(1, actualPage.getTotalCount());
            assertEquals(0, actualPage.getList().size());
        }
    }

    @Nested
    class testGetIndicatorsCompileValue {
        @Test
        void withCurrentUserFacility() {
            Indicator mockIndicator1 = new Indicator();
            mockIndicator1.setIndicatorId(1L);
            mockIndicator1.setAge("I1A1");
            mockIndicator1.setGender("I1G1");
            mockIndicator1.setDisplayType(CommonConstant.INDICATOR_DISPLAY_TYPE_COUNT);
            mockIndicator1.setQueryConfigure(true);

            Indicator mockIndicator4 = new Indicator();
            mockIndicator4.setIndicatorId(4L);
            mockIndicator4.setAge(null);
            mockIndicator4.setGender(null);
            mockIndicator4.setDisplayType(CommonConstant.INDICATOR_DISPLAY_TYPE_COUNT);
            mockIndicator4.setQueryConfigure(true);

            Indicator mockIndicator3 = new Indicator();
            mockIndicator3.setIndicatorId(3L);
            mockIndicator3.setAge("I3A3");
            mockIndicator3.setGender("I3G3");
            mockIndicator3.setDisplayType(CommonConstant.INDICATOR_DISPLAY_TYPE_COUNT);
            mockIndicator3.setQueryConfigure(false);
            IndicatorNumeratorEquation mi3NEq = new IndicatorNumeratorEquation();
            mi3NEq.setEqIdentifier("nvar");
            mockIndicator3.setNumeratorEquation(List.of(mi3NEq));
            mockIndicator3.setNumeratorIndicatorEquation("3 + nvar");
            IndicatorDenominatorEquation mi3DEq = new IndicatorDenominatorEquation();
            mi3DEq.setEqIdentifier("dvar");
            mi3DEq.setCode(CommonConstant.ALL_CODE);
            mockIndicator3.setDenominatorEquation(List.of(mi3DEq));
            mockIndicator3.setDenominatorIndicatorEquation("1 + dvar");

            Indicator mockIndicator5 = new Indicator();
            mockIndicator5.setIndicatorId(5L);
            mockIndicator5.setAge(null);
            mockIndicator5.setGender(null);
            mockIndicator5.setDisplayType(CommonConstant.INDICATOR_DISPLAY_TYPE_COUNT);
            mockIndicator5.setQueryConfigure(false);
            IndicatorNumeratorEquation mi5NEq = new IndicatorNumeratorEquation();
            mi5NEq.setEqIdentifier("nvar");
            mi5NEq.setCode("OTHER_CODE");
            mockIndicator5.setNumeratorEquation(List.of(mi5NEq));
            mockIndicator5.setNumeratorIndicatorEquation("5 + nvar");
            mockIndicator5.setDenominatorEquation(List.of());
            mockIndicator5.setDenominatorIndicatorEquation("1");

            Indicator mockIndicator2 = new Indicator();
            mockIndicator2.setIndicatorId(2L);
            mockIndicator2.setAge("I2A2");
            mockIndicator2.setGender("I2G2");
            mockIndicator2.setDisplayType("IGNORE");

            List<Indicator> mockIndicatorList = List.of(mockIndicator1, mockIndicator2, mockIndicator3, mockIndicator4, mockIndicator5);

            when(indicatorRepository.findAll()).thenReturn(mockIndicatorList);

            when(userService.getCurrentUserFacility()).thenReturn(List.of("F1", "F2"));

            when(indicatorQueryBuilder.changeQueryBasedOnFilterValueReplace(anyString(), any(Indicator.class), any(IndicatorFilterDto.class)))
                    .thenAnswer(i -> {
                        String p1 = i.getArgument(0);
                        Indicator p2 = i.getArgument(1);
                        IndicatorFilterDto p3 = i.getArgument(2);
                        return p1 + ":" + p2.getIndicatorId() + ":" + (p3.getAge() != null ? p3.getAge() : "") + ":" + (p3.getGender() != null ? p3.getGender(): "");
                    });


            when(
                    indicatorQueryBuilder
                            .getQueryForIndicatorNumeratorEquation(
                                    any(IndicatorNumeratorEquation.class),
                                    anyString(),
                                    any(Indicator.class),
                                    any(IndicatorFilterDto.class)
                            )
            ).thenAnswer(i -> {
                IndicatorNumeratorEquation p1 = i.getArgument(0);
                String p2 = i.getArgument(1);
//            Indicator p3 = i.getArgument(2);
                IndicatorFilterDto p4 = i.getArgument(3);
                return p1.getEqIdentifier() + ":" + p2 + ":" + (p4.getAge() != null ? p4.getAge() : "") + ":" + (p4.getGender() != null ? p4.getGender() : "");
            });

            when(
                    indicatorQueryBuilder
                            .getQueryForIndicatorDenominatorEquation(
                                    any(IndicatorDenominatorEquation.class),
                                    anyString(),
                                    any(Indicator.class),
                                    any(IndicatorFilterDto.class)
                            )
            ).thenAnswer(i -> {
                IndicatorDenominatorEquation p1 = i.getArgument(0);
                String p2 = i.getArgument(1);
//            Indicator p3 = i.getArgument(2);
                IndicatorFilterDto p4 = i.getArgument(3);
                return p1.getEqIdentifier() + ":" + p2 + ":" + (p4.getAge() != null ? p4.getAge() : "") + ":" + (p4.getGender() != null ? p4.getGender() : "");
            });

            Map<String, List<Map<String, Object>>> observationCustomResourceMap = new HashMap<>();
            List<Map<String, Object>> m1 = mock(List.class); when(m1.size()).thenReturn(11);
            List<Map<String, Object>> m2 = mock(List.class); when(m2.size()).thenReturn(12);
            List<Map<String, Object>> m3 = mock(List.class); when(m3.size()).thenReturn(7);
            List<Map<String, Object>> m4 = mock(List.class); when(m4.size()).thenReturn(12);
            List<Map<String, Object>> m5 = mock(List.class); when(m5.size()).thenReturn(10);
            List<Map<String, Object>> m6 = mock(List.class); when(m6.size()).thenReturn(19);
            List<Map<String, Object>> m7 = mock(List.class); when(m7.size()).thenReturn(15);
            List<Map<String, Object>> m8 = mock(List.class); when(m8.size()).thenReturn(9);
            List<Map<String, Object>> m9 = mock(List.class); when(m9.size()).thenReturn(11);

            observationCustomResourceMap.put("'F1','F2':1:I1A1:I1G1", m1);
            observationCustomResourceMap.put("'F1','F2':2:I2A2:I2G2", m2);
            observationCustomResourceMap.put("'F1','F2':3:I3A3:I3G3", m3);
            observationCustomResourceMap.put("'F1','F2':4::", m4);
            observationCustomResourceMap.put("'F1','F2':5::", m5);
            observationCustomResourceMap.put("nvar:F1','F2:I3A3:I3G3", m6);
            observationCustomResourceMap.put("dvar:F1','F2:I3A3:I3G3", m7);
            observationCustomResourceMap.put("nvar:F1','F2::", m8);
            observationCustomResourceMap.put("dvar:F1','F2::", m9);

            when(observationCustomResourceRepository.findByPublished(anyString())).thenAnswer(
                    i -> observationCustomResourceMap.get((String) i.getArgument(0))
            );

            ResponseEntity<Object> actualResponse = indicatorService.getIndicatorsCompileValue(List.of(1L, 2L));
            assertNotNull(actualResponse);
            List<Map<String, Object>> actualData = (List<Map<String, Object>>) actualResponse.getBody();
            assertNotNull(actualData);
            assertEquals(4, actualData.size());

            assertEquals(5L, actualData.get(0).get("indicatorId"));
            assertEquals("Count", actualData.get(0).get("indicatorType"));
            assertEquals("1400.0", actualData.get(0).get("indicatorValue"));

            assertEquals(3L, actualData.get(1).get("indicatorId"));
            assertEquals("Count", actualData.get(1).get("indicatorType"));
            assertEquals("I3G3", actualData.get(1).get("gender"));
            assertEquals("I3A3", actualData.get(1).get("age"));
            assertEquals("137.5", actualData.get(1).get("indicatorValue"));

            assertEquals(4L, actualData.get(2).get("indicatorId"));
            assertEquals("Count", actualData.get(2).get("indicatorType"));
            assertNull(actualData.get(2).get("gender"));
            assertNull(actualData.get(2).get("age"));
            assertEquals("12.0", actualData.get(2).get("indicatorValue"));

            assertEquals(1L, actualData.get(3).get("indicatorId"));
            assertEquals("Count", actualData.get(3).get("indicatorType"));
            assertEquals("I1G1", actualData.get(3).get("gender"));
            assertEquals("I1A1", actualData.get(3).get("age"));
            assertEquals("11.0", actualData.get(3).get("indicatorValue"));
        }

        @Test
        void withNoCurrentUserFacility() {
            Indicator mockIndicator1 = new Indicator();
            mockIndicator1.setIndicatorId(1L);
            mockIndicator1.setAge("I1A1");
            mockIndicator1.setGender("I1G1");
            mockIndicator1.setDisplayType(CommonConstant.INDICATOR_DISPLAY_TYPE_COUNT);
            mockIndicator1.setQueryConfigure(true);

            Indicator mockIndicator4 = new Indicator();
            mockIndicator4.setIndicatorId(4L);
            mockIndicator4.setAge(null);
            mockIndicator4.setGender(null);
            mockIndicator4.setDisplayType(CommonConstant.INDICATOR_DISPLAY_TYPE_COUNT);
            mockIndicator4.setQueryConfigure(true);

            Indicator mockIndicator3 = new Indicator();
            mockIndicator3.setIndicatorId(3L);
            mockIndicator3.setAge("I3A3");
            mockIndicator3.setGender("I3G3");
            mockIndicator3.setDisplayType(CommonConstant.INDICATOR_DISPLAY_TYPE_COUNT);
            mockIndicator3.setQueryConfigure(false);
            IndicatorNumeratorEquation mi3NEq = new IndicatorNumeratorEquation();
            mi3NEq.setEqIdentifier("nvar");
            mockIndicator3.setNumeratorEquation(List.of(mi3NEq));
            mockIndicator3.setNumeratorIndicatorEquation("3 + nvar");
            IndicatorDenominatorEquation mi3DEq = new IndicatorDenominatorEquation();
            mi3DEq.setEqIdentifier("dvar");
            mi3DEq.setCode(CommonConstant.ALL_CODE);
            mockIndicator3.setDenominatorEquation(List.of(mi3DEq));
            mockIndicator3.setDenominatorIndicatorEquation("1 + dvar");

            Indicator mockIndicator5 = new Indicator();
            mockIndicator5.setIndicatorId(5L);
            mockIndicator5.setAge(null);
            mockIndicator5.setGender(null);
            mockIndicator5.setDisplayType(CommonConstant.INDICATOR_DISPLAY_TYPE_COUNT);
            mockIndicator5.setQueryConfigure(false);
            IndicatorNumeratorEquation mi5NEq = new IndicatorNumeratorEquation();
            mi5NEq.setEqIdentifier("nvar");
            mi5NEq.setCode("OTHER_CODE");
            mockIndicator5.setNumeratorEquation(List.of(mi5NEq));
            mockIndicator5.setNumeratorIndicatorEquation("5 + nvar");
            mockIndicator5.setDenominatorEquation(List.of());
            mockIndicator5.setDenominatorIndicatorEquation("1");

            Indicator mockIndicator2 = new Indicator();
            mockIndicator2.setIndicatorId(2L);
            mockIndicator2.setAge("I2A2");
            mockIndicator2.setGender("I2G2");
            mockIndicator2.setDisplayType("IGNORE");

            List<Indicator> mockIndicatorList = List.of(mockIndicator1, mockIndicator2, mockIndicator3, mockIndicator4, mockIndicator5);

            when(indicatorRepository.findAll()).thenReturn(mockIndicatorList);

            when(userService.getCurrentUserFacility()).thenReturn(List.of());

            FacilityDto f1 = new FacilityDto(); f1.setFacilityId("F1");
            FacilityDto f2 = new FacilityDto(); f2.setFacilityId("F2");
            when(locationResourceService.getActiveFacility()).thenReturn(List.of(f1, f2));

            when(indicatorQueryBuilder.changeQueryBasedOnFilterValueReplace(anyString(), any(Indicator.class), any(IndicatorFilterDto.class)))
                    .thenAnswer(i -> {
                        String p1 = i.getArgument(0);
                        Indicator p2 = i.getArgument(1);
                        IndicatorFilterDto p3 = i.getArgument(2);
                        return p1 + ":" + p2.getIndicatorId() + ":" + (p3.getAge() != null ? p3.getAge() : "") + ":" + (p3.getGender() != null ? p3.getGender(): "");
                    });


            when(
                    indicatorQueryBuilder
                            .getQueryForIndicatorNumeratorEquation(
                                    any(IndicatorNumeratorEquation.class),
                                    anyString(),
                                    any(Indicator.class),
                                    any(IndicatorFilterDto.class)
                            )
            ).thenAnswer(i -> {
                IndicatorNumeratorEquation p1 = i.getArgument(0);
                String p2 = i.getArgument(1);
//            Indicator p3 = i.getArgument(2);
                IndicatorFilterDto p4 = i.getArgument(3);
                return p1.getEqIdentifier() + ":" + p2 + ":" + (p4.getAge() != null ? p4.getAge() : "") + ":" + (p4.getGender() != null ? p4.getGender() : "");
            });

            when(
                    indicatorQueryBuilder
                            .getQueryForIndicatorDenominatorEquation(
                                    any(IndicatorDenominatorEquation.class),
                                    anyString(),
                                    any(Indicator.class),
                                    any(IndicatorFilterDto.class)
                            )
            ).thenAnswer(i -> {
                IndicatorDenominatorEquation p1 = i.getArgument(0);
                String p2 = i.getArgument(1);
//            Indicator p3 = i.getArgument(2);
                IndicatorFilterDto p4 = i.getArgument(3);
                return p1.getEqIdentifier() + ":" + p2 + ":" + (p4.getAge() != null ? p4.getAge() : "") + ":" + (p4.getGender() != null ? p4.getGender() : "");
            });

            Map<String, List<Map<String, Object>>> observationCustomResourceMap = new HashMap<>();
            List<Map<String, Object>> m1 = mock(List.class); when(m1.size()).thenReturn(11);
            List<Map<String, Object>> m2 = mock(List.class); when(m2.size()).thenReturn(12);
            List<Map<String, Object>> m3 = mock(List.class); when(m3.size()).thenReturn(7);
            List<Map<String, Object>> m4 = mock(List.class); when(m4.size()).thenReturn(12);
            List<Map<String, Object>> m5 = mock(List.class); when(m5.size()).thenReturn(10);
            List<Map<String, Object>> m6 = mock(List.class); when(m6.size()).thenReturn(19);
            List<Map<String, Object>> m7 = mock(List.class); when(m7.size()).thenReturn(15);
            List<Map<String, Object>> m8 = mock(List.class); when(m8.size()).thenReturn(9);
            List<Map<String, Object>> m9 = mock(List.class); when(m9.size()).thenReturn(11);

            observationCustomResourceMap.put("'F1','F2':1:I1A1:I1G1", m1);
            observationCustomResourceMap.put("'F1','F2':2:I2A2:I2G2", m2);
            observationCustomResourceMap.put("'F1','F2':3:I3A3:I3G3", m3);
            observationCustomResourceMap.put("'F1','F2':4::", m4);
            observationCustomResourceMap.put("'F1','F2':5::", m5);
            observationCustomResourceMap.put("nvar:F1','F2:I3A3:I3G3", m6);
            observationCustomResourceMap.put("dvar:F1','F2:I3A3:I3G3", m7);
            observationCustomResourceMap.put("nvar:F1','F2::", m8);
            observationCustomResourceMap.put("dvar:F1','F2::", m9);

            when(observationCustomResourceRepository.findByPublished(anyString())).thenAnswer(
                    i -> observationCustomResourceMap.get((String) i.getArgument(0))
            );

            ResponseEntity<Object> actualResponse = indicatorService.getIndicatorsCompileValue(List.of(1L, 2L));
            assertNotNull(actualResponse);
            List<Map<String, Object>> actualData = (List<Map<String, Object>>) actualResponse.getBody();
            assertNotNull(actualData);
            assertEquals(4, actualData.size());

            assertEquals(5L, actualData.get(0).get("indicatorId"));
            assertEquals("Count", actualData.get(0).get("indicatorType"));
            assertEquals("1400.0", actualData.get(0).get("indicatorValue"));

            assertEquals(3L, actualData.get(1).get("indicatorId"));
            assertEquals("Count", actualData.get(1).get("indicatorType"));
            assertEquals("I3G3", actualData.get(1).get("gender"));
            assertEquals("I3A3", actualData.get(1).get("age"));
            assertEquals("137.5", actualData.get(1).get("indicatorValue"));

            assertEquals(4L, actualData.get(2).get("indicatorId"));
            assertEquals("Count", actualData.get(2).get("indicatorType"));
            assertNull(actualData.get(2).get("gender"));
            assertNull(actualData.get(2).get("age"));
            assertEquals("12.0", actualData.get(2).get("indicatorValue"));

            assertEquals(1L, actualData.get(3).get("indicatorId"));
            assertEquals("Count", actualData.get(3).get("indicatorType"));
            assertEquals("I1G1", actualData.get(3).get("gender"));
            assertEquals("I1A1", actualData.get(3).get("age"));
            assertEquals("11.0", actualData.get(3).get("indicatorValue"));
        }
    }

    @Nested
    class testGetIndicatorFilteredCompileValue {
        @Test
        void existingIdComputed() {
            // With Current User Facility
            Date mockDate = new Date();

            when(userService.getCurrentUserFacility()).thenReturn(List.of("F1", "F2"));

            when(indicatorQueryBuilder.changeQueryBasedOnFilterValueReplace(anyString(), any(Indicator.class), any(IndicatorFilterDto.class)))
                    .thenAnswer(i -> {
                        String p1 = i.getArgument(0);
                        Indicator p2 = i.getArgument(1);
                        IndicatorFilterDto p3 = i.getArgument(2);
                        return p1 + ":" +
                                p2.getIndicatorId() + ":" +
                                (p3.getAge() != null ? p3.getAge() : "") + ":" +
                                (p3.getGender() != null ? p3.getGender(): "") + ":" +
                                (p3.getStartDate() != null ? p3.getStartDate() : "") + ":" +
                                (p3.getEndDate() != null ? p3.getEndDate() : "");
                    });


            when(
                    indicatorQueryBuilder
                            .getQueryForIndicatorNumeratorEquation(
                                    any(IndicatorNumeratorEquation.class),
                                    anyString(),
                                    any(Indicator.class),
                                    any(IndicatorFilterDto.class)
                            )
            ).thenAnswer(i -> {
                IndicatorNumeratorEquation p1 = i.getArgument(0);
                String p2 = i.getArgument(1);
//            Indicator p3 = i.getArgument(2);
                IndicatorFilterDto p4 = i.getArgument(3);
                return p1.getEqIdentifier() + ":" +
                        p2 + ":" + (p4.getAge() != null ? p4.getAge() : "") + ":" +
                        (p4.getGender() != null ? p4.getGender() : "") + ":" +
                        (p4.getStartDate() != null ? p4.getStartDate() : "") + ":" +
                        (p4.getEndDate() != null ? p4.getEndDate() : "");
            });

            when(
                    indicatorQueryBuilder
                            .getQueryForIndicatorDenominatorEquation(
                                    any(IndicatorDenominatorEquation.class),
                                    anyString(),
                                    any(Indicator.class),
                                    any(IndicatorFilterDto.class)
                            )
            ).thenAnswer(i -> {
                IndicatorDenominatorEquation p1 = i.getArgument(0);
                String p2 = i.getArgument(1);
//            Indicator p3 = i.getArgument(2);
                IndicatorFilterDto p4 = i.getArgument(3);
                return p1.getEqIdentifier() + ":" +
                        p2 + ":" + (p4.getAge() != null ? p4.getAge() : "") + ":" +
                        (p4.getGender() != null ? p4.getGender() : "") + ":" +
                        (p4.getStartDate() != null ? p4.getStartDate() : "") + ":" +
                        (p4.getEndDate() != null ? p4.getEndDate() : "");
            });

            Map<String, List<Map<String, Object>>> observationCustomResourceMap = new HashMap<>();
            List<Map<String, Object>> m1 = mock(List.class); when(m1.size()).thenReturn(11);
            List<Map<String, Object>> m2 = mock(List.class); when(m2.size()).thenReturn(12);
            List<Map<String, Object>> m3 = mock(List.class); when(m3.size()).thenReturn(7);
            List<Map<String, Object>> m4 = mock(List.class); when(m4.size()).thenReturn(12);
            List<Map<String, Object>> m5 = mock(List.class); when(m5.size()).thenReturn(10);
            List<Map<String, Object>> m6 = mock(List.class); when(m6.size()).thenReturn(19);
            List<Map<String, Object>> m7 = mock(List.class); when(m7.size()).thenReturn(15);
            List<Map<String, Object>> m8 = mock(List.class); when(m8.size()).thenReturn(9);
            List<Map<String, Object>> m9 = mock(List.class); when(m9.size()).thenReturn(11);

            observationCustomResourceMap.put("'F1':1:I1A1:I1G1:" + mockDate + ":" + mockDate, m1);
            observationCustomResourceMap.put("'F1':2:I2A2:I2G2:" + mockDate + ":" + mockDate, m2);
            observationCustomResourceMap.put("'F1':3:I3A3:I3G3:" + mockDate + ":" + mockDate, m3);
            observationCustomResourceMap.put("'F1':4:::" + mockDate + ":" + mockDate, m4);
            observationCustomResourceMap.put("'F1':5:::" + mockDate + ":" + mockDate, m5);
            observationCustomResourceMap.put("nvar:F1:0-5:Male:" + mockDate + ":" + mockDate, m6);
            observationCustomResourceMap.put("dvar:F1:0-5:Male:" + mockDate + ":" + mockDate, m7);
            observationCustomResourceMap.put("nvar:F1',':::" + mockDate + ":" + mockDate, m8);
            observationCustomResourceMap.put("dvar:F1',':::" + mockDate + ":" + mockDate, m9);

            when(observationCustomResourceRepository.findByPublished(anyString())).thenAnswer(
                    i -> observationCustomResourceMap.get((String) i.getArgument(0))
            );

            IndicatorFilterDto mockFilterDto = new IndicatorFilterDto();
            mockFilterDto.setAge("0-5");
            mockFilterDto.setGender("Male");
            mockFilterDto.setIndicatorId(1L);
            mockFilterDto.setStartDate(mockDate);
            mockFilterDto.setEndDate(mockDate);
            mockFilterDto.setFacilityIds(List.of("F1"));

            Indicator mockIndicator = new Indicator();
            mockIndicator.setIndicatorId(3L);
            mockIndicator.setAge("I3A3");
            mockIndicator.setGender("I3G3");
            mockIndicator.setDisplayType(CommonConstant.INDICATOR_DISPLAY_TYPE_COUNT);
            mockIndicator.setQueryConfigure(false);
            IndicatorNumeratorEquation mi1NEq = new IndicatorNumeratorEquation();
            mi1NEq.setEqIdentifier("nvar");
            mockIndicator.setNumeratorEquation(List.of(mi1NEq));
            mockIndicator.setNumeratorIndicatorEquation("3 + nvar");
            IndicatorDenominatorEquation mi1DEq = new IndicatorDenominatorEquation();
            mi1DEq.setEqIdentifier("dvar");
            mi1DEq.setCode(CommonConstant.ALL_CODE);
            mockIndicator.setDenominatorEquation(List.of(mi1DEq));
            mockIndicator.setDenominatorIndicatorEquation("1 + dvar + invalid");

            when(indicatorRepository.findById(mockFilterDto.getIndicatorId())).thenReturn(Optional.of(mockIndicator));

            ResponseEntity<Object> actualResponse = indicatorService.getIndicatorFilteredCompileValue(mockFilterDto);
            assertNotNull(actualResponse);
            List<Map<String, Object>> actualData = (List<Map<String, Object>>) actualResponse.getBody();
            assertNotNull(actualData);
            assertEquals(1, actualData.size());

            assertEquals(3L, actualData.get(0).get("indicatorId"));
            assertEquals("Count", actualData.get(0).get("indicatorType"));
            assertEquals("Male", actualData.get(0).get("gender"));
            assertEquals("0-5", actualData.get(0).get("age"));
            assertEquals("0.0", actualData.get(0).get("indicatorValue"));
            assertEquals(mockDate, actualData.get(0).get("startDate"));
            assertEquals(mockDate, actualData.get(0).get("endDate"));
        }

        @Test
        void existingIdDirectQuery() {
            // With Current User Facility
            Date mockDate = new Date();

            when(userService.getCurrentUserFacility()).thenReturn(List.of("F1", "F2"));

            when(indicatorQueryBuilder.changeQueryBasedOnFilterValueReplace(anyString(), any(Indicator.class), any(IndicatorFilterDto.class)))
                    .thenAnswer(i -> {
                        String p1 = i.getArgument(0);
                        Indicator p2 = i.getArgument(1);
                        IndicatorFilterDto p3 = i.getArgument(2);
                        return p1 + ":" +
                                p2.getIndicatorId() + ":" +
                                (p3.getAge() != null ? p3.getAge() : "") + ":" +
                                (p3.getGender() != null ? p3.getGender(): "") + ":" +
                                (p3.getStartDate() != null ? p3.getStartDate() : "") + ":" +
                                (p3.getEndDate() != null ? p3.getEndDate() : "");
                    });


            when(
                    indicatorQueryBuilder
                            .getQueryForIndicatorNumeratorEquation(
                                    any(IndicatorNumeratorEquation.class),
                                    anyString(),
                                    any(Indicator.class),
                                    any(IndicatorFilterDto.class)
                            )
            ).thenAnswer(i -> {
                IndicatorNumeratorEquation p1 = i.getArgument(0);
                String p2 = i.getArgument(1);
//            Indicator p3 = i.getArgument(2);
                IndicatorFilterDto p4 = i.getArgument(3);
                return p1.getEqIdentifier() + ":" +
                        p2 + ":" + (p4.getAge() != null ? p4.getAge() : "") + ":" +
                        (p4.getGender() != null ? p4.getGender() : "") + ":" +
                        (p4.getStartDate() != null ? p4.getStartDate() : "") + ":" +
                        (p4.getEndDate() != null ? p4.getEndDate() : "");
            });

            when(
                    indicatorQueryBuilder
                            .getQueryForIndicatorDenominatorEquation(
                                    any(IndicatorDenominatorEquation.class),
                                    anyString(),
                                    any(Indicator.class),
                                    any(IndicatorFilterDto.class)
                            )
            ).thenAnswer(i -> {
                IndicatorDenominatorEquation p1 = i.getArgument(0);
                String p2 = i.getArgument(1);
//            Indicator p3 = i.getArgument(2);
                IndicatorFilterDto p4 = i.getArgument(3);
                return p1.getEqIdentifier() + ":" +
                        p2 + ":" + (p4.getAge() != null ? p4.getAge() : "") + ":" +
                        (p4.getGender() != null ? p4.getGender() : "") + ":" +
                        (p4.getStartDate() != null ? p4.getStartDate() : "") + ":" +
                        (p4.getEndDate() != null ? p4.getEndDate() : "");
            });

            Map<String, List<Map<String, Object>>> observationCustomResourceMap = new HashMap<>();
            List<Map<String, Object>> m1 = mock(List.class); when(m1.size()).thenReturn(11);
            List<Map<String, Object>> m2 = mock(List.class); when(m2.size()).thenReturn(12);
            List<Map<String, Object>> m3 = mock(List.class); when(m3.size()).thenReturn(7);
            List<Map<String, Object>> m4 = mock(List.class); when(m4.size()).thenReturn(12);
            List<Map<String, Object>> m5 = mock(List.class); when(m5.size()).thenReturn(10);
            List<Map<String, Object>> m6 = mock(List.class); when(m6.size()).thenReturn(19);
            List<Map<String, Object>> m7 = mock(List.class); when(m7.size()).thenReturn(15);
            List<Map<String, Object>> m8 = mock(List.class); when(m8.size()).thenReturn(9);
            List<Map<String, Object>> m9 = mock(List.class); when(m9.size()).thenReturn(11);

            observationCustomResourceMap.put("'F1':1:I1A1:I1G1:" + mockDate + ":" + mockDate, m1);
            observationCustomResourceMap.put("'F1':2:I2A2:I2G2:" + mockDate + ":" + mockDate, m2);
            observationCustomResourceMap.put("'F1':3:0-5:Male:" + mockDate + ":" + mockDate, m3);
            observationCustomResourceMap.put("'F1':4:::" + mockDate + ":" + mockDate, m4);
            observationCustomResourceMap.put("'F1':5:::" + mockDate + ":" + mockDate, m5);
            observationCustomResourceMap.put("nvar:F1:0-5:Male:" + mockDate + ":" + mockDate, m6);
            observationCustomResourceMap.put("dvar:F1:0-5:Male:" + mockDate + ":" + mockDate, m7);
            observationCustomResourceMap.put("nvar:F1',':::" + mockDate + ":" + mockDate, m8);
            observationCustomResourceMap.put("dvar:F1',':::" + mockDate + ":" + mockDate, m9);

            when(observationCustomResourceRepository.findByPublished(anyString())).thenAnswer(
                    i -> observationCustomResourceMap.get((String) i.getArgument(0))
            );

            IndicatorFilterDto mockFilterDto = new IndicatorFilterDto();
            mockFilterDto.setAge("0-5");
            mockFilterDto.setGender("Male");
            mockFilterDto.setIndicatorId(1L);
            mockFilterDto.setStartDate(mockDate);
            mockFilterDto.setEndDate(mockDate);
            mockFilterDto.setFacilityIds(List.of("F1"));

            Indicator mockIndicator = new Indicator();
            mockIndicator.setIndicatorId(3L);
            mockIndicator.setAge("I3A3");
            mockIndicator.setGender("I3G3");
            mockIndicator.setDisplayType(CommonConstant.INDICATOR_DISPLAY_TYPE_COUNT);
            mockIndicator.setQueryConfigure(true);
            IndicatorNumeratorEquation mi1NEq = new IndicatorNumeratorEquation();
            mi1NEq.setEqIdentifier("nvar");
            mockIndicator.setNumeratorEquation(List.of(mi1NEq));
            mockIndicator.setNumeratorIndicatorEquation("3 + nvar");
            IndicatorDenominatorEquation mi1DEq = new IndicatorDenominatorEquation();
            mi1DEq.setEqIdentifier("dvar");
            mi1DEq.setCode(CommonConstant.ALL_CODE);
            mockIndicator.setDenominatorEquation(List.of(mi1DEq));
            mockIndicator.setDenominatorIndicatorEquation("1 + dvar");

            when(indicatorRepository.findById(mockFilterDto.getIndicatorId())).thenReturn(Optional.of(mockIndicator));

            ResponseEntity<Object> actualResponse = indicatorService.getIndicatorFilteredCompileValue(mockFilterDto);
            assertNotNull(actualResponse);
            List<Map<String, Object>> actualData = (List<Map<String, Object>>) actualResponse.getBody();
            assertNotNull(actualData);
            assertEquals(1, actualData.size());

            assertEquals(3L, actualData.get(0).get("indicatorId"));
            assertEquals("Count", actualData.get(0).get("indicatorType"));
            assertEquals("Male", actualData.get(0).get("gender"));
            assertEquals("0-5", actualData.get(0).get("age"));
            assertEquals("7.0", actualData.get(0).get("indicatorValue"));
            assertEquals(mockDate, actualData.get(0).get("startDate"));
            assertEquals(mockDate, actualData.get(0).get("endDate"));
        }


        @Test
        void nonExistingId() {
            // With Current User Facility
            when(indicatorRepository.findById(404L)).thenReturn(Optional.empty());

            IndicatorFilterDto mockFilterDto = new IndicatorFilterDto();
            mockFilterDto.setAge("0-5");
            mockFilterDto.setGender("Male");
            mockFilterDto.setIndicatorId(404L);
            ResponseEntity<Object> actualResponse = indicatorService.getIndicatorFilteredCompileValue(mockFilterDto);
            assertNotNull(actualResponse);
            assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
        }
    }

    @Test
    void testGetCommaSeparatedFacilityIdsNullList() {
        assertNull(indicatorService.getCommaSepratedFacilityIds(null));
    }

    @Test
    void testGetCommaSeparatedFacilityIdsWithFullStringNullList() {
        assertNull(indicatorService.getCommaSepratedFacilityIdsWithFullString(null));
    }

    // Mock data generating fn
    List<Indicator> getMockIndicators() throws IOException {
        File file = new File("src/test/resources/mockdata/indicators/mockIndicators.json");
        InputStream fileInputStream = new FileInputStream(file);
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(fileInputStream, new TypeReference<List<Indicator>>() {
        });
    }
}