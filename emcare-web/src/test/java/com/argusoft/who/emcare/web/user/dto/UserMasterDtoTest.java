package com.argusoft.who.emcare.web.user.dto;

import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.menu.dto.CurrentUserFeatureJson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserMasterDtoTest {
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
    void testUserMasterDto() {
        FacilityDto facilityDto = new FacilityDto();
        facilityDto.setOrganizationName("Org1");
        facilityDto.setFacilityName("Facility1");

        UserMasterDto userMasterDto = new UserMasterDto();

        String userId = "A1234";
        String userName = "emcare";
        String email = "emcare@gmail.com";
        String language = "en";
        String[] roles = {"Admin", "User"};
        String firstName = "EM";
        String lastName = "Care";
        String phone = "7666321541";
        String countryCode = "+1";
        List<FacilityDto> facilities = new ArrayList<>();
        facilities.add(facilityDto);

        userMasterDto.setUserId(userId);
        userMasterDto.setUserName(userName);
        userMasterDto.setEmail(email);
        userMasterDto.setLanguage(language);
        userMasterDto.setRoles(roles);
        userMasterDto.setFirstName(firstName);
        userMasterDto.setLastName(lastName);
        userMasterDto.setPhone(phone);
        userMasterDto.setCountryCode(countryCode);
        userMasterDto.setFacilities(facilities);

        assertThat(userMasterDto.getUserId()).isEqualTo(userId);
        assertThat(userMasterDto.getUserName()).isEqualTo(userName);
        assertThat(userMasterDto.getEmail()).isEqualTo(email);
        assertThat(userMasterDto.getLanguage()).isEqualTo(language);
        assertThat(userMasterDto.getRoles()).isEqualTo(roles);
        assertThat(userMasterDto.getFirstName()).isEqualTo(firstName);
        assertThat(userMasterDto.getLastName()).isEqualTo(lastName);
        assertThat(userMasterDto.getPhone()).isEqualTo(phone);
        assertThat(userMasterDto.getCountryCode()).isEqualTo(countryCode);
        assertThat(userMasterDto.getFacilities()).isEqualTo(facilities);
    }
}