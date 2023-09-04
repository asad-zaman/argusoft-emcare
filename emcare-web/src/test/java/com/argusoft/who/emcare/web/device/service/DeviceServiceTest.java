package com.argusoft.who.emcare.web.device.service;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.config.KeyCloakConfig;
import com.argusoft.who.emcare.web.device.dao.DeviceRepository;
import com.argusoft.who.emcare.web.device.dto.DeviceDto;
import com.argusoft.who.emcare.web.device.dto.DeviceWithUserDetails;
import com.argusoft.who.emcare.web.device.model.DeviceMaster;
import com.argusoft.who.emcare.web.device.service.impl.DeviceServiceImpl;
import com.argusoft.who.emcare.web.secuirty.EmCareSecurityUser;
import com.argusoft.who.emcare.web.user.dto.MultiLocationUserListDto;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.apache.commons.beanutils.BeanUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {DeviceService.class})
@RunWith(SpringJUnit4ClassRunner.class)
class DeviceServiceTest {
    @Mock
    EmCareSecurityUser emCareSecurityUser;

    @Mock
    DeviceRepository deviceRepository;

    @Mock
    UserService userService;

    @Mock
    KeyCloakConfig keyCloakConfig;

    @InjectMocks
    @Spy
    DeviceServiceImpl deviceService;


    AutoCloseable autoCloseable;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(deviceService, "realm", "master");
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Nested
    class testAddNewDevice {
        @BeforeEach
        public void setUpAddNewDeviceTest() {
            when(deviceRepository.save(any(DeviceMaster.class))).thenAnswer(i -> {
                DeviceMaster d = i.getArgument(0);
                if (d.getDeviceId() == null) d.setDeviceId(404);
                return d;
            });
        }

        @Test
        void newDevice() {
            DeviceDto deviceDto = new DeviceDto();
            deviceDto.setDeviceId(1);
            deviceDto.setDeviceUUID("D-UUID");
            deviceDto.setIsBlocked(false);
            deviceDto.setDeviceOs("Android 13");
            deviceDto.setDeviceModel("GPixel7A");
            deviceDto.setDeviceName("Google Pixel 7A");
            deviceDto.setIgVersion("igV1");
            deviceDto.setImeiNumber("IMEI_NUMBER");
            deviceDto.setAndroidVersion("13");
            deviceDto.setMacAddress("FA-GR-E1-G5-99-0P");
            deviceDto.setUserId("U1");

            String mockUserId = "U1";
            String mockUserName = "NUN";

            when(emCareSecurityUser.getLoggedInUserId()).thenReturn(mockUserId);
            when(emCareSecurityUser.getLoggedInUserName()).thenReturn(mockUserName);
            when(deviceRepository.getDeviceByDeviceUUID(deviceDto.getDeviceUUID())).thenReturn(Optional.empty());

            ResponseEntity<Object> actualResponse = deviceService.addNewDevice(deviceDto);
            assertNotNull(actualResponse);
            assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
            DeviceMaster actualDevice = (DeviceMaster) actualResponse.getBody();
            assertNotNull(actualDevice);
            assertEquals(deviceDto.getDeviceOs(), actualDevice.getDeviceOs());
            assertEquals(404, actualDevice.getDeviceId());
            assertEquals(deviceDto.getDeviceName(), actualDevice.getDeviceName());
            assertEquals(deviceDto.getDeviceModel(), actualDevice.getDeviceModel());
            assertEquals(deviceDto.getIsBlocked(), actualDevice.getIsBlocked());
            assertEquals(deviceDto.getIgVersion(), actualDevice.getIgVersion());
            assertEquals(deviceDto.getAndroidVersion(), actualDevice.getAndroidVersion());
            assertEquals(deviceDto.getMacAddress(), actualDevice.getMacAddress());
            assertEquals(deviceDto.getImeiNumber(), actualDevice.getImeiNumber());
            assertEquals(deviceDto.getDeviceUUID(), actualDevice.getDeviceUUID());
            assertEquals(mockUserId, actualDevice.getLastLoggedInUser());
            assertEquals(mockUserName, actualDevice.getUserName());
        }

        @Test
        void existing() {
            DeviceDto deviceDto = new DeviceDto();
            deviceDto.setDeviceId(1);
            deviceDto.setDeviceUUID("D-UUID");
            deviceDto.setIsBlocked(false);
            deviceDto.setDeviceOs("Android 13");
            deviceDto.setDeviceModel("GPixel7A");
            deviceDto.setDeviceName("Google Pixel 7A");
            deviceDto.setIgVersion("igV1");
            deviceDto.setImeiNumber("IMEI_NUMBER");
            deviceDto.setAndroidVersion("13");
            deviceDto.setMacAddress("FA-GR-E1-G5-99-0P");
            deviceDto.setUserId("U1");

            String mockUserId = "U1";
            String mockUserName = "NUN";

            DeviceMaster mockOldDevice = new DeviceMaster();
            mockOldDevice.setDeviceId(1);
            mockOldDevice.setDeviceName("Google Pixel 7A");
            mockOldDevice.setDeviceUUID(deviceDto.getDeviceUUID());
            mockOldDevice.setIsBlocked(true);
            mockOldDevice.setDeviceOs("Android 12");
            mockOldDevice.setDeviceModel(deviceDto.getDeviceModel());
            mockOldDevice.setDeviceName(deviceDto.getDeviceName());
            mockOldDevice.setIgVersion("igV0");
            mockOldDevice.setImeiNumber(deviceDto.getImeiNumber());
            mockOldDevice.setAndroidVersion("12");
            mockOldDevice.setMacAddress("RA-GR-E1-G5-99-0P");
            mockOldDevice.setUserName("UN");

            Optional<DeviceMaster> mockOldDeviceOptional = Optional.of(mockOldDevice);

            when(emCareSecurityUser.getLoggedInUserId()).thenReturn(mockUserId);
            when(emCareSecurityUser.getLoggedInUserName()).thenReturn(mockUserName);
            when(deviceRepository.getDeviceByDeviceUUID(deviceDto.getDeviceUUID())).thenReturn(mockOldDeviceOptional);

            ResponseEntity<Object> actualResponse = deviceService.addNewDevice(deviceDto);
            assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
            assertNotNull(actualResponse);
            DeviceDto actualDevice = (DeviceDto) actualResponse.getBody();
            assertNotNull(actualDevice);
            assertEquals(deviceDto.getDeviceOs(), actualDevice.getDeviceOs());
            assertEquals(deviceDto.getDeviceId(), actualDevice.getDeviceId());
            assertEquals(deviceDto.getDeviceName(), actualDevice.getDeviceName());
            assertEquals(deviceDto.getDeviceModel(), actualDevice.getDeviceModel());
            assertEquals(deviceDto.getIsBlocked(), actualDevice.getIsBlocked());
            assertEquals(deviceDto.getIgVersion(), actualDevice.getIgVersion());
            assertEquals(deviceDto.getAndroidVersion(), actualDevice.getAndroidVersion());
            assertEquals(deviceDto.getMacAddress(), actualDevice.getMacAddress());
            assertEquals(deviceDto.getImeiNumber(), actualDevice.getImeiNumber());
            assertEquals(deviceDto.getDeviceUUID(), actualDevice.getDeviceUUID());
        }
    }

    @Nested
    class testUpdateDeviceDetails {
        @BeforeEach
        public void setUpUpdateDeviceDetails() {
            when(deviceRepository.save(any(DeviceMaster.class))).thenAnswer(i -> {
                DeviceMaster d = i.getArgument(0);
                if (d.getDeviceId() == null) d.setDeviceId(404);
                return d;
            });
        }

        @Test
        void notFound() {
            DeviceDto deviceDto = new DeviceDto();
            deviceDto.setDeviceId(1);

            AccessToken mockAccessToken = new AccessToken();
            mockAccessToken.setSubject("sub");
            String mockUserName = "NUN";

            when(emCareSecurityUser.getLoggedInUser()).thenReturn(mockAccessToken);
            when(emCareSecurityUser.getLoggedInUserName()).thenReturn(mockUserName);
            when(deviceRepository.findById(deviceDto.getDeviceId())).thenReturn(Optional.empty());

            ResponseEntity<Object> actualResponse = deviceService.updateDeviceDetails(deviceDto);
            assertNotNull(actualResponse);
            assertEquals(HttpStatus.NO_CONTENT, actualResponse.getStatusCode());
        }

        @Test
        void existing() {
            DeviceDto deviceDto = new DeviceDto();
            deviceDto.setDeviceId(1);
            deviceDto.setDeviceUUID("D-UUID");
            deviceDto.setIsBlocked(false);
            deviceDto.setDeviceOs("Android 13");
            deviceDto.setDeviceModel("GPixel7A");
            deviceDto.setDeviceName("Google Pixel 7A");
            deviceDto.setIgVersion("igV1");
            deviceDto.setImeiNumber("IMEI_NUMBER");
            deviceDto.setAndroidVersion("13");
            deviceDto.setMacAddress("FA-GR-E1-G5-99-0P");
            deviceDto.setUserId("U1");

            DeviceMaster mockDeviceMaster = new DeviceMaster();
            mockDeviceMaster.setDeviceId(1);
            mockDeviceMaster.setDeviceName("Google Pixel 7A");
            mockDeviceMaster.setDeviceUUID(deviceDto.getDeviceUUID());
            mockDeviceMaster.setIsBlocked(true);
            mockDeviceMaster.setDeviceOs("Android 12");
            mockDeviceMaster.setDeviceModel(deviceDto.getDeviceModel());
            mockDeviceMaster.setDeviceName(deviceDto.getDeviceName());
            mockDeviceMaster.setIgVersion("igV0");
            mockDeviceMaster.setImeiNumber(deviceDto.getImeiNumber());
            mockDeviceMaster.setAndroidVersion("12");
            mockDeviceMaster.setMacAddress("RA-GR-E1-G5-99-0P");
            mockDeviceMaster.setUserName("UN");

            AccessToken mockAccessToken = new AccessToken();
            mockAccessToken.setSubject("sub");
            String mockUserName = "NUN";

            when(emCareSecurityUser.getLoggedInUser()).thenReturn(mockAccessToken);
            when(emCareSecurityUser.getLoggedInUserName()).thenReturn(mockUserName);
            when(deviceRepository.findById(deviceDto.getDeviceId())).thenReturn(Optional.of(mockDeviceMaster));

            Keycloak mockKeyCloak = mock(Keycloak.class);
            when(keyCloakConfig.getInstance()).thenReturn(mockKeyCloak);
            RealmResource realmResource = mock(RealmResource.class);
            when(mockKeyCloak.realm(anyString())).thenReturn(realmResource);
            UsersResource usersResource = mock(UsersResource.class);
            when(realmResource.users()).thenReturn(usersResource);
            UserResource userResource = mock(UserResource.class);
            when(usersResource.get(mockAccessToken.getSubject())).thenReturn(userResource);
            UserSessionRepresentation userSessionRepresentation = new UserSessionRepresentation();
            userSessionRepresentation.setId("Session-1");
            when(userResource.getUserSessions()).thenReturn(List.of(userSessionRepresentation));

            ResponseEntity<Object> actualResponse = deviceService.updateDeviceDetails(deviceDto);

            verify(realmResource).deleteSession(userSessionRepresentation.getId());

            assertNotNull(actualResponse);
            assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
            DeviceMaster actualDevice = (DeviceMaster) actualResponse.getBody();
            assertNotNull(actualDevice);
            // @TODO: should verify
            // assertEquals(deviceDto.getDeviceOs(), actualDevice.getDeviceOs());
            // assertEquals(deviceDto.getDeviceName(), actualDevice.getDeviceName());
            // assertEquals(deviceDto.getDeviceModel(), actualDevice.getDeviceModel());
            // assertEquals(deviceDto.getDeviceUUID(), actualDevice.getDeviceUUID());
            assertEquals(deviceDto.getDeviceId(), actualDevice.getDeviceId());
            assertEquals(deviceDto.getIsBlocked(), actualDevice.getIsBlocked());
            assertEquals(deviceDto.getIgVersion(), actualDevice.getIgVersion());
            assertEquals(deviceDto.getAndroidVersion(), actualDevice.getAndroidVersion());
            assertEquals(deviceDto.getMacAddress(), actualDevice.getMacAddress());
            assertEquals(deviceDto.getImeiNumber(), actualDevice.getImeiNumber());
            assertEquals(mockUserName, actualDevice.getUserName());
        }
    }


    @Nested
    class testChangeDeviceStatus {
        @BeforeEach
        public void setUpChangeDeviceStatus() {
            when(deviceRepository.save(any(DeviceMaster.class))).thenAnswer(i -> {
                DeviceMaster d = i.getArgument(0);
                if (d.getDeviceId() == null) d.setDeviceId(404);
                return d;
            });
        }

        @Test
        void deviceExistsAndSetToActive() {
            Integer deviceId = 1;
            Boolean status = true;
            DeviceMaster mockDeviceMaster = new DeviceMaster();
            mockDeviceMaster.setDeviceId(deviceId);
            mockDeviceMaster.setLastLoggedInUser("last_user");
            mockDeviceMaster.setIsBlocked(false);
            when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(mockDeviceMaster));

            Keycloak mockKeyCloak = mock(Keycloak.class);
            when(keyCloakConfig.getInstance()).thenReturn(mockKeyCloak);
            RealmResource realmResource = mock(RealmResource.class);
            when(mockKeyCloak.realm(anyString())).thenReturn(realmResource);
            UsersResource usersResource = mock(UsersResource.class);
            when(realmResource.users()).thenReturn(usersResource);
            UserResource userResource = mock(UserResource.class);
            when(usersResource.get(mockDeviceMaster.getLastLoggedInUser())).thenReturn(userResource);
            UserSessionRepresentation userSessionRepresentation = new UserSessionRepresentation();
            userSessionRepresentation.setId("Session-1");
            when(userResource.getUserSessions()).thenReturn(List.of(userSessionRepresentation));

            ResponseEntity<Object> actualResponse = deviceService.changeDeviceStatus(deviceId, status);

            verify(realmResource).deleteSession(userSessionRepresentation.getId());

            assertNotNull(actualResponse);
            assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
            DeviceMaster actualDevice = (DeviceMaster) actualResponse.getBody();
            assertNotNull(actualDevice);
            assertEquals(deviceId, actualDevice.getDeviceId());
            assertEquals(status, actualDevice.getIsBlocked());
        }

        @Test
        void deviceExistsAndSetToInactive() {
            Integer deviceId = 1;
            Boolean status = false;
            DeviceMaster mockDeviceMaster = new DeviceMaster();
            mockDeviceMaster.setDeviceId(deviceId);
            mockDeviceMaster.setLastLoggedInUser("last_user");
            mockDeviceMaster.setIsBlocked(false);
            when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(mockDeviceMaster));

            Keycloak mockKeyCloak = mock(Keycloak.class);
            when(keyCloakConfig.getInstance()).thenReturn(mockKeyCloak);
            RealmResource realmResource = mock(RealmResource.class);
            when(mockKeyCloak.realm(anyString())).thenReturn(realmResource);
            UsersResource usersResource = mock(UsersResource.class);
            when(realmResource.users()).thenReturn(usersResource);
            UserResource userResource = mock(UserResource.class);
            when(usersResource.get(mockDeviceMaster.getLastLoggedInUser())).thenReturn(userResource);
            UserSessionRepresentation userSessionRepresentation = new UserSessionRepresentation();
            userSessionRepresentation.setId("Session-1");
            when(userResource.getUserSessions()).thenReturn(List.of(userSessionRepresentation));

            ResponseEntity<Object> actualResponse = deviceService.changeDeviceStatus(deviceId, status);

            verify(realmResource).deleteSession(userSessionRepresentation.getId());

            assertNotNull(actualResponse);
            assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
            DeviceMaster actualDevice = (DeviceMaster) actualResponse.getBody();
            assertNotNull(actualDevice);
            assertEquals(deviceId, actualDevice.getDeviceId());
            assertEquals(status, actualDevice.getIsBlocked());
        }

        @Test
        void deviceDoesNotExists() {
            Integer deviceId = 1;
            Boolean status = false;
            DeviceMaster mockDeviceMaster = new DeviceMaster();
            mockDeviceMaster.setDeviceId(deviceId);
            mockDeviceMaster.setLastLoggedInUser("last_user");
            mockDeviceMaster.setIsBlocked(false);
            when(deviceRepository.findById(deviceId)).thenReturn(Optional.empty());

            ResponseEntity<Object> actualResponse = deviceService.changeDeviceStatus(deviceId, status);

            assertNotNull(actualResponse);
            assertEquals(HttpStatus.NO_CONTENT, actualResponse.getStatusCode());
        }
    }

    @Nested
    class testGetDeviceInfoByImei {

        @Test
        void byNothing() {
            ResponseEntity<Object> actualResponse = deviceService.getDeviceInfoByImei(null, null, null, null);

            // @TODO: should be null or some short of error.
            assertNotNull(actualResponse);

            DeviceMaster actualDevice = (DeviceMaster) actualResponse.getBody();
            assertNotNull(actualDevice);
        }

        @Test
        void byImei() {
            DeviceMaster mockDeviceMaster = getMockDeviceMaster();

            when(deviceRepository.getDeviceByImei(mockDeviceMaster.getImeiNumber())).thenReturn(mockDeviceMaster);

            ResponseEntity<Object> actualResponse = deviceService.getDeviceInfoByImei(mockDeviceMaster.getImeiNumber(), null, null, null);

            assertNotNull(actualResponse);

            DeviceMaster actualDevice = (DeviceMaster) actualResponse.getBody();
            assertNotNull(actualDevice);

            assertEquals(mockDeviceMaster.getDeviceId(), actualDevice.getDeviceId());
            assertEquals(mockDeviceMaster.getIsBlocked(), actualDevice.getIsBlocked());
            assertEquals(mockDeviceMaster.getDeviceName(), actualDevice.getDeviceName());
            assertEquals(mockDeviceMaster.getDeviceModel(), actualDevice.getDeviceModel());
            assertEquals(mockDeviceMaster.getUserName(), actualDevice.getUserName());
            assertEquals(mockDeviceMaster.getImeiNumber(), actualDevice.getImeiNumber());
        }

        @Test
        void byMAC() {
            DeviceMaster mockDeviceMaster = getMockDeviceMaster();

            when(deviceRepository.getDeviceByMacAddress(mockDeviceMaster.getMacAddress())).thenReturn(mockDeviceMaster);

            ResponseEntity<Object> actualResponse = deviceService.getDeviceInfoByImei(null, mockDeviceMaster.getMacAddress(), null, null);

            assertNotNull(actualResponse);

            DeviceMaster actualDevice = (DeviceMaster) actualResponse.getBody();
            assertNotNull(actualDevice);

            assertEquals(mockDeviceMaster.getDeviceId(), actualDevice.getDeviceId());
            assertEquals(mockDeviceMaster.getIsBlocked(), actualDevice.getIsBlocked());
            assertEquals(mockDeviceMaster.getDeviceName(), actualDevice.getDeviceName());
            assertEquals(mockDeviceMaster.getDeviceModel(), actualDevice.getDeviceModel());
            assertEquals(mockDeviceMaster.getUserName(), actualDevice.getUserName());
            assertEquals(mockDeviceMaster.getImeiNumber(), actualDevice.getImeiNumber());
        }

        @Test
        void byUserId() {
            DeviceMaster mockDeviceMaster = getMockDeviceMaster();
            String userId = "mockUser";

            when(deviceRepository.getDeviceByuserId(userId)).thenReturn(mockDeviceMaster);

            ResponseEntity<Object> actualResponse = deviceService.getDeviceInfoByImei(null, null, userId, null);

            assertNotNull(actualResponse);

            DeviceMaster actualDevice = (DeviceMaster) actualResponse.getBody();
            assertNotNull(actualDevice);

            assertEquals(mockDeviceMaster.getDeviceId(), actualDevice.getDeviceId());
            assertEquals(mockDeviceMaster.getIsBlocked(), actualDevice.getIsBlocked());
            assertEquals(mockDeviceMaster.getDeviceName(), actualDevice.getDeviceName());
            assertEquals(mockDeviceMaster.getDeviceModel(), actualDevice.getDeviceModel());
            assertEquals(mockDeviceMaster.getUserName(), actualDevice.getUserName());
            assertEquals(mockDeviceMaster.getImeiNumber(), actualDevice.getImeiNumber());
        }

        @Test
        void byDeviceUUID() {
            DeviceMaster mockDeviceMaster = getMockDeviceMaster();

            when(deviceRepository.getDeviceByDeviceUUID(mockDeviceMaster.getDeviceUUID())).thenReturn(Optional.of(mockDeviceMaster));

            ResponseEntity<Object> actualResponse = deviceService.getDeviceInfoByImei(null, null, null, mockDeviceMaster.getDeviceUUID());

            assertNotNull(actualResponse);

            DeviceMaster actualDevice = (DeviceMaster) actualResponse.getBody();
            assertNotNull(actualDevice);

            assertEquals(mockDeviceMaster.getDeviceId(), actualDevice.getDeviceId());
            assertEquals(mockDeviceMaster.getIsBlocked(), actualDevice.getIsBlocked());
            assertEquals(mockDeviceMaster.getDeviceName(), actualDevice.getDeviceName());
            assertEquals(mockDeviceMaster.getDeviceModel(), actualDevice.getDeviceModel());
            assertEquals(mockDeviceMaster.getUserName(), actualDevice.getUserName());
            assertEquals(mockDeviceMaster.getImeiNumber(), actualDevice.getImeiNumber());
        }

        // @TODO: No context how this fn should behave with multiple parameters

        // Mock Data fn
        DeviceMaster getMockDeviceMaster() {
            DeviceMaster mockDeviceMaster = new DeviceMaster();
            mockDeviceMaster.setDeviceId(1);
            mockDeviceMaster.setIsBlocked(false);
            mockDeviceMaster.setDeviceName("Google Pixel 7A");
            mockDeviceMaster.setDeviceModel("GPixel7A");
            mockDeviceMaster.setUserName("username");
            mockDeviceMaster.setImeiNumber("119032112309123");
            mockDeviceMaster.setMacAddress("G7-89-0H-YZ-P9-Y7");
            mockDeviceMaster.setDeviceUUID("HJUA-AAAA-UUID-0909");
            return mockDeviceMaster;
        }

    }

    @Test
    void testGetAllDevice() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        DeviceMaster d1 = new DeviceMaster();
        d1.setDeviceId(1);
        d1.setIsBlocked(false);
        d1.setDeviceModel("GPixel7A");
        d1.setImeiNumber("IMEI1");
        d1.setLastLoggedInUser("U1");
        d1.setIgVersion("V1");

        DeviceMaster d2 = new DeviceMaster();
        d2.setDeviceId(2);
        d2.setIsBlocked(false);
        d2.setDeviceModel("IPHONE14Pro");
        d2.setImeiNumber("IMEI2");
        d2.setLastLoggedInUser("U2");
        d2.setIgVersion("V2");

        DeviceMaster d3 = new DeviceMaster();
        d3.setDeviceId(3);
        d3.setIsBlocked(true);
        d3.setDeviceModel("GPixel6A");
        d3.setImeiNumber("IMEI3");
        d3.setLastLoggedInUser("U1");
        d3.setIgVersion("V1");

        List<DeviceMaster> mockDeviceList = List.of(d1, d2, d3);
        when(userService.getUserDtoById(anyString())).thenAnswer(i -> {
            MultiLocationUserListDto multiLocationUserListDto = new MultiLocationUserListDto();
            multiLocationUserListDto.setId("U0");
            multiLocationUserListDto.setUserName("User 0");
            switch ((String) i.getArgument(0)) {
                case "U1":
                    multiLocationUserListDto.setId("U1");
                    multiLocationUserListDto.setUserName("User 1");
                    break;
                case "U2":
                    multiLocationUserListDto.setId("U2");
                    multiLocationUserListDto.setUserName("User 2");
                    break;
            }
            return multiLocationUserListDto;
        });
        when(deviceRepository.findAll()).thenReturn(mockDeviceList);

        ResponseEntity<Object> actualResponse = deviceService.getAllDevice(mockRequest);
        assertNotNull(actualResponse);
        List<DeviceWithUserDetails> actualDeviceWithUserDetailList = (List<DeviceWithUserDetails>) actualResponse.getBody();

        assertNotNull(actualDeviceWithUserDetailList);
        assertEquals(mockDeviceList.size(), actualDeviceWithUserDetailList.size());
        assertEquals(mockDeviceList.get(0).getDeviceId(), actualDeviceWithUserDetailList.get(0).getDeviceId());
        assertEquals(mockDeviceList.get(0).getImeiNumber(), actualDeviceWithUserDetailList.get(0).getImeiNumber());
        assertEquals(mockDeviceList.get(1).getIsBlocked(), actualDeviceWithUserDetailList.get(1).getIsBlocked());
        assertEquals(mockDeviceList.get(1).getLastLoggedInUser(), actualDeviceWithUserDetailList.get(1).getLastLoggedInUser());
        assertEquals(mockDeviceList.get(1).getLastLoggedInUser(), actualDeviceWithUserDetailList.get(1).getUsersResource().getId());
    }

    @Nested
    class testGetDevicePage {
        @BeforeEach
        void setUpGetDevicePage() throws IOException {
            List<DeviceMaster> deviceMasterList = getMockDeviceMasters();
            List<MultiLocationUserListDto> multiLocationUserListDtoList = getMockMultiLocationUserListDtos();
            when(userService.getUserDtoById(anyString()))
                    .thenAnswer(i -> {
                        String id = i.getArgument(0);
                        for (MultiLocationUserListDto multiLocationUserListDto : multiLocationUserListDtoList)
                            if (multiLocationUserListDto.getId().equals(id)) return multiLocationUserListDto;
                        return null;
                    });

            when(deviceRepository.count()).thenReturn((long) deviceMasterList.size());
            when(deviceRepository.findAll(any(Pageable.class))).thenAnswer(i -> {
                Pageable p = i.getArgument(0);
                int page = p.getPageNumber();
                int pageSize = p.getPageSize();
                Sort sort = p.getSort();
                Sort.Order order = sort.get().collect(Collectors.toList()).get(0);
                Sort.Direction direction = order.getDirection();
                String sortColumn = order.getProperty();
//                String sortColumn = "get" + order.getProperty().substring(0, 1).toUpperCase() + order.getProperty().substring(1);
                try {
                    BeanUtils.getProperty(new DeviceMaster(), sortColumn);
                } catch (Exception ex) {
                    return null;
                }
                List<DeviceMaster> filteredList = deviceMasterList
                        .stream()
                        .sorted(Comparator.comparing(e -> {
                            try {
                                return BeanUtils.getProperty(e, sortColumn);
                            } catch (Exception ex) {
                                return e.getDeviceId().toString();
                            }
                        })).collect(Collectors.toList());
                List<DeviceMaster> sortedAndPaginatedList = new ArrayList<>();

                int startIndex = Math.max(-1, (page - 1) * pageSize);
                int endIndex = Math.min(filteredList.size() - 1, startIndex + pageSize - 1);
                if(startIndex < 0 || endIndex >= filteredList.size()) return new ArrayList<>();
                if(direction.isDescending()) {
                    while(startIndex <= endIndex) {
                        sortedAndPaginatedList.add(filteredList.get(endIndex));
                        --endIndex;
                    }
                } else {
                    while(startIndex <= endIndex) {
                        sortedAndPaginatedList.add(filteredList.get(startIndex));
                        ++startIndex;
                    }
                }
                return new PageImpl<>(sortedAndPaginatedList);
            });

            when(deviceRepository
                    .findByAndroidVersionContainingIgnoreCaseOrDeviceNameContainingIgnoreCaseOrDeviceOsContainingIgnoreCaseOrDeviceModelContainingIgnoreCaseOrDeviceUUIDContainingIgnoreCaseOrUserNameContainingIgnoreCase(
                            anyString(),
                            anyString(),
                            anyString(),
                            anyString(),
                            anyString(),
                            anyString()
                    )
            ).thenAnswer(i -> {
                String search = ((String) i.getArgument(0)).toLowerCase();
                return deviceMasterList.stream().filter(deviceMaster -> {
                    if(deviceMaster.getAndroidVersion() != null && deviceMaster.getAndroidVersion().toLowerCase().contains(search)) return true;
                    if(deviceMaster.getDeviceName() != null && deviceMaster.getDeviceName().toLowerCase().contains(search)) return true;
                    if(deviceMaster.getDeviceOs() != null && deviceMaster.getDeviceOs().toLowerCase().contains(search)) return true;
                    if(deviceMaster.getDeviceModel() != null && deviceMaster.getDeviceModel().toLowerCase().contains(search)) return true;
                    if(deviceMaster.getDeviceUUID() != null && deviceMaster.getDeviceUUID().toLowerCase().contains(search)) return true;
                    return deviceMaster.getUserName() != null && deviceMaster.getUserName().toLowerCase().contains(search);
                }).collect(Collectors.toList());
            });

            when(deviceRepository
                    .findByAndroidVersionContainingIgnoreCaseOrDeviceNameContainingIgnoreCaseOrDeviceOsContainingIgnoreCaseOrDeviceModelContainingIgnoreCaseOrDeviceUUIDContainingIgnoreCaseOrUserNameContainingIgnoreCase(
                            anyString(),
                            anyString(),
                            anyString(),
                            anyString(),
                            anyString(),
                            anyString(),
                            any(Pageable.class)
                    )
            ).thenAnswer(i -> {
                String search = ((String) i.getArgument(0)).toLowerCase();
                Pageable p = i.getArgument(6);
                int page = p.getPageNumber();
                int pageSize = p.getPageSize();
                Sort sort = p.getSort();
                Sort.Order order = sort.get().collect(Collectors.toList()).get(0);
                Sort.Direction direction = order.getDirection();
                String sortColumn = order.getProperty();
                try {
                    BeanUtils.getProperty(new DeviceMaster(), sortColumn);
                } catch (Exception ex) {
                    return null;
                }
                List<DeviceMaster> filteredList = deviceMasterList
                        .stream()
                        .filter(deviceMaster -> {
                            if(deviceMaster.getAndroidVersion() != null && deviceMaster.getAndroidVersion().toLowerCase().contains(search)) return true;
                            if(deviceMaster.getDeviceName() != null && deviceMaster.getDeviceName().toLowerCase().contains(search)) return true;
                            if(deviceMaster.getDeviceOs() != null && deviceMaster.getDeviceOs().toLowerCase().contains(search)) return true;
                            if(deviceMaster.getDeviceModel() != null && deviceMaster.getDeviceModel().toLowerCase().contains(search)) return true;
                            if(deviceMaster.getDeviceUUID() != null && deviceMaster.getDeviceUUID().toLowerCase().contains(search)) return true;
                            return deviceMaster.getUserName() != null && deviceMaster.getUserName().toLowerCase().contains(search);
                        })
                        .sorted(Comparator.comparing(e -> {
                            try {
                                return BeanUtils.getProperty(e, sortColumn);
                            } catch (Exception ex) {
                                return e.getDeviceId().toString();
                            }
                        })).collect(Collectors.toList());
                List<DeviceMaster> sortedAndPaginatedList = new ArrayList<>();

                int startIndex = Math.max(-1, (page - 1) * pageSize);
                int endIndex = Math.min(filteredList.size(), startIndex + pageSize - 1);
                if(startIndex < 0 || endIndex >= filteredList.size()) return new ArrayList<>();
                if(direction.isDescending()) {
                    while(startIndex <= endIndex) {
                        sortedAndPaginatedList.add(filteredList.get(endIndex));
                        --endIndex;
                    }
                } else {
                    while(startIndex <= endIndex) {
                        sortedAndPaginatedList.add(filteredList.get(startIndex));
                        ++startIndex;
                    }
                }
                return new PageImpl<>(sortedAndPaginatedList);
            });
        }

        @Test
        void noParams() {
            HttpServletRequest mockRequest = mock(HttpServletRequest.class);
            Integer pageNo = 1;
            String orderBy = "null";
            String orderCase = CommonConstant.DESC;
            String search = "";
            ResponseEntity<Object> actualResponse = deviceService.getDevicePage(mockRequest, pageNo, orderBy, orderCase, search);
            assertNotNull(actualResponse);

            PageDto actualPage = (PageDto) actualResponse.getBody();
            assertNotNull(actualPage);
            assertEquals(55, actualPage.getTotalCount());
            assertEquals(CommonConstant.PAGE_SIZE, actualPage.getList().size());
        }

        @Test
        void noParamAndPage6() {
            HttpServletRequest mockRequest = mock(HttpServletRequest.class);
            Integer pageNo = 6;
            String orderBy = "null";
            String orderCase = CommonConstant.DESC;
            String search = "";
            ResponseEntity<Object> actualResponse = deviceService.getDevicePage(mockRequest, pageNo, orderBy, orderCase, search);
            assertNotNull(actualResponse);

            PageDto actualPage = (PageDto) actualResponse.getBody();
            assertNotNull(actualPage);
            assertEquals(55, actualPage.getTotalCount());
            assertEquals(5, actualPage.getList().size());
        }

        @Test
        void searchAndOrderByDeviceName() {
            HttpServletRequest mockRequest = mock(HttpServletRequest.class);
            Integer pageNo = 1;
            String orderBy = "deviceId";
            String orderCase = CommonConstant.ASC;
            String search = "samsung";
            ResponseEntity<Object> actualResponse = deviceService.getDevicePage(mockRequest, pageNo, orderBy, orderCase, search);
            assertNotNull(actualResponse);

            PageDto actualPage = (PageDto) actualResponse.getBody();
            assertNotNull(actualPage);
            assertEquals(22, actualPage.getTotalCount());
            assertEquals(10, actualPage.getList().size());
        }
    }

    // Mock data fn
    List<DeviceMaster> getMockDeviceMasters() throws IOException {
        File file = new File("src/test/resources/mockdata/device/mockDeviceMasters.json");
        InputStream fileInputStream = new FileInputStream(file);
        return objectMapper.readValue(fileInputStream, new TypeReference<List<DeviceMaster>>() {
        });
    }

    List<MultiLocationUserListDto> getMockMultiLocationUserListDtos() throws IOException {
        File file = new File("src/test/resources/mockdata/device/mockMultiLocationUserListDtos.json");
        InputStream fileInputStream = new FileInputStream(file);
        return objectMapper.readValue(fileInputStream, new TypeReference<List<MultiLocationUserListDto>>() {
        });
    }
}