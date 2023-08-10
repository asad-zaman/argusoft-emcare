package com.argusoft.who.emcare.web.location.dto;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocationaListDtoTest {
    @Test
    public void testGettersAndSetters() {
        Date mockDate = new Date();
        LocationaListDto dto = new LocationaListDto();

        dto.setId(1);
        dto.setName("Location 1");
        dto.setType("City");
        dto.setActive(true);
        dto.setParent(10L);
        dto.setParentName("Parent Location");
        dto.setCreatedBy("John");
        dto.setCreatedOn(new Date(mockDate.getTime()));
        dto.setModifiedBy("Alice");
        dto.setModifiedOn(new Date(mockDate.getTime()));

        assertEquals(Integer.valueOf(1), dto.getId());
        assertEquals("Location 1", dto.getName());
        assertEquals("City", dto.getType());
        assertTrue(dto.isActive());
        assertEquals(Long.valueOf(10L), dto.getParent());
        assertEquals("Parent Location", dto.getParentName());
        assertEquals("John", dto.getCreatedBy());
        assertEquals("Alice", dto.getModifiedBy());

        assertEquals(mockDate, dto.getCreatedOn());
        assertEquals(mockDate, dto.getModifiedOn());
    }

    @Test
    public void testEqualsAndHashCode() {
        Date mockDate = new Date();
        LocationaListDto dto1 = new LocationaListDto();
        dto1.setId(1);
        dto1.setName("Location 1");
        dto1.setType("City");
        dto1.setActive(true);
        dto1.setParent(10L);
        dto1.setParentName("Parent Location");
        dto1.setCreatedBy("John");
        dto1.setCreatedOn(new Date(mockDate.getTime()));
        dto1.setModifiedBy("Alice");
        dto1.setModifiedOn(new Date(mockDate.getTime()));

        LocationaListDto dto2 = new LocationaListDto();
        dto2.setId(1);
        dto2.setName("Location 1");
        dto2.setType("City");
        dto2.setActive(true);
        dto2.setParent(10L);
        dto2.setParentName("Parent Location");
        dto2.setCreatedBy("John");
        dto2.setCreatedOn(new Date(mockDate.getTime()));
        dto2.setModifiedBy("Alice");
        dto2.setModifiedOn(new Date(mockDate.getTime()));

        assertEquals(dto1, dto2);

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testToString() {
        Date mockDate = new Date();
        LocationaListDto dto = new LocationaListDto();
        dto.setId(1);
        dto.setName("Location 1");
        dto.setType("City");
        dto.setActive(true);
        dto.setParent(10L);
        dto.setParentName("Parent Location");
        dto.setCreatedBy("John");
        dto.setCreatedOn(new Date(mockDate.getTime()));
        dto.setModifiedBy("Alice");
        dto.setModifiedOn(new Date(mockDate.getTime()));

        String expectedToString = "LocationaListDto(id=1, name=Location 1, type=City, isActive=true, parent=10, parentName=Parent Location, createdBy=John, createdOn=" + mockDate + ", modifiedBy=Alice, modifiedOn=" + mockDate + ")";
        assertEquals(expectedToString, dto.toString());
    }
}
