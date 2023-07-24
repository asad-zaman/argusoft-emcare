package com.argusoft.who.emcare.web.dashboard.dto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChartDtoTest {

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
    void testGetters() {
        ChartDto mockChartDto = mock(ChartDto.class);

        when(mockChartDto.getCount()).thenReturn(1L);
        when(mockChartDto.getFacilityId()).thenReturn("chart");

        assertEquals(mockChartDto.getCount(), 1L);
        assertEquals(mockChartDto.getFacilityId(), "chart");
    }
}