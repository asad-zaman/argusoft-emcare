package com.argusoft.who.emcare.web.location.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocationMasterDtoTest {
    @Test
    public void testGettersAndSetters() {
        LocationMasterDto dto = new LocationMasterDto();

        dto.setId(1);
        dto.setName("Location 1");
        dto.setType("City");
        dto.setParent(10L);

        assertEquals(Integer.valueOf(1), dto.getId());
        assertEquals("Location 1", dto.getName());
        assertEquals("City", dto.getType());
        assertEquals(Long.valueOf(10L), dto.getParent());
    }

    @Test
    public void testEqualsAndHashCode() {
        LocationMasterDto dto1 = new LocationMasterDto();
        dto1.setId(1);
        dto1.setName("Location 1");
        dto1.setType("City");
        dto1.setParent(10L);

        LocationMasterDto dto2 = new LocationMasterDto();
        dto2.setId(1);
        dto2.setName("Location 1");
        dto2.setType("City");
        dto2.setParent(10L);

        assertEquals(dto1, dto2);

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testToString() {
        LocationMasterDto dto = new LocationMasterDto();
        dto.setId(1);
        dto.setName("Location 1");
        dto.setType("City");
        dto.setParent(10L);

        String expectedToString = "LocationMasterDto(id=1, name=Location 1, type=City, parent=10)";
        assertEquals(expectedToString, dto.toString());
    }
}
