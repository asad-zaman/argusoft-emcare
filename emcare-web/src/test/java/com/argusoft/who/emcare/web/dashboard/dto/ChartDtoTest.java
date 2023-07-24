package com.argusoft.who.emcare.web.dashboard.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChartDtoTest {

    @Test
    void testGetters() {
        ChartDto mockChartDto = mock(ChartDto.class);

        when(mockChartDto.getCount()).thenReturn(1L);
        when(mockChartDto.getFacilityId()).thenReturn("chart");

        assertEquals(mockChartDto.getCount(), 1L);
        assertEquals(mockChartDto.getFacilityId(), "chart");
    }
}