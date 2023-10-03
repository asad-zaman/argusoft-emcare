package com.argusoft.who.emcare.web.user.mapper;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.user.cons.UserConst;
import com.argusoft.who.emcare.web.user.dto.MultiLocationUserListDto;
import com.argusoft.who.emcare.web.user.dto.UserDto;
import com.argusoft.who.emcare.web.user.dto.UserListDto;
import com.argusoft.who.emcare.web.user.dto.UserMasterDto;
import com.argusoft.who.emcare.web.userlocationmapping.model.UserLocationMapping;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    public void testUserDtoToUserLocationMappingEntityForSignup() {
        UserDto mockUserDTO = new UserDto();
        mockUserDTO.setRegRequestFrom(UserConst.MOBILE);
        String mockUserId = "";

        UserLocationMapping actualUserLocation = UserMapper.userDtoToUserLocationMappingEntityForSignup(mockUserDTO, mockUserId);

        assertEquals(mockUserId, actualUserLocation.getUserId());
        assertTrue(actualUserLocation.isIsFirst());
        assertFalse(actualUserLocation.isState());
        assertEquals(UserConst.MOBILE, actualUserLocation.getRegRequestFrom());

        mockUserDTO.setRegRequestFrom(UserConst.WEB);
        actualUserLocation = UserMapper.userDtoToUserLocationMappingEntityForSignup(mockUserDTO, mockUserId);
        assertEquals(UserConst.WEB, actualUserLocation.getRegRequestFrom());
    }

    @Test
    public void testUserDtoToUserLocationMappingEntity() {
        UserDto mockUserDTO = new UserDto();
        mockUserDTO.setRegRequestFrom(UserConst.MOBILE);
        String mockUserId = "";

        UserLocationMapping actualUserLocation = UserMapper.userDtoToUserLocationMappingEntity(mockUserDTO, mockUserId);

        assertEquals(mockUserId, actualUserLocation.getUserId());
        assertTrue(actualUserLocation.isIsFirst());
        assertFalse(actualUserLocation.isState());
        assertEquals(UserConst.MOBILE, actualUserLocation.getRegRequestFrom());

        mockUserDTO.setRegRequestFrom(UserConst.WEB);
        actualUserLocation = UserMapper.userDtoToUserLocationMappingEntityForSignup(mockUserDTO, mockUserId);
        assertEquals(UserConst.WEB, actualUserLocation.getRegRequestFrom());
        assertFalse(actualUserLocation.isState());
    }

    @Test
    public void testGetMasterUser() {
        AccessToken mockToken = new AccessToken();
        mockToken.setPreferredUsername("testUser");
        mockToken.setSubject("test-subject");
        mockToken.setEmail("test@email.com");

        AccessToken.Access mockAccess = new AccessToken.Access();
        mockAccess.addRole("Admin");
        mockAccess.addRole("Tester");
        mockAccess.addRole("User");
        mockToken.setRealmAccess(mockAccess);

        List<FacilityDto> facilityDtos = List.of(
                new FacilityDto(),
                new FacilityDto(),
                new FacilityDto()
        );

        UserRepresentation mockUserRepresentation = new UserRepresentation();
        mockUserRepresentation.setFirstName("Test");
        mockUserRepresentation.setLastName("Tester");
        Map<String, List<String>> mockUserAttributes = new HashMap<>();
        mockUserAttributes.put(CommonConstant.LANGUAGE_KEY, List.of(CommonConstant.HINDI));
        mockUserAttributes.put(CommonConstant.PHONE_KEY, List.of("1233214567"));
        mockUserAttributes.put(CommonConstant.COUNTRY_CODE, List.of("IND"));
        mockUserRepresentation.setAttributes(mockUserAttributes);

        UserMasterDto actualUserMasterDto = UserMapper.getMasterUser(mockToken, facilityDtos, mockUserRepresentation);

        assertEquals(mockToken.getPreferredUsername(), actualUserMasterDto.getUserName());
        assertEquals(mockUserRepresentation.getFirstName(), actualUserMasterDto.getFirstName());
        assertEquals(mockUserRepresentation.getLastName(), actualUserMasterDto.getLastName());
        assertEquals(facilityDtos.size(), actualUserMasterDto.getFacilities().size());
        assertEquals(facilityDtos.get(0), actualUserMasterDto.getFacilities().get(0));
        assertEquals(mockAccess.getRoles().toArray().length, actualUserMasterDto.getRoles().length);
        assertEquals(mockToken.getSubject(), actualUserMasterDto.getUserId());
        assertEquals(mockToken.getEmail(), actualUserMasterDto.getEmail());
        assertEquals(CommonConstant.HINDI, actualUserMasterDto.getLanguage());
        assertEquals("1233214567", actualUserMasterDto.getPhone());
        assertEquals("IND", actualUserMasterDto.getCountryCode());
    }

    @Test
    public void testGetMasterUserDefaults() {
        AccessToken mockToken = new AccessToken();
        mockToken.setPreferredUsername("testUser");
        mockToken.setSubject("test-subject");
        mockToken.setEmail("test@email.com");

        AccessToken.Access mockAccess = new AccessToken.Access();
        mockAccess.addRole("Admin");
        mockAccess.addRole("Tester");
        mockAccess.addRole("User");
        mockToken.setRealmAccess(mockAccess);

        List<FacilityDto> facilityDtos = List.of(
                new FacilityDto(),
                new FacilityDto(),
                new FacilityDto()
        );

        UserRepresentation mockUserRepresentation = new UserRepresentation();
        mockUserRepresentation.setFirstName("Test");
        mockUserRepresentation.setLastName("Tester");
        mockUserRepresentation.setAttributes(null);

        UserMasterDto actualUserMasterDto = UserMapper.getMasterUser(mockToken, facilityDtos, mockUserRepresentation);

        assertEquals(mockToken.getPreferredUsername(), actualUserMasterDto.getUserName());
        assertEquals(mockUserRepresentation.getFirstName(), actualUserMasterDto.getFirstName());
        assertEquals(mockUserRepresentation.getLastName(), actualUserMasterDto.getLastName());
        assertEquals(facilityDtos.size(), actualUserMasterDto.getFacilities().size());
        assertEquals(facilityDtos.get(0), actualUserMasterDto.getFacilities().get(0));
        assertEquals(mockAccess.getRoles().toArray().length, actualUserMasterDto.getRoles().length);
        assertEquals(mockToken.getSubject(), actualUserMasterDto.getUserId());
        assertEquals(mockToken.getEmail(), actualUserMasterDto.getEmail());
        assertEquals(CommonConstant.ENGLISH, actualUserMasterDto.getLanguage());
        assertNull(actualUserMasterDto.getPhone());
        assertNull(actualUserMasterDto.getCountryCode());
    }

    @Test
    public void testGetUserListDto() {
        FacilityDto facilityDto = new FacilityDto();
        facilityDto.setFacilityId("F1");
        facilityDto.setStatus("Active");
        facilityDto.setFacilityName("Facility 1");
        facilityDto.setOrganizationId("O1");
        facilityDto.setOrganizationName("Org 1");

        UserRepresentation mockUserRepresentation = new UserRepresentation();
        mockUserRepresentation.setId("TEST@ID");
        mockUserRepresentation.setUsername("TEST@User");
        mockUserRepresentation.setFirstName("Test");
        mockUserRepresentation.setLastName("Tester");
        mockUserRepresentation.setEmail("tester@test.com");
        mockUserRepresentation.setEnabled(true);
        mockUserRepresentation.setRealmRoles(List.of("ADMIN", "TESTER", "USER"));
        Map<String, List<String>> mockUserAttributes = new HashMap<>();
        mockUserAttributes.put(CommonConstant.PHONE_KEY, List.of("1233214567"));
        mockUserAttributes.put(CommonConstant.COUNTRY_CODE, List.of("IND"));
        mockUserRepresentation.setAttributes(mockUserAttributes);

        UserListDto actualUserListDto = UserMapper.getUserListDto(mockUserRepresentation, facilityDto);

        assertEquals(mockUserRepresentation.getUsername(), actualUserListDto.getUserName());
        assertEquals(mockUserRepresentation.getFirstName(), actualUserListDto.getFirstName());
        assertEquals(mockUserRepresentation.getLastName(), actualUserListDto.getLastName());
        assertEquals(mockUserRepresentation.isEnabled(), actualUserListDto.getEnabled());

        assertEquals(facilityDto.getFacilityId(), actualUserListDto.getFacilityDto().getFacilityId());
        assertEquals(facilityDto.getFacilityName(), actualUserListDto.getFacilityDto().getFacilityName());
        assertEquals(facilityDto.getStatus(), actualUserListDto.getFacilityDto().getStatus());
        assertEquals(facilityDto.getOrganizationId(), actualUserListDto.getFacilityDto().getOrganizationId());
        assertEquals(facilityDto.getOrganizationName(), actualUserListDto.getFacilityDto().getOrganizationName());

        assertEquals(mockUserRepresentation.getRealmRoles().size(), actualUserListDto.getRealmRoles().size());
        assertEquals(mockUserRepresentation.getId(), actualUserListDto.getId());
        assertEquals(mockUserRepresentation.getEmail(), actualUserListDto.getEmail());
        assertEquals("1233214567", actualUserListDto.getPhone());
        assertEquals("IND", actualUserListDto.getCountryCode());
    }

    @Test
    public void testGetUserListDtoDefaults() {
        FacilityDto facilityDto = new FacilityDto();
        facilityDto.setFacilityId("F1");
        facilityDto.setStatus("Active");
        facilityDto.setFacilityName("Facility 1");
        facilityDto.setOrganizationId("O1");
        facilityDto.setOrganizationName("Org 1");

        UserRepresentation mockUserRepresentation = new UserRepresentation();
        mockUserRepresentation.setId("TEST@ID");
        mockUserRepresentation.setUsername("TEST@User");
        mockUserRepresentation.setFirstName("Test");
        mockUserRepresentation.setLastName("Tester");
        mockUserRepresentation.setEmail("tester@test.com");
        mockUserRepresentation.setEnabled(true);
        mockUserRepresentation.setRealmRoles(List.of("ADMIN", "TESTER", "USER"));
        Map<String, List<String>> mockUserAttributes = new HashMap<>();
        mockUserRepresentation.setAttributes(mockUserAttributes);

        UserListDto actualUserListDto = UserMapper.getUserListDto(mockUserRepresentation, facilityDto);

        assertEquals(mockUserRepresentation.getUsername(), actualUserListDto.getUserName());
        assertEquals(mockUserRepresentation.getFirstName(), actualUserListDto.getFirstName());
        assertEquals(mockUserRepresentation.getLastName(), actualUserListDto.getLastName());
        assertEquals(mockUserRepresentation.isEnabled(), actualUserListDto.getEnabled());

        assertEquals(facilityDto.getFacilityId(), actualUserListDto.getFacilityDto().getFacilityId());
        assertEquals(facilityDto.getFacilityName(), actualUserListDto.getFacilityDto().getFacilityName());
        assertEquals(facilityDto.getStatus(), actualUserListDto.getFacilityDto().getStatus());
        assertEquals(facilityDto.getOrganizationId(), actualUserListDto.getFacilityDto().getOrganizationId());
        assertEquals(facilityDto.getOrganizationName(), actualUserListDto.getFacilityDto().getOrganizationName());

        assertEquals(mockUserRepresentation.getRealmRoles().size(), actualUserListDto.getRealmRoles().size());
        assertEquals(mockUserRepresentation.getId(), actualUserListDto.getId());
        assertEquals(mockUserRepresentation.getEmail(), actualUserListDto.getEmail());
        assertNull(actualUserListDto.getPhone());
        assertNull(actualUserListDto.getCountryCode());
    }

    @Test
    public void testMultiLocationUserListDto() {
        FacilityDto f1 = new FacilityDto();
        f1.setFacilityId("F1");
        f1.setStatus("Active");
        f1.setFacilityName("Facility 1");
        f1.setOrganizationId("O1");
        f1.setOrganizationName("Org 1");

        FacilityDto f2 = new FacilityDto();
        f2.setFacilityId("F1");
        f2.setStatus("Active");
        f2.setFacilityName("Facility 1");
        f2.setOrganizationId("O1");
        f2.setOrganizationName("Org 1");

        FacilityDto f3 = new FacilityDto();
        f3.setFacilityId("F1");
        f3.setStatus("Active");
        f3.setFacilityName("Facility 1");
        f3.setOrganizationId("O1");
        f3.setOrganizationName("Org 1");

        UserRepresentation mockUserRepresentation = new UserRepresentation();
        mockUserRepresentation.setId("TEST@ID");
        mockUserRepresentation.setUsername("TEST@User");
        mockUserRepresentation.setFirstName("Test");
        mockUserRepresentation.setLastName("Tester");
        mockUserRepresentation.setEmail("tester@test.com");
        mockUserRepresentation.setEnabled(true);
        mockUserRepresentation.setRealmRoles(List.of("ADMIN", "TESTER", "USER"));
        Map<String, List<String>> mockUserAttributes = new HashMap<>();
        mockUserAttributes.put(CommonConstant.PHONE_KEY, List.of("1233214567"));
        mockUserAttributes.put(CommonConstant.COUNTRY_CODE, List.of("IND"));
        mockUserAttributes.put(CommonConstant.LANGUAGE_KEY, List.of(CommonConstant.HINDI));
        mockUserRepresentation.setAttributes(mockUserAttributes);

        MultiLocationUserListDto actualMLUserListDto = UserMapper.getMultiLocationUserListDto(mockUserRepresentation, List.of(f1, f2, f3));

        assertEquals(mockUserRepresentation.getUsername(), actualMLUserListDto.getUserName());
        assertEquals(mockUserRepresentation.getFirstName(), actualMLUserListDto.getFirstName());
        assertEquals(mockUserRepresentation.getLastName(), actualMLUserListDto.getLastName());
        assertEquals(mockUserRepresentation.isEnabled(), actualMLUserListDto.getEnabled());

        assertEquals(f1.getFacilityId(), actualMLUserListDto.getFacilities().get(0).getFacilityId());
        assertEquals(f1.getFacilityName(), actualMLUserListDto.getFacilities().get(0).getFacilityName());
        assertEquals(f2.getStatus(), actualMLUserListDto.getFacilities().get(1).getStatus());
        assertEquals(f2.getOrganizationId(), actualMLUserListDto.getFacilities().get(1).getOrganizationId());
        assertEquals(f3.getOrganizationName(), actualMLUserListDto.getFacilities().get(2).getOrganizationName());

        assertEquals(mockUserRepresentation.getRealmRoles().size(), actualMLUserListDto.getRealmRoles().size());
        assertEquals(mockUserRepresentation.getId(), actualMLUserListDto.getId());
        assertEquals(mockUserRepresentation.getEmail(), actualMLUserListDto.getEmail());
        assertEquals(CommonConstant.HINDI, actualMLUserListDto.getLanguage());
        assertEquals("1233214567", actualMLUserListDto.getPhone());
        assertEquals("IND", actualMLUserListDto.getCountryCode());
    }


    @Test
    public void testMultiLocationUserListDtoDefaults() {
        FacilityDto f1 = new FacilityDto();
        f1.setFacilityId("F1");
        f1.setStatus("Active");
        f1.setFacilityName("Facility 1");
        f1.setOrganizationId("O1");
        f1.setOrganizationName("Org 1");

        FacilityDto f2 = new FacilityDto();
        f2.setFacilityId("F1");
        f2.setStatus("Active");
        f2.setFacilityName("Facility 1");
        f2.setOrganizationId("O1");
        f2.setOrganizationName("Org 1");

        FacilityDto f3 = new FacilityDto();
        f3.setFacilityId("F1");
        f3.setStatus("Active");
        f3.setFacilityName("Facility 1");
        f3.setOrganizationId("O1");
        f3.setOrganizationName("Org 1");

        UserRepresentation mockUserRepresentation = new UserRepresentation();
        mockUserRepresentation.setId("TEST@ID");
        mockUserRepresentation.setUsername("TEST@User");
        mockUserRepresentation.setFirstName("Test");
        mockUserRepresentation.setLastName("Tester");
        mockUserRepresentation.setEmail("tester@test.com");
        mockUserRepresentation.setEnabled(true);
        mockUserRepresentation.setRealmRoles(List.of("ADMIN", "TESTER", "USER"));
        Map<String, List<String>> mockUserAttributes = new HashMap<>();
        mockUserRepresentation.setAttributes(mockUserAttributes);

        MultiLocationUserListDto actualMLUserListDto = UserMapper.getMultiLocationUserListDto(mockUserRepresentation, List.of(f1, f2, f3));

        assertEquals(mockUserRepresentation.getUsername(), actualMLUserListDto.getUserName());
        assertEquals(mockUserRepresentation.getFirstName(), actualMLUserListDto.getFirstName());
        assertEquals(mockUserRepresentation.getLastName(), actualMLUserListDto.getLastName());
        assertEquals(mockUserRepresentation.isEnabled(), actualMLUserListDto.getEnabled());

        assertEquals(f1.getFacilityId(), actualMLUserListDto.getFacilities().get(0).getFacilityId());
        assertEquals(f1.getFacilityName(), actualMLUserListDto.getFacilities().get(0).getFacilityName());
        assertEquals(f2.getStatus(), actualMLUserListDto.getFacilities().get(1).getStatus());
        assertEquals(f2.getOrganizationId(), actualMLUserListDto.getFacilities().get(1).getOrganizationId());
        assertEquals(f3.getOrganizationName(), actualMLUserListDto.getFacilities().get(2).getOrganizationName());

        assertEquals(mockUserRepresentation.getRealmRoles().size(), actualMLUserListDto.getRealmRoles().size());
        assertEquals(mockUserRepresentation.getId(), actualMLUserListDto.getId());
        assertEquals(mockUserRepresentation.getEmail(), actualMLUserListDto.getEmail());
        assertEquals(CommonConstant.ENGLISH, actualMLUserListDto.getLanguage());
        assertNull(actualMLUserListDto.getPhone());
        assertNull(actualMLUserListDto.getCountryCode());
    }

    @Test
    public void testGetUserMappingEntityPerLocation() {
        UserDto mockUserDto = new UserDto();
        mockUserDto.setRegRequestFrom(UserConst.MOBILE);
        mockUserDto.setFacilityIds(List.of("F1", "F2", "F3"));

        String mockUserId = "";
        Map<String, Long> locationData = new HashMap<>();
        locationData.put("F1", 1L);
        locationData.put("F2", 2L);
        locationData.put("F3", 3L);
        locationData.put("F4", 4L);
        Boolean isFirst = true;

        List<UserLocationMapping> actualULMap = UserMapper.getUserMappingEntityPerLocation(mockUserDto, mockUserId, locationData, isFirst);

        assertNotNull(actualULMap);
        assertEquals(mockUserDto.getFacilityIds().size(), actualULMap.size());
        for (int i = 0; i < actualULMap.size(); i++) {
            assertEquals(mockUserId, actualULMap.get(i).getUserId());
            assertEquals("F" + (i + 1), actualULMap.get(i).getFacilityId());
            assertEquals(locationData.get("F" + (i + 1)).intValue(), actualULMap.get(i).getLocationId());
            assertEquals(mockUserId, actualULMap.get(i).getUserId());
            assertFalse(actualULMap.get(i).isState());
            assertEquals(UserConst.MOBILE, actualULMap.get(i).getRegRequestFrom());
        }

        // With Defaults
        mockUserDto.setRegRequestFrom(UserConst.WEB);
        actualULMap = UserMapper.getUserMappingEntityPerLocation(mockUserDto, mockUserId, locationData, isFirst);

        assertNotNull(actualULMap);
        assertEquals(mockUserDto.getFacilityIds().size(), actualULMap.size());
        for (int i = 0; i < actualULMap.size(); i++) {
            assertEquals(mockUserId, actualULMap.get(i).getUserId());
            assertEquals("F" + (i + 1), actualULMap.get(i).getFacilityId());
            assertEquals(locationData.get("F" + (i + 1)).intValue(), actualULMap.get(i).getLocationId());
            assertEquals(mockUserId, actualULMap.get(i).getUserId());
            assertTrue(actualULMap.get(i).isState());
            assertEquals(UserConst.WEB, actualULMap.get(i).getRegRequestFrom());
        }
    }

    @Test
    public void  tesGetUserMappingEntityPerLocationForTenant() {
        UserDto mockUserDto = new UserDto();
        String mockUserId = "";
        Map<String, Long> locationData = new HashMap<>();
        locationData.put("F1", 1L);
        locationData.put("F2", 2L);
        locationData.put("F3", 3L);
        locationData.put("F4", 4L);

        List<UserLocationMapping> actualULMap = UserMapper.getUserMappingEntityPerLocationForTenant(mockUserDto, mockUserId, locationData);

        assertNotNull(actualULMap);
        assertEquals(1, actualULMap.size());
        assertEquals(mockUserId, actualULMap.get(0).getUserId());
        assertFalse(actualULMap.get(0).isIsFirst());
        assertEquals(UserConst.WEB, actualULMap.get(0).getRegRequestFrom());
        assertTrue(actualULMap.get(0).isState());
        assertTrue(actualULMap.get(0).isState());
    }
}