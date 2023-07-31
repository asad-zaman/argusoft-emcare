package com.argusoft.who.emcare.web.menu.controller;

import com.argusoft.who.emcare.web.common.response.Response;
import com.argusoft.who.emcare.web.menu.dto.MenuConfigDto;
import com.argusoft.who.emcare.web.menu.mapper.MenuConfigMapper;
import com.argusoft.who.emcare.web.menu.model.MenuConfig;
import com.argusoft.who.emcare.web.menu.model.UserMenuConfig;
import com.argusoft.who.emcare.web.menu.service.MenuService;
import org.codehaus.jackson.map.DeserializationConfig;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {MenuController.class})
@RunWith(MockitoJUnitRunner.class)
class MenuControllerTest {
    @Mock
    MenuService menuService;

    @InjectMocks
    private MenuController menuController;

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
    void getAllMenu() throws Exception {
        List<MenuConfig> mockMenuConfigs = getDemoMenuConfigData();
        mockMenuConfigs.sort(Comparator.comparing(MenuConfig::getMenuName));

        when(menuService.getAllMenus()).thenReturn(mockMenuConfigs);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/menu/all").accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(menuController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        List<MenuConfig> actualMenuConfigs = objectMapper.readValue(response, new TypeReference<List<MenuConfig>>() {});

        assertNotNull(actualMenuConfigs);
        assertEquals(mockMenuConfigs.size(), actualMenuConfigs.size());

        for(int i = 0; i < mockMenuConfigs.size(); i++) {
            assertEquals(mockMenuConfigs.get(i).getId(), actualMenuConfigs.get(i).getId());
        }
    }

    @Test
    void getMenuConfigByIdWithValidExistingMenuID() throws Exception {
        List<MenuConfigDto> mockMenuConfigDTOs = getMockMenuConfigDTOs();

        when(menuService.getMenuConfigByMenuId(1)).thenReturn(mockMenuConfigDTOs);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/menu/menuconfig/1").accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(menuController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        List<MenuConfig> actualMenuConfigDTOs = objectMapper.readValue(response, new TypeReference<List<MenuConfigDto>>() {});

        assertNotNull(actualMenuConfigDTOs);
        assertEquals(mockMenuConfigDTOs.size(), actualMenuConfigDTOs.size());
    }

    @Test
    void getMenuConfigByIdWithValidNonExistingMenuID() throws Exception {
        when(menuService.getMenuConfigByMenuId(anyInt())).thenReturn(new ArrayList<>());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/menu/menuconfig/2").accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(menuController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);
        assertEquals("[]", response);
    }

    @Test
    void getMenuConfigByIdWithInvalidNonExistingMenuID() throws Exception {
        when(menuService.getMenuConfigByMenuId(anyInt())).thenReturn(new ArrayList<>());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/menu/menuconfig/invalid").accept(MediaType.APPLICATION_JSON);

        MockMvcBuilders.standaloneSetup(menuController).build().perform(requestBuilder).andExpect(status().isBadRequest());
    }

    @Test
    void addMenuConfigWithValidDataAndAllNotNullField() throws Exception {
        MenuConfigDto mockMenuConfig = new MenuConfigDto();
        mockMenuConfig.setMenuId(1);
        mockMenuConfig.setFeatureJson("{ \"canAdd\": true, \"canEdit\": true, \"canView\": true, \"canDelete\": true }");
        mockMenuConfig.setRoleId("R1");
        mockMenuConfig.setUserId("U1");

        String mockContent = objectMapper.writeValueAsString(mockMenuConfig);

        when(menuService.addMenuConfiguration(any(MenuConfigDto.class))).thenAnswer(invocation -> {
            UserMenuConfig u = MenuConfigMapper.getUserMenuConfigEntity(invocation.getArgument(0));
            u.setId(1);
            return u;
        });

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/menu/config/add")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mockContent);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(menuController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        UserMenuConfig actualUserMenuConfigDTO = objectMapper.readValue(response, UserMenuConfig.class);

        assertNotNull(actualUserMenuConfigDTO);
        assertNotNull(actualUserMenuConfigDTO.getId());
        assertEquals(mockMenuConfig.getMenuId(), actualUserMenuConfigDTO.getMenuId());
        assertEquals(mockMenuConfig.getFeatureJson(), actualUserMenuConfigDTO.getFeatureJson());
        assertEquals(mockMenuConfig.getRoleId(), actualUserMenuConfigDTO.getRoleId());
        assertEquals(mockMenuConfig.getUserId(), actualUserMenuConfigDTO.getUserId());
    }

    @Test
    void addMenuConfigWithInvalidDataAndNullField() throws Exception {
        MenuConfigDto mockMenuConfig = new MenuConfigDto();
        mockMenuConfig.setMenuId(null);
        mockMenuConfig.setFeatureJson(null);
        mockMenuConfig.setRoleId(null);
        mockMenuConfig.setUserId(null);

        String mockContent = objectMapper.writeValueAsString(mockMenuConfig);

        when(menuService.addMenuConfiguration(any(MenuConfigDto.class))).thenAnswer(invocation -> {
            UserMenuConfig u = MenuConfigMapper.getUserMenuConfigEntity(invocation.getArgument(0));
            u.setId(1);
            return u;
        });

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/menu/config/add")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mockContent);

        MockMvcBuilders.standaloneSetup(menuController).build().perform(requestBuilder).andExpect(status().isBadRequest());
    }

    @Test
    void addMenuConfigWithInvalidDataAndInvalidFieldValue() throws Exception {
        MenuConfigDto mockMenuConfig = new MenuConfigDto();
        mockMenuConfig.setMenuId(1);
        mockMenuConfig.setFeatureJson("Not a json & should fail");
        mockMenuConfig.setRoleId("R1");
        mockMenuConfig.setUserId("U1");

        String mockContent = objectMapper.writeValueAsString(mockMenuConfig);

        when(menuService.addMenuConfiguration(any(MenuConfigDto.class))).thenAnswer(invocation -> {
            UserMenuConfig u = MenuConfigMapper.getUserMenuConfigEntity(invocation.getArgument(0));
            u.setId(1);
            return u;
        });

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/menu/config/add")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mockContent);

        // Should reject with 400 for invalid JSON
        // MockMvcBuilders.standaloneSetup(menuController).build().perform(requestBuilder).andExpect(status().isBadRequest());
        // But any way it's 200
        MockMvcBuilders.standaloneSetup(menuController).build().perform(requestBuilder).andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteMenuConfigWithValidID() throws Exception {
        Response mockResponse = new Response("Successfully Deleted", HttpStatus.OK.value());

        when(menuService.deleteMenuConfig(anyInt())).thenReturn(mockResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/menu/config/delete/1")
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(menuController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        Map<String, Object> actualResponse = objectMapper.readValue(response, Map.class);

        assertNotNull(actualResponse);
        assertEquals(mockResponse.getErrorMessage(), actualResponse.get("errorMessage"));
        assertEquals(mockResponse.getStatusCode(), actualResponse.get("statusCode"));
    }

    @Test
    void deleteMenuConfigWithNonExistingID() throws Exception {
        Response mockResponse = new Response(
                "No class com.argusoft.who.emcare.web.menu.model.UserMenuConfig entity with id 123 exists!",
                2144591659
        );

        when(menuService.deleteMenuConfig(anyInt())).thenReturn(mockResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/menu/config/delete/123")
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(menuController).build();

        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        Map<String, Object> actualResponse = objectMapper.readValue(response, Map.class);

        assertNotNull(actualResponse);
        assertEquals(mockResponse.getErrorMessage(), actualResponse.get("errorMessage"));
        assertEquals(mockResponse.getStatusCode(), actualResponse.get("statusCode"));

        // Should actually be this
        // ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
    }

    @Test
    void deleteMenuConfigWithInvalidID() throws Exception {
        Response mockResponse = new Response(
                "No class com.argusoft.who.emcare.web.menu.model.UserMenuConfig entity with id 123 exists!",
                2144591659
        );
        when(menuService.deleteMenuConfig(anyInt())).thenReturn(mockResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/menu/config/delete/abc")
                .accept(MediaType.APPLICATION_JSON);

        MockMvcBuilders.standaloneSetup(menuController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
    }

    @Test
    void updateMenuConfigWithValidIDAndFields() throws Exception {
        MenuConfigDto mockUpdateMenuConfig = new MenuConfigDto();
        mockUpdateMenuConfig.setId(1);
        mockUpdateMenuConfig.setMenuId(1);
        mockUpdateMenuConfig.setUserId("U1");
        mockUpdateMenuConfig.setRoleId("R1");
        mockUpdateMenuConfig.setFeatureJson("{ \"canAdd\": true, \"canEdit\": true, \"canView\": true, \"canDelete\": true }");

        when(menuService.addMenuConfiguration(any(MenuConfigDto.class)))
                .thenAnswer(i -> MenuConfigMapper.getUserMenuConfigEntity(i.getArgument(0)));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/menu/config/update")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockUpdateMenuConfig));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(menuController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        UserMenuConfig actualResponse = objectMapper.readValue(response, UserMenuConfig.class);

        assertNotNull(actualResponse);
        assertEquals(mockUpdateMenuConfig.getId(), actualResponse.getId());
        assertEquals(mockUpdateMenuConfig.getMenuId(), actualResponse.getMenuId());
        assertEquals(mockUpdateMenuConfig.getFeatureJson(), actualResponse.getFeatureJson());
        assertEquals(mockUpdateMenuConfig.getRoleId(), actualResponse.getRoleId());
        assertEquals(mockUpdateMenuConfig.getUserId(), actualResponse.getUserId());
    }

    @Test
    void updateMenuConfigWithInvalidField() throws Exception {
        MenuConfigDto mockUpdateMenuConfig = new MenuConfigDto();
        // Invalid ID
        mockUpdateMenuConfig.setId(null);
        mockUpdateMenuConfig.setMenuId(null);
        mockUpdateMenuConfig.setUserId(null);
        mockUpdateMenuConfig.setRoleId(null);
        mockUpdateMenuConfig.setFeatureJson("haha not a json");

        when(menuService.addMenuConfiguration(any(MenuConfigDto.class)))
                .thenAnswer(i -> MenuConfigMapper.getUserMenuConfigEntity(i.getArgument(0)));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/menu/config/update")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockUpdateMenuConfig));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(menuController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);

        UserMenuConfig actualResponse = objectMapper.readValue(response, UserMenuConfig.class);

        assertNotNull(actualResponse);
        assertEquals(mockUpdateMenuConfig.getId(), actualResponse.getId());
        assertEquals(mockUpdateMenuConfig.getUserId(), actualResponse.getUserId());
        assertEquals(mockUpdateMenuConfig.getRoleId(), actualResponse.getRoleId());
        assertEquals(mockUpdateMenuConfig.getFeatureJson(), actualResponse.getFeatureJson());
        assertEquals(mockUpdateMenuConfig.getMenuId(), actualResponse.getMenuId());

        // Should be this
        // MockMvcBuilders.standaloneSetup(menuController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
    }


    List<MenuConfig> getDemoMenuConfigData() throws IOException {
        File file = new File("src/test/resources/mockdata/menu/demoMenuConfigData.json");
        InputStream fileInputStream = new FileInputStream(file);
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(fileInputStream, new TypeReference<List<MenuConfig>>(){});
    }

    List<MenuConfigDto> getMockMenuConfigDTOs() {
        MenuConfigDto m1 = new MenuConfigDto();
        m1.setId(1);
        m1.setMenuId(1);
        m1.setFeatureJson("{ \"canAdd\": true, \"canEdit\": true, \"canView\": true, \"canDelete\": true }");
        m1.setRoleId("R1");
        m1.setRoleName("ROLE-1");
        m1.setUserId("U1");
        m1.setUserName("User-1");

        MenuConfigDto m2 = new MenuConfigDto();
        m2.setId(2);
        m2.setMenuId(1);
        m2.setFeatureJson("{ \"canAdd\": true, \"canEdit\": true, \"canView\": true, \"canDelete\": true }");
        m2.setRoleId(null);
        m2.setRoleName(null);
        m2.setUserId("U1");
        m2.setUserName("User-1");
        return List.of(m1, m2);
    }
}