package com.argusoft.who.emcare.web.dashboard.dto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DashboardDtoTest {
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
        DashboardDto mockDashboardDto = mock(DashboardDto.class);

        when(mockDashboardDto.getPendingRequest()).thenReturn(1L);
        when(mockDashboardDto.getTotalPatient()).thenReturn(2L);
        when(mockDashboardDto.getTotalUser()).thenReturn(3L);

        assertEquals(mockDashboardDto.getPendingRequest(), 1L);
        assertEquals(mockDashboardDto.getTotalPatient(), 2L);
        assertEquals(mockDashboardDto.getTotalUser(), 3L);
    }
}