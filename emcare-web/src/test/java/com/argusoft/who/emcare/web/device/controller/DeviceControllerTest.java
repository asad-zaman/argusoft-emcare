package com.argusoft.who.emcare.web.device.controller;

import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.device.dto.DeviceDto;
import com.argusoft.who.emcare.web.device.dto.DeviceWithUserDetails;
import com.argusoft.who.emcare.web.device.mapper.DeviceMapper;
import com.argusoft.who.emcare.web.device.model.DeviceMaster;
import com.argusoft.who.emcare.web.device.service.DeviceService;
import org.apache.commons.beanutils.BeanUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {DeviceController.class})
@RunWith(MockitoJUnitRunner.class)
class DeviceControllerTest {

    @Mock
    DeviceService deviceService;

    @InjectMocks
    private DeviceController deviceController;

    ObjectMapper objectMapper = new ObjectMapper();

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
    void testAddNewDevice() throws Exception {
        DeviceDto mockDevice = new DeviceDto();
        mockDevice.setDeviceName("Google Pixel 7A");
        mockDevice.setImeiNumber("4567894596879874");
        mockDevice.setIsBlocked(false);
        mockDevice.setAndroidVersion("13.0");
        Integer newDeviceId = 1;
        String userId = "user_id";
        String userName = "awesome_user";

        when(deviceService.addNewDevice(any(DeviceDto.class))).thenAnswer(i -> {
            DeviceMaster d = DeviceMapper.getDeviceMatserFromDto(i.getArgument(0), userId, userName);
            d.setDeviceId(newDeviceId);
            return ResponseEntity.ok().body(d);
        });

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/device/add")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockDevice));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(deviceController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        DeviceMaster actualDevice = objectMapper.readValue(response, DeviceMaster.class);

        assertNotNull(actualDevice);
        assertEquals(newDeviceId, actualDevice.getDeviceId());
        assertEquals(userName, actualDevice.getUserName());
        assertEquals(userId, actualDevice.getLastLoggedInUser());
        assertEquals(mockDevice.getImeiNumber(), actualDevice.getImeiNumber());
        assertEquals(mockDevice.getDeviceName(), actualDevice.getDeviceName());
        assertEquals(mockDevice.getAndroidVersion(), actualDevice.getAndroidVersion());
        assertEquals(mockDevice.getIsBlocked(), actualDevice.getIsBlocked());
    }

    @Nested
    class testUpdateDeviceDetails {
        String sessionUserId = "sessionUserId";
        String sessionUserName = "sessionUserName";
        @BeforeEach
        void setUpUpdateDeviceDetails () {
            when(deviceService.updateDeviceDetails(any(DeviceDto.class))).thenAnswer(i -> {
                DeviceDto d = i.getArgument(0);
                if(d.getDeviceId() == -1) return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
                DeviceMaster dm = DeviceMapper.getDeviceMatserFromDto(i.getArgument(0), sessionUserId, sessionUserName);
                dm.setDeviceId(d.getDeviceId());
                return ResponseEntity.ok().body(dm);
            });
        }
        @Test
        void existingDevice() throws Exception {
            DeviceDto mockDevice = new DeviceDto();
            mockDevice.setDeviceId(1);
            mockDevice.setDeviceName("Google Pixel 7A");
            mockDevice.setImeiNumber("4567894596879874");
            mockDevice.setIsBlocked(false);
            mockDevice.setAndroidVersion("13.0");

            RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/device/update")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mockDevice));

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(deviceController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);

            DeviceMaster actualDevice = objectMapper.readValue(response, DeviceMaster.class);

            assertNotNull(actualDevice);
            assertEquals(mockDevice.getDeviceId(), actualDevice.getDeviceId());
            assertEquals(sessionUserName, actualDevice.getUserName());
            assertEquals(sessionUserId, actualDevice.getLastLoggedInUser());
            assertEquals(mockDevice.getImeiNumber(), actualDevice.getImeiNumber());
            assertEquals(mockDevice.getDeviceName(), actualDevice.getDeviceName());
            assertEquals(mockDevice.getAndroidVersion(), actualDevice.getAndroidVersion());
            assertEquals(mockDevice.getIsBlocked(), actualDevice.getIsBlocked());
        }

        @Test
        void nonExisting() throws Exception {
            DeviceDto mockDevice = new DeviceDto();
            mockDevice.setDeviceId(-1);
            mockDevice.setDeviceName("Google Pixel 7A");
            mockDevice.setImeiNumber("4567894596879874");
            mockDevice.setIsBlocked(false);
            mockDevice.setAndroidVersion("13.0");

            RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/device/update")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mockDevice));

            MockMvcBuilders.standaloneSetup(deviceController).build().perform(requestBuilder).andExpect(status().isNoContent());
        }
    }

    @Test
    void testGetAllDevice() throws Exception {
        List<DeviceWithUserDetails> mockDeviceList = List.of(
                new DeviceWithUserDetails(),
                new DeviceWithUserDetails(),
                new DeviceWithUserDetails()
        );

        when(deviceService.getAllDevice(any(HttpServletRequest.class))).thenReturn(ResponseEntity.ok(mockDeviceList));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/device/all")
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(deviceController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        List<DeviceWithUserDetails> actualDeviceList = objectMapper.readValue(response, new TypeReference<List<DeviceWithUserDetails>>() {});

        assertNotNull(actualDeviceList);
        assertEquals(mockDeviceList.size(), actualDeviceList.size());
    }

    @Nested
    class testGetDevicePage {
        double maxRows = 10000;

        @BeforeEach
        void setUpGetDevicePage () {
            when(
                    deviceService.getDevicePage(
                            any(HttpServletRequest.class),
                            anyInt(),
                            anyString(),
                            anyString(),
                            any()
                    )
            )
                    .thenAnswer(i -> {
                        Integer pageNo = i.getArgument(1);
                        String orderBy = i.getArgument(2);
                        String order = i.getArgument(3);
                        String searchString = i.getArgument(4);
                        try {
                            BeanUtils.getProperty(new DeviceMaster(), orderBy);
                        } catch (Exception e) {
                            return null;
                        }
                        PageDto p = new PageDto();
                        double ceil = searchString != null ? Math.ceil(maxRows / (searchString.length() + 1)) : maxRows;
                        p.setTotalCount((long) ceil);
                        p.setList(List.of(pageNo.toString(), orderBy, order));
                        return ResponseEntity.ok(p);
                    });

        }
        @Test
        void noParams() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/device/page")
                    .accept(MediaType.APPLICATION_JSON);

            MockMvcBuilders.standaloneSetup(deviceController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
        }

        @Test
        void pageNumberAndInvalidOrderColumnNoSearch() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/device/page")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("pageNo", "2")
                    .param("order", "ASC")
                    .param("orderBy", "invalid_column");

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(deviceController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertThat(response).isNullOrEmpty();
        }

        @Test
        void pageNumberAndNoSearch() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/device/page")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("pageNo", "1")
                    .param("order", "ASC")
                    .param("orderBy", "deviceUUID");

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(deviceController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);

            PageDto actualPage = objectMapper.readValue(response, PageDto.class);

            assertNotNull(actualPage);
            assertEquals((long) Math.ceil(maxRows / ("".length() + 1)), actualPage.getTotalCount());
            assertEquals(3, actualPage.getList().size());
            assertEquals("1", actualPage.getList().get(0));
            assertEquals("deviceUUID", actualPage.getList().get(1));
            assertEquals("ASC", actualPage.getList().get(2));
        }

        @Test
        void searchAndOrder() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/device/page")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("pageNo", "1")
                    .param("order", "DESC")
                    .param("orderBy", "igVersion")
                    .param("search", "input");

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(deviceController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);

            PageDto actualPage = objectMapper.readValue(response, PageDto.class);

            assertNotNull(actualPage);
            assertEquals((long) Math.ceil(maxRows / ("input".length() + 1)), actualPage.getTotalCount());
            assertEquals(3, actualPage.getList().size());
            assertEquals("1", actualPage.getList().get(0));
            assertEquals("igVersion", actualPage.getList().get(1));
            assertEquals("DESC", actualPage.getList().get(2));
        }
    }

    @Nested
    class testChangeDeviceStatus {
        @Test
        void validPath() throws Exception {
            DeviceMaster mockDevice = new DeviceMaster();
            mockDevice.setDeviceId(1);
            mockDevice.setIsBlocked(true);

            when(deviceService.changeDeviceStatus(1, true))
                    .thenReturn(ResponseEntity.ok(mockDevice));

            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/device/status/1/true")
                    .accept(MediaType.APPLICATION_JSON);

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(deviceController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);

            DeviceMaster actualDevice = objectMapper.readValue(response, DeviceMaster.class);

            assertNotNull(actualDevice);
            assertEquals(1, actualDevice.getDeviceId());
            assertEquals(true, actualDevice.getIsBlocked());
        }

        @Test
        void notExistingId() throws Exception {
            DeviceMaster mockDevice = new DeviceMaster();
            mockDevice.setDeviceId(1);
            mockDevice.setIsBlocked(true);

            when(deviceService.changeDeviceStatus(1, true))
                    .thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).body(null));

            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/device/status/1/true")
                    .accept(MediaType.APPLICATION_JSON);

            MockMvcBuilders.standaloneSetup(deviceController).build().perform(requestBuilder).andExpect(status().isNoContent());
        }

        @Test
        void invalidPath() throws Exception {
            DeviceMaster mockDevice = new DeviceMaster();
            mockDevice.setDeviceId(1);
            mockDevice.setIsBlocked(true);

            when(deviceService.changeDeviceStatus(1, true))
                    .thenReturn(ResponseEntity.ok(mockDevice));

            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/device/status/1/test")
                    .accept(MediaType.APPLICATION_JSON);

            MockMvcBuilders.standaloneSetup(deviceController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
        }
    }

    @Nested
    class testGetDeviceByImei {
        DeviceMaster mockDevice;

        @BeforeEach
        void setUpGetDeviceByImei () {
            mockDevice = new DeviceMaster();
            mockDevice.setDeviceUUID("DEVI-CEUU-ID00");
            mockDevice.setImeiNumber("90190922124221");
            mockDevice.setMacAddress("AA-BB-CC-DD-EE");
            mockDevice.setLastLoggedInUser("lastUser");

            when(deviceService.getDeviceInfoByImei(any(), any(), any(), any()))
                    .thenAnswer(i -> {
                        String imei = i.getArgument(0);
                        String macAddress = i.getArgument(1);
                        String userId = i.getArgument(2);
                        String deviceUUID = i.getArgument(3);
                        if(imei != null && imei.equals(mockDevice.getImeiNumber())) return ResponseEntity.ok(mockDevice);
                        if(macAddress != null && macAddress.equals(mockDevice.getMacAddress())) return ResponseEntity.ok(mockDevice);
                        if(deviceUUID != null && deviceUUID.equals(mockDevice.getDeviceUUID())) return ResponseEntity.ok(mockDevice);
                        if(userId != null && userId.equals(mockDevice.getLastLoggedInUser())) return ResponseEntity.ok(mockDevice);
                        return ResponseEntity.ok(new DeviceMaster());
                    });
        }

        @Test
        void noParams() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/device")
                    .accept(MediaType.APPLICATION_JSON);

            MockMvcBuilders.standaloneSetup(deviceController).build().perform(requestBuilder).andExpect(status().is2xxSuccessful());

            // @TODO: Should be Bad Request
//            MockMvcBuilders.standaloneSetup(deviceController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
        }

        @Test
        void onlyImei() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/device")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("imei", mockDevice.getImeiNumber());

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(deviceController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);

            DeviceMaster actualDevice = objectMapper.readValue(response, DeviceMaster.class);

            assertNotNull(actualDevice);
            assertEquals(mockDevice.getImeiNumber(), actualDevice.getImeiNumber());
            assertEquals(mockDevice.getMacAddress(), actualDevice.getMacAddress());
            assertEquals(mockDevice.getDeviceUUID(), actualDevice.getDeviceUUID());
            assertEquals(mockDevice.getLastLoggedInUser(), actualDevice.getLastLoggedInUser());
        }

        @Test
        void onlyMAC() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/device")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("macAddress", mockDevice.getMacAddress());

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(deviceController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);

            DeviceMaster actualDevice = objectMapper.readValue(response, DeviceMaster.class);

            assertNotNull(actualDevice);
            assertEquals(mockDevice.getImeiNumber(), actualDevice.getImeiNumber());
            assertEquals(mockDevice.getMacAddress(), actualDevice.getMacAddress());
            assertEquals(mockDevice.getDeviceUUID(), actualDevice.getDeviceUUID());
            assertEquals(mockDevice.getLastLoggedInUser(), actualDevice.getLastLoggedInUser());
        }

        @Test
        void onlyDeviceUUID() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/device")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("deviceUUID", mockDevice.getDeviceUUID());

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(deviceController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);

            DeviceMaster actualDevice = objectMapper.readValue(response, DeviceMaster.class);

            assertNotNull(actualDevice);
            assertEquals(mockDevice.getImeiNumber(), actualDevice.getImeiNumber());
            assertEquals(mockDevice.getMacAddress(), actualDevice.getMacAddress());
            assertEquals(mockDevice.getDeviceUUID(), actualDevice.getDeviceUUID());
            assertEquals(mockDevice.getLastLoggedInUser(), actualDevice.getLastLoggedInUser());
        }

        @Test
        void onlyUserId() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/device")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("userId", mockDevice.getLastLoggedInUser());

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(deviceController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);

            DeviceMaster actualDevice = objectMapper.readValue(response, DeviceMaster.class);

            assertNotNull(actualDevice);
            assertEquals(mockDevice.getImeiNumber(), actualDevice.getImeiNumber());
            assertEquals(mockDevice.getMacAddress(), actualDevice.getMacAddress());
            assertEquals(mockDevice.getDeviceUUID(), actualDevice.getDeviceUUID());
            assertEquals(mockDevice.getLastLoggedInUser(), actualDevice.getLastLoggedInUser());
        }

        @Test
        void multipleParams() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/device")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("imei", mockDevice.getImeiNumber())
                    .param("macAddress", mockDevice.getMacAddress())
                    .param("deviceUUID", mockDevice.getDeviceUUID())
                    .param("userId", mockDevice.getDeviceUUID());

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(deviceController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);

            DeviceMaster actualDevice = objectMapper.readValue(response, DeviceMaster.class);

            assertNotNull(actualDevice);
            assertEquals(mockDevice.getImeiNumber(), actualDevice.getImeiNumber());
            assertEquals(mockDevice.getMacAddress(), actualDevice.getMacAddress());
            assertEquals(mockDevice.getDeviceUUID(), actualDevice.getDeviceUUID());
            assertEquals(mockDevice.getLastLoggedInUser(), actualDevice.getLastLoggedInUser());
        }
    }
}