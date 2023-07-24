package com.argusoft.who.emcare.web.dashboard.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScatterCharDtoTest {
    @Test
    void testGetters() {
        ScatterCharDto mockScatterCharDto = mock(ScatterCharDto.class);

        when(mockScatterCharDto.getDay()).thenReturn(LocalDate.parse("2023-07-21"));
        when(mockScatterCharDto.getCount()).thenReturn(1);

        assertEquals(mockScatterCharDto.getDay(), LocalDate.parse("2023-07-21"));
        assertEquals(mockScatterCharDto.getCount(), 1);
    }
}