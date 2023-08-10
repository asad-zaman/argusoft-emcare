package com.argusoft.who.emcare.web.user.dto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class RoleUpdateDtoTest {

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
    void testRoleUpdateDto() {
        RoleUpdateDto roleUpdateDto = new RoleUpdateDto();

        String id = "123";
        String name = "Admin";
        String oldRoleName = "OldAdmin";
        String description = "Administrator role";

        roleUpdateDto.setId(id);
        roleUpdateDto.setName(name);
        roleUpdateDto.setOldRoleName(oldRoleName);
        roleUpdateDto.setDescription(description);

        assertEquals(id, roleUpdateDto.getId());
        assertEquals(name, roleUpdateDto.getName());
        assertEquals(oldRoleName, roleUpdateDto.getOldRoleName());
        assertEquals(description, roleUpdateDto.getDescription());
    }

    @Test
    void testRoleUpdateDtoWithDefaultConstructor() {
        RoleUpdateDto roleUpdateDto = new RoleUpdateDto();

        assertNull(roleUpdateDto.getId());
        assertNull(roleUpdateDto.getName());
        assertNull(roleUpdateDto.getOldRoleName());
        assertNull(roleUpdateDto.getDescription());
    }
}