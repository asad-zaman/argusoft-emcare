package com.argusoft.who.emcare.web.user.dto;

import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserListDtoTest {

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
    void testUserListDto() {
        FacilityDto facilityDto = new FacilityDto();
        facilityDto.setOrganizationName("Org1");
        facilityDto.setFacilityName("Facility1");

        UserListDto userListDto = new UserListDto();

        String id = "X11";
        String firstName = "Iraq";
        String lastName = "Admin";
        String userName = "Iraqadmin";
        String email = "iraqadmin@gmail.com";
        String phone = "56792134";
        String countryCode = "+1";
        Boolean enabled = true;
        List<String> realmRoles = Arrays.asList("User", "Iraq_User");

        userListDto.setId(id);
        userListDto.setFirstName(firstName);
        userListDto.setLastName(lastName);
        userListDto.setUserName(userName);
        userListDto.setEmail(email);
        userListDto.setPhone(phone);
        userListDto.setCountryCode(countryCode);
        userListDto.setEnabled(enabled);
        userListDto.setRealmRoles(realmRoles);
        userListDto.setFacilityDto(facilityDto);

        assertThat(userListDto.getId()).isEqualTo(id);
        assertThat(userListDto.getFirstName()).isEqualTo(firstName);
        assertThat(userListDto.getLastName()).isEqualTo(lastName);
        assertThat(userListDto.getUserName()).isEqualTo(userName);
        assertThat(userListDto.getEmail()).isEqualTo(email);
        assertThat(userListDto.getPhone()).isEqualTo(phone);
        assertThat(userListDto.getCountryCode()).isEqualTo(countryCode);
        assertThat(userListDto.getEnabled()).isEqualTo(enabled);
        assertThat(userListDto.getRealmRoles()).isEqualTo(realmRoles);
        assertThat(userListDto.getFacilityDto()).isEqualTo(facilityDto);
    }
}