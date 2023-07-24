package com.argusoft.who.emcare.web.dashboard.dto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScatterCharDtoTest {
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
        ScatterCharDto mockScatterCharDto = mock(ScatterCharDto.class);

        when(mockScatterCharDto.getDay()).thenReturn(LocalDate.parse("2023-07-21"));
        when(mockScatterCharDto.getCount()).thenReturn(1);

        assertEquals(mockScatterCharDto.getDay(), LocalDate.parse("2023-07-21"));
        assertEquals(mockScatterCharDto.getCount(), 1);
    }
}