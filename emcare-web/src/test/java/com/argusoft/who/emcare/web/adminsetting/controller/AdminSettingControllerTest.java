package com.argusoft.who.emcare.web.adminsetting.controller;

import com.argusoft.who.emcare.web.adminsetting.dto.SettingDto;
import com.argusoft.who.emcare.web.adminsetting.entity.Settings;
import com.argusoft.who.emcare.web.adminsetting.service.AdminSettingService;
import com.argusoft.who.emcare.web.mail.entity.EmailContent;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {AdminSettingController.class})
@RunWith(MockitoJUnitRunner.class)
class AdminSettingControllerTest {

    @Mock
    AdminSettingService adminSettingService;

    @InjectMocks
    private AdminSettingController adminSettingController;

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
    void testGetAdminPanelSetting() throws Exception {
        List<Settings> mockSettingsList = List.of(
                mockSettingsBuilder(1, "n1", "k1", "v1"),
                mockSettingsBuilder(2, "n2", "k2", "v2"),
                mockSettingsBuilder(3, "n3", "k3", "v3")
        );

        when(adminSettingService.getAdminSetting()).thenReturn(mockSettingsList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/admin/setting").accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(adminSettingController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        List<Settings> actualSettingsList = objectMapper.readValue(response, new TypeReference<List<Settings>>(){});

        assertNotNull(actualSettingsList);
        assertEquals(mockSettingsList.size(), actualSettingsList.size());
    }

    @Test
    void testUpdateAdminPanelSetting() throws Exception {
        SettingDto mockSettingDto = new SettingDto();
        mockSettingDto.setId(1L);
        mockSettingDto.setName("N");
        mockSettingDto.setKey("K");
        mockSettingDto.setValue("V");

        when(adminSettingService.updateAdminSettings(any(SettingDto.class)))
                .thenAnswer(i -> {
                    SettingDto settingDto = (SettingDto) i.getArgument(0);
                    return mockSettingsBuilder(settingDto.getId().intValue(), settingDto.getName(), settingDto.getKey(), settingDto.getValue());
                });

        String mockContent = objectMapper.writeValueAsString(mockSettingDto);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/admin/update")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mockContent);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(adminSettingController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        Settings actualSettings = objectMapper.readValue(response, Settings.class);

        assertNotNull(actualSettings);
        assertEquals(mockSettingDto.getId(), actualSettings.getId());
        assertEquals(mockSettingDto.getName(), actualSettings.getName());
        assertEquals(mockSettingDto.getKey(), actualSettings.getKey());
        assertEquals(mockSettingDto.getValue(), actualSettings.getValue());
    }

    @Test
    void getAllMailTemplate() throws Exception {
        EmailContent e1 = new EmailContent();
        e1.setId(1L);
        e1.setCode("C1");
        e1.setContent("C1");
        e1.setSubject("S1");
        e1.setVarList("V1");

        EmailContent e2 = new EmailContent();
        e1.setId(2L);
        e1.setCode("C2");
        e1.setContent("C2");
        e1.setSubject("S2");
        e1.setVarList("V2");

        List<EmailContent> mockEmailContentList = List.of(e1, e2);

        when(adminSettingService.getAllMailTemplate())
                .thenReturn(mockEmailContentList);

        String mockContent = objectMapper.writeValueAsString(mockEmailContentList);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/admin/mail/template")
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(adminSettingController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        List<EmailContent> actualEmailContentList = objectMapper.readValue(response, new TypeReference<List<EmailContent>>() {});

        assertNotNull(actualEmailContentList);
        assertEquals(mockEmailContentList.size(), actualEmailContentList.size());
        assertEquals(mockEmailContentList.get(0).getId(), actualEmailContentList.get(0).getId());
        assertEquals(mockEmailContentList.get(0).getCode(), actualEmailContentList.get(0).getCode());
        assertEquals(mockEmailContentList.get(0).getContent(), actualEmailContentList.get(0).getContent());
        assertEquals(mockEmailContentList.get(0).getSubject(), actualEmailContentList.get(0).getSubject());
        assertEquals(mockEmailContentList.get(0).getVarList(), actualEmailContentList.get(0).getVarList());
    }

    Settings mockSettingsBuilder(Integer id, String name, String key, String value) {
        Settings settings = new Settings();
        settings.setId(id == null ? 1L : id.longValue());
        settings.setName(name == null ? "name" : name);
        settings.setKey(key == null ? "key" : key);
        settings.setValue(value == null ? "value" : value);
        return  settings;
    }
}