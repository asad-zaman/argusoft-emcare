package com.argusoft.who.emcare.web.user.dto;

import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.location.dto.LocationMasterWithHierarchy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.Test;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MultiLocationUserListDtoTest {

    private MultiLocationUserListDto userDto;

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
    void testMultiLocationUserListDto() {
        MultiLocationUserListDto userListDto = new MultiLocationUserListDto();

        userListDto.setId("A245");
        userListDto.setFirstName("Cameroon");
        userListDto.setLastName("Admin");
        userListDto.setUserName("cameroonadmin");
        userListDto.setEmail("cameroonadmin@gmail.com");
        userListDto.setPhone("31255563");
        userListDto.setCountryCode("US");
        userListDto.setLanguage("English");
        userListDto.setEnabled(true);

        List<String> realmRoles = new ArrayList<>();
        realmRoles.add("CameroonAdmin");
        realmRoles.add("CameroonUser");
        userListDto.setRealmRoles(realmRoles);

        LocationMasterWithHierarchy location1 = new LocationMasterWithHierarchy();
        location1.setId(1);
        location1.setName("Tammoz");
        location1.setType("PHC");
        location1.setActive(true);
        location1.setParent(3456L);
        location1.setHierarch("Child");

        List<LocationMasterWithHierarchy> locations = new ArrayList<>();
        locations.add(location1);
        userListDto.setLocations(locations);

        FacilityDto facility1 = new FacilityDto();
        facility1.setOrganizationName("Cameroon Health Organization");
        facility1.setFacilityName("Tammoz");
        facility1.setAddress("Cameroon");
        facility1.setOrganizationId("Org125");
        facility1.setFacilityId("asada-1sac2-1344");
        facility1.setLocationName("Al-Rabee");
        facility1.setLocationId(1324L);
        facility1.setStatus("Active");

        FacilityDto facility2 = new FacilityDto();
        facility2.setOrganizationName("Cameroon Health Organization");
        facility2.setFacilityName("Tammoz");
        facility2.setAddress("New Cameroon");
        facility2.setOrganizationId("afasgas-1324a-23wra");
        facility2.setFacilityId("Faasaf-2wrsaf-wdsaf");
        facility2.setLocationName("AL-Qadisia");
        facility2.setLocationId(2L);
        facility2.setStatus("Inactive");

        List<FacilityDto> facilities = new ArrayList<>();
        facilities.add(facility1);
        facilities.add(facility2);
        userListDto.setFacilities(facilities);

        assertEquals("A245", userListDto.getId());
        assertEquals("Cameroon", userListDto.getFirstName());
        assertEquals("Admin", userListDto.getLastName());
        assertEquals("cameroonadmin", userListDto.getUserName());
        assertEquals("cameroonadmin@gmail.com", userListDto.getEmail());
        assertEquals("31255563", userListDto.getPhone());
        assertEquals("US", userListDto.getCountryCode());
        assertEquals("English", userListDto.getLanguage());
        assertEquals(true, userListDto.getEnabled());

        assertEquals(realmRoles, userListDto.getRealmRoles());

        List<LocationMasterWithHierarchy> expectedLocations = new ArrayList<>();
        expectedLocations.add(location1);
        assertEquals(expectedLocations, userListDto.getLocations());

        List<FacilityDto> expectedFacilities = new ArrayList<>();
        expectedFacilities.add(facility1);
        expectedFacilities.add(facility2);
        assertEquals(expectedFacilities, userListDto.getFacilities());
    }
}