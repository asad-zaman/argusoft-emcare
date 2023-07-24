package com.argusoft.who.emcare.web.dashboard.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DashboardDtoTest {
    @Test
    void testGetters() {
        DashboardDto mockDashboardDto = mock(DashboardDto.class);

        when(mockDashboardDto.getPendingRequest()).thenReturn(1L);
        when(mockDashboardDto.getTotalPatient()).thenReturn(2L);
        when(mockDashboardDto.getTotalUser()).thenReturn(3L);

        assertEquals(mockDashboardDto.getPendingRequest(), 1L);
        assertEquals(mockDashboardDto.getTotalPatient(), 2L);
        assertEquals(mockDashboardDto.getTotalUser(), 3L);
    }
}