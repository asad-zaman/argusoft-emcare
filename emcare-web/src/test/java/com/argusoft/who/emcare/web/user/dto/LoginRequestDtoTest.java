package com.argusoft.who.emcare.web.user.dto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class LoginRequestDtoTest {
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
    public void testGetters() {
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("testUser");
        loginRequestDto.setPassword("testPassword");

        assertEquals("testUser", loginRequestDto.getUsername());
        assertEquals("testPassword", loginRequestDto.getPassword());
    }
}
