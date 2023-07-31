package com.argusoft.who.emcare.web.location.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocationMasterWithHierarchyTest {
    @Test
    public void testGettersAndSetters() {
        LocationMasterWithHierarchy dto = new LocationMasterWithHierarchy();

        dto.setId(1);
        dto.setName("Location 1");
        dto.setType("City");
        dto.setActive(true);
        dto.setParent(10L);
        dto.setHierarch("Region > City > Suburb");

        assertEquals(Integer.valueOf(1), dto.getId());
        assertEquals("Location 1", dto.getName());
        assertEquals("City", dto.getType());
        assertTrue(dto.isActive());
        assertEquals(Long.valueOf(10L), dto.getParent());
        assertEquals("Region > City > Suburb", dto.getHierarch());
    }

}
