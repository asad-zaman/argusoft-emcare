package com.argusoft.who.emcare.web.menu.service;

import com.argusoft.who.emcare.web.common.response.Response;
import com.argusoft.who.emcare.web.menu.dao.MenuConfigRepository;
import com.argusoft.who.emcare.web.menu.dao.UserMenuConfigRepository;
import com.argusoft.who.emcare.web.menu.dto.FeatureJSON;
import com.argusoft.who.emcare.web.menu.dto.MenuConfigDto;
import com.argusoft.who.emcare.web.menu.model.MenuConfig;
import com.argusoft.who.emcare.web.menu.model.UserMenuConfig;
import com.argusoft.who.emcare.web.menu.service.impl.MenuServiceimpl;
import com.argusoft.who.emcare.web.user.service.UserService;
import com.google.gson.Gson;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ContextConfiguration(classes = {MenuServiceimpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
class MenuServiceTest {

    @Mock
    MenuConfigRepository menuConfigRepository;

    @Mock
    UserMenuConfigRepository userMenuConfigRepository;

    @Mock
    UserService userService;

    @InjectMocks
    MenuServiceimpl menuService;


    AutoCloseable autoCloseable;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void getAllMenus() throws IOException {
        List<MenuConfig> demoMenuConfigData = getDemoMenuConfigData();

        when(menuConfigRepository.findAll()).thenReturn(demoMenuConfigData);

        List<MenuConfig> actualMenuConfigs = menuService.getAllMenus();

        assertNotNull(actualMenuConfigs);
        assertThat(demoMenuConfigData.size() == actualMenuConfigs.size()).isTrue();
        // Compare lexicographical sorting of menu by name
        for(int i = 1; i < actualMenuConfigs.size(); i++) {
            assertThat(
                    actualMenuConfigs.get(i).getMenuName().compareTo(actualMenuConfigs.get(i - 1).getMenuName())
            ).isGreaterThanOrEqualTo(0);
        }
    }

    @Test
    void getMenuConfigByMenuId_ValidMenuId() throws IOException {
        List<UserMenuConfig> demoUserMenuConfigData = getDemoUserMenuConfigData();
        Map<Integer, Boolean> userMenuConfigHasRoleId = new HashMap<>();
        Map<Integer, Boolean> userMenuConfigHasUserId = new HashMap<>();

        for(UserMenuConfig userMenuConfig: demoUserMenuConfigData) {
            userMenuConfigHasRoleId.put(userMenuConfig.getId(), userMenuConfig.getRoleId() != null);
            userMenuConfigHasUserId.put(userMenuConfig.getId(), userMenuConfig.getUserId() != null);
        }

        when(userMenuConfigRepository.getMenuConfigByMenuId(anyInt())).thenReturn(demoUserMenuConfigData);
        when(userService.getUserById(anyString())).thenReturn(getMockUserRepresentation());
        when(userService.getRoleNameById(anyString())).thenReturn("roleName");

        List<MenuConfigDto> actualMenuConfigDtos = menuService.getMenuConfigByMenuId(1);

        assertNotNull(actualMenuConfigDtos);
        assertThat(actualMenuConfigDtos.size() == demoUserMenuConfigData.size()).isTrue();

        // Confirm data mapping when roleId or userId is present
        Gson g = new Gson();
        for(int i = 1; i < actualMenuConfigDtos.size(); i++) {
            Boolean hasRoleId = userMenuConfigHasRoleId.get(actualMenuConfigDtos.get(i).getId());
            Boolean hasUserId = userMenuConfigHasUserId.get(actualMenuConfigDtos.get(i).getId());
            assertNotNull(hasRoleId);
            assertNotNull(hasUserId);
            if(hasRoleId) assertNotNull(actualMenuConfigDtos.get(i).getRoleName());
            if(hasUserId) assertNotNull(actualMenuConfigDtos.get(i).getUserName());
            FeatureJSON f = g.fromJson(actualMenuConfigDtos.get(i).getFeatureJson(), FeatureJSON.class);
            assertNotNull(f.getCanExport());
        }
    }

    @Test
    void getMenuConfigByMenuId_InvalidMenuId() {
        List<UserMenuConfig> demoUserMenuConfigData = new ArrayList<>();
        when(userMenuConfigRepository.getMenuConfigByMenuId(anyInt())).thenReturn(demoUserMenuConfigData);

        List<MenuConfigDto> actualMenuConfigDtos = menuService.getMenuConfigByMenuId(1);

        assertNotNull(actualMenuConfigDtos);
        assertThat(actualMenuConfigDtos.size() == demoUserMenuConfigData.size()).isTrue();
    }

    @Test
    void addMenuConfiguration() {
        MenuConfigDto mockMenuConfigDto = new MenuConfigDto();
        mockMenuConfigDto.setId(1);
        mockMenuConfigDto.setMenuId(1);
        mockMenuConfigDto.setFeatureJson("{ \"canEdit\": true, \"canDelete\": true, \"canAdd\": true, \"canView\": true, \"canExport\": true }");
        mockMenuConfigDto.setRoleId("7868b46d-a4fb-4d87-b939-729f381cbb37");
        mockMenuConfigDto.setRoleName("roleName");
        mockMenuConfigDto.setUserId("7d8b2cf7-5e57-4909-b880-6f96d49bbec1");
        mockMenuConfigDto.setUserName("Test User");

        when(userMenuConfigRepository.save(any(UserMenuConfig.class))).thenAnswer(i -> i.getArgument(0));

        UserMenuConfig expectedUserMenuConfig = menuService.addMenuConfiguration(mockMenuConfigDto);

        assertNotNull(expectedUserMenuConfig);
        assertEquals(expectedUserMenuConfig.getMenuId(), mockMenuConfigDto.getMenuId());
        assertEquals(expectedUserMenuConfig.getUserId(), mockMenuConfigDto.getUserId());
        assertEquals(expectedUserMenuConfig.getRoleId(), mockMenuConfigDto.getRoleId());
        assertEquals(expectedUserMenuConfig.getFeatureJson(), mockMenuConfigDto.getFeatureJson());
    }

    @Test
    void deleteMenuConfig_ExistingMenuConfigId() {
        doNothing().when(userMenuConfigRepository).deleteById(anyInt());
        Response actualResponse = menuService.deleteMenuConfig(1);

        verify(userMenuConfigRepository, times(1)).deleteById(1);

        assertEquals(actualResponse.getStatusCode(), HttpStatus.OK.value());
        assertEquals(actualResponse.getErrorMessage(), "Successfully Deleted");
    }

    @Test
    void deleteMenuConfig_NonExistingMenuConfigId() {
        String expectedExceptionMessage = "MenuUserConfig Doesn't Exists";
        int expectedExceptionHashCode = 404;

        EmptyResultDataAccessException mockException = mock(EmptyResultDataAccessException.class);
        when(mockException.getMessage()).thenReturn(expectedExceptionMessage);

        doThrow(mockException).when(userMenuConfigRepository).deleteById(anyInt());

        Response actualResponse = menuService.deleteMenuConfig(-1);

        verify(userMenuConfigRepository, times(1)).deleteById(-1);

        assertEquals(expectedExceptionMessage, actualResponse.getErrorMessage());
    }

    // Data fetching functions
    List<MenuConfig> getDemoMenuConfigData() throws IOException {
        File file = new File("src/test/resources/mockdata/menu/demoMenuConfigData.json");
        InputStream fileInputStream = new FileInputStream(file);
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(fileInputStream, new TypeReference<List<MenuConfig>>(){});
    }

    UserRepresentation getMockUserRepresentation() {
        UserRepresentation u = new UserRepresentation();
        u.setUsername("userName");
        return u;
    }

    List<UserMenuConfig> getDemoUserMenuConfigData() throws IOException {
        File file = new File("src/test/resources/mockdata/menu/mockUserMenuConfigData.json");
        InputStream fileInputStream = new FileInputStream(file);
        return objectMapper.readValue(fileInputStream, new TypeReference<List<UserMenuConfig>>(){});
    }
}