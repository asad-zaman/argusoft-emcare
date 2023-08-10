package com.argusoft.who.emcare.web.indicators.indicator.dto;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IndicatorFilterDtoTest {

    @Test
    void testGettersAndSetters() {
        IndicatorFilterDto dto = new IndicatorFilterDto();

        dto.setIndicatorId(1L);

        Date startDate = new Date();
        dto.setStartDate(startDate);

        Date endDate = new Date();
        dto.setEndDate(endDate);

        dto.setAge("Adult");
        dto.setGender("Male");

        List<String> facilityIds = new ArrayList<>();
        facilityIds.add("Facility1");
        facilityIds.add("Facility2");
        dto.setFacilityIds(facilityIds);

        assertEquals(1L, dto.getIndicatorId());
        assertEquals(startDate, dto.getStartDate());
        assertEquals(endDate, dto.getEndDate());
        assertEquals("Adult", dto.getAge());
        assertEquals("Male", dto.getGender());
        assertEquals(facilityIds, dto.getFacilityIds());
    }


    @Test
    void testDefaultValues() {
        IndicatorFilterDto dto = new IndicatorFilterDto();

        // Check default values
        assertNull(dto.getIndicatorId());
        assertNull(dto.getStartDate());
        assertNull(dto.getEndDate());
        assertNull(dto.getAge());
        assertNull(dto.getGender());
        assertNull(dto.getFacilityIds());
    }
}
