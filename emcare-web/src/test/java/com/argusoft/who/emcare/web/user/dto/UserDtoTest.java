package com.argusoft.who.emcare.web.user.dto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

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
    void testUserDto() {
        String firstName = "Kamekazi";
        String lastName = "Admin";
        String password = "x12345abc";
        String email = "kamekazi.admin@gmail.com";
        String userName = "kamekazi";
        String regRequestFrom = "Web";
        String roleName = "User";
        String language = "English";
        String phone = "789123456";
        String countryCode = "+1";
        List<String> facilityIds = Arrays.asList("tammoz", "al-rabee");

        UserDto userDto = new UserDto();
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setPassword(password);
        userDto.setEmail(email);
        userDto.setUserName(userName);
        userDto.setRegRequestFrom(regRequestFrom);
        userDto.setRoleName(roleName);
        userDto.setLanguage(language);
        userDto.setPhone(phone);
        userDto.setCountryCode(countryCode);
        userDto.setFacilityIds(facilityIds);

        assertEquals(firstName, userDto.getFirstName());
        assertEquals(lastName, userDto.getLastName());
        assertEquals(password, userDto.getPassword());
        assertEquals(email, userDto.getEmail());
        assertEquals(userName, userDto.getUserName());
        assertEquals(regRequestFrom, userDto.getRegRequestFrom());
        assertEquals(roleName, userDto.getRoleName());
        assertEquals(language, userDto.getLanguage());
        assertEquals(phone, userDto.getPhone());
        assertEquals(countryCode, userDto.getCountryCode());
        assertEquals(facilityIds, userDto.getFacilityIds());
    }

    @Test
    void testUserDtoWithDefaultConstructor() {
        UserDto userDto = new UserDto();

        assertNull(userDto.getFirstName());
        assertNull(userDto.getLastName());
        assertNull(userDto.getPassword());
        assertNull(userDto.getEmail());
        assertNull(userDto.getUserName());
        assertNull(userDto.getRegRequestFrom());
        assertNull(userDto.getRoleName());
        assertNull(userDto.getLanguage());
        assertNull(userDto.getPhone());
        assertNull(userDto.getCountryCode());
        assertNull(userDto.getFacilityIds());
    }
}