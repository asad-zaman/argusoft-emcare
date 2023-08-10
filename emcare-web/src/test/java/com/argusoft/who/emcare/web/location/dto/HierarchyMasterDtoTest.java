package com.argusoft.who.emcare.web.location.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HierarchyMasterDtoTest {

    @Test
    public void testGettersAndSetters(){
        HierarchyMasterDto dto = new HierarchyMasterDto();
        dto.setHierarchyType("Region");
        dto.setName("Region 1");
        dto.setCode("REG001");

        assertEquals("Region", dto.getHierarchyType());
        assertEquals("Region 1", dto.getName());
        assertEquals("REG001", dto.getCode());
    }

    @Test
    public void testEqualsAndHashCode() {
        HierarchyMasterDto dto1 = new HierarchyMasterDto();
        dto1.setHierarchyType("Region");
        dto1.setName("Region 1");
        dto1.setCode("REG001");

        HierarchyMasterDto dto2 = new HierarchyMasterDto();
        dto2.setHierarchyType("Region");
        dto2.setName("Region 1");
        dto2.setCode("REG001");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testToString() {
        HierarchyMasterDto dto = new HierarchyMasterDto();
        dto.setHierarchyType("Region");
        dto.setName("Region 1");
        dto.setCode("REG001");

        String expectedToString = "HierarchyMasterDto(hierarchyType=Region, name=Region 1, code=REG001)";
        assertEquals(expectedToString, dto.toString());
    }
}
