package com.argusoft.who.emcare.web.user.dto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class UserUpdateDtoTest {

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
    void testUserUpdateDto() {
        UserUpdateDto userUpdateDto = new UserUpdateDto();

        String userId = "PA12";
        boolean isEnabled = true;

        userUpdateDto.setUserId(userId);
        userUpdateDto.setIsEnabled(isEnabled);

        assertEquals(userId, userUpdateDto.getUserId());
        assertEquals(isEnabled, userUpdateDto.getIsEnabled());
    }
}