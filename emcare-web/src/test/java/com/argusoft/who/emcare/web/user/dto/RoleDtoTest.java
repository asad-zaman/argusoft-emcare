package com.argusoft.who.emcare.web.user.dto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RoleDtoTest {

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
    void testRoleDto() {
        RoleDto roleDto = new RoleDto();

        String roleName = "Admin";
        String roleDescription = "Administrator role";

        roleDto.setRoleName(roleName);
        roleDto.setRoleDescription(roleDescription);

        assertThat(roleDto.getRoleName()).isEqualTo(roleName);
        assertThat(roleDto.getRoleDescription()).isEqualTo(roleDescription);
    }
}