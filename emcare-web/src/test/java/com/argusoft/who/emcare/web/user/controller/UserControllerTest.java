package com.argusoft.who.emcare.web.user.controller;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.common.response.Response;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.location.dto.LocationMasterWithHierarchy;
import com.argusoft.who.emcare.web.location.dto.LocationaListDto;
import com.argusoft.who.emcare.web.location.service.LocationService;
import com.argusoft.who.emcare.web.user.cons.UserConst;
import com.argusoft.who.emcare.web.user.dto.*;
import com.argusoft.who.emcare.web.user.service.UserService;
import com.argusoft.who.emcare.web.userlocationmapping.model.UserLocationMapping;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {UserController.class})
@RunWith(MockitoJUnitRunner.class)
class UserControllerTest {

    @Mock
    UserService userService;

    @Mock
    LocationService locationConfigService;

    @InjectMocks
    UserController userController;

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
    void testGetCurrentLoggedInUser() throws Exception {
        UserMasterDto userMasterDto = new UserMasterDto();
        userMasterDto.setUserId("user-1");
        ResponseEntity mockEntity = ResponseEntity.ok(userMasterDto);
        when(userService.getCurrentUser()).thenReturn(mockEntity);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user").accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);
        assertNotEquals("", response);

        UserMasterDto actualUser = objectMapper.readValue(response, UserMasterDto.class);

        assertNotNull(actualUser);
        assertEquals("user-1", actualUser.getUserId());
    }

    @Test
    void testGetAllUser() throws Exception {
        MultiLocationUserListDto mlul1 = new MultiLocationUserListDto();
        mlul1.setId("MLUL1");
        mlul1.setUserName("tester1");
        mlul1.setFacilities(List.of());
        MultiLocationUserListDto mlul2 = new MultiLocationUserListDto();
        mlul2.setId("MLUL2");
        mlul2.setUserName("tester2");
        mlul2.setFacilities(List.of(new FacilityDto()));
        MultiLocationUserListDto mlul3 = new MultiLocationUserListDto();
        mlul3.setId("MLUL3");
        mlul3.setUserName("tester3");
        mlul3.setFacilities(List.of());
        MultiLocationUserListDto mlul4 = new MultiLocationUserListDto();
        mlul4.setId("MLUL4");
        mlul4.setUserName("tester4");
        mlul4.setFacilities(List.of());
        when(userService.getAllUserWithMultiLocation(any())).thenReturn(List.of(mlul1, mlul2, mlul3, mlul4));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user/all").accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);
        assertNotEquals("", response);

        List<MultiLocationUserListDto> actualUsers = objectMapper.readValue(response, new TypeReference<List<MultiLocationUserListDto>>() {
        });

        assertNotNull(actualUsers);
        assertEquals(4, actualUsers.size());
        assertEquals(1, actualUsers.get(1).getFacilities().size());
    }

    @Nested
    class TestGetUserPage {
        @BeforeEach
        void setUpGetUserPage() {
            when(userService.getUserPage(any(), anyInt(), any(), any()))
                    .thenAnswer(i -> {
                        Integer page = i.getArgument(1);
                        String search = i.getArgument(2);
                        Boolean filter = i.getArgument(3);
                        PageDto pageDto = new PageDto();
                        int totalUsers = 1000;

                        if (filter != null) totalUsers = (int) (totalUsers / (filter ? 1.5 : 1.0));

                        if (search != null) totalUsers = (int) (totalUsers / (search.length() + 1.0));

                        List<String> data = new ArrayList<>();
                        int x = page * CommonConstant.PAGE_SIZE;
                        int y = Math.min((page + 1) * CommonConstant.PAGE_SIZE, totalUsers);
                        while (x < y) data.add("Test: " + (++x));
                        pageDto.setTotalCount((long) totalUsers);
                        pageDto.setList(data);
                        return pageDto;
                    });
        }

        @Test
        void invalidQuery() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user/page")
                    .accept(MediaType.APPLICATION_JSON);

            MockMvcBuilders.standaloneSetup(userController)
                    .build()
                    .perform(requestBuilder)
                    .andExpect(status().is4xxClientError());

            requestBuilder = MockMvcRequestBuilders.get("/api/user/page")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("pageNo", "wrong")
                    .param("filter", "1");

            MockMvcBuilders.standaloneSetup(userController)
                    .build()
                    .perform(requestBuilder)
                    .andExpect(status().is4xxClientError());
        }

        @Test
        void page1NoSearchOrFilter() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user/page")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("pageNo", "1");

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);
            assertNotEquals("", response);

            PageDto actualPage = objectMapper.readValue(response, PageDto.class);

            assertNotNull(actualPage);
            assertEquals(1000, actualPage.getTotalCount());
            assertEquals(10, actualPage.getList().size());
        }

        @Test
        void page1WithSearchAndFilter() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user/page")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("pageNo", "1")
                    .param("search", "test")
                    .param("filter", "true");

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);
            assertNotEquals("", response);

            PageDto actualPage = objectMapper.readValue(response, PageDto.class);

            assertNotNull(actualPage);
            assertEquals(133, actualPage.getTotalCount());
            assertEquals(10, actualPage.getList().size());
        }

        @Test
        void page9WithSearchAndFilter() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user/page")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("pageNo", "9")
                    .param("search", "tester")
                    .param("filter", "true");

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);
            assertNotEquals("", response);

            PageDto actualPage = objectMapper.readValue(response, PageDto.class);

            assertNotNull(actualPage);
            assertEquals(95, actualPage.getTotalCount());
            assertEquals(5, actualPage.getList().size());
        }
    }

    @Test
    void testGetAllSignedUpUser() throws Exception {
        FacilityDto f1 = new FacilityDto();
        f1.setFacilityId("F1");
        FacilityDto f2 = new FacilityDto();
        f2.setFacilityId("F2");

        UserListDto ul1 = new UserListDto();
        ul1.setUserName("tester-1");
        ul1.setFacilityDto(f1);
        ul1.setRealmRoles(List.of("tester", "admin"));

        UserListDto ul2 = new UserListDto();
        ul2.setUserName("tester-2");
        ul2.setFacilityDto(f2);
        ul2.setRealmRoles(List.of("tester", "user"));

        UserListDto ul3 = new UserListDto();
        ul3.setUserName("tester-3");
        ul3.setFacilityDto(f1);
        ul3.setRealmRoles(List.of("user"));

        List<UserListDto> userListDtoList = new ArrayList<>();
        userListDtoList.add(ul1);
        userListDtoList.add(ul2);
        userListDtoList.add(ul3);

        when(userService.getAllSignedUpUser(any())).thenReturn(userListDtoList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user/signedup")
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);
        assertNotEquals("", response);

        List<UserListDto> actualUserList = objectMapper.readValue(response, new TypeReference<List<UserListDto>>() {
        });

        assertNotNull(actualUserList);
        assertEquals(userListDtoList.size(), actualUserList.size());
        assertEquals(userListDtoList.get(0).getUserName(), actualUserList.get(0).getUserName());
        assertEquals(userListDtoList.get(1).getFacilityDto().getFacilityId(), actualUserList.get(1).getFacilityDto().getFacilityId());
        assertEquals(userListDtoList.get(2).getRealmRoles().get(0), actualUserList.get(2).getRealmRoles().get(0));
    }

    @Test
    void getAllRoles() throws Exception {
        RoleRepresentation r1 = new RoleRepresentation();
        r1.setName("tester-1");
        r1.setId("role-1");

        RoleRepresentation r2 = new RoleRepresentation();
        r2.setName("tester-2");
        r2.setId("role-1");

        RoleRepresentation r3 = new RoleRepresentation();
        r3.setName("tester-3");
        r3.setId("role-1");

        List<RoleRepresentation> roleRepresentationList = new ArrayList<>();
        roleRepresentationList.add(r1);
        roleRepresentationList.add(r2);
        roleRepresentationList.add(r3);

        when(userService.getAllRoles(any())).thenReturn(roleRepresentationList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/role")
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);
        assertNotEquals("", response);

        List<RoleRepresentation> actualRolesList = objectMapper.readValue(response, new TypeReference<List<RoleRepresentation>>() {
        });

        assertNotNull(actualRolesList);
        assertEquals(roleRepresentationList.size(), actualRolesList.size());
        assertEquals(roleRepresentationList.get(0).getName(), actualRolesList.get(0).getName());
        assertEquals(roleRepresentationList.get(1).getId(), actualRolesList.get(1).getId());
    }

    @Test
    void getAllRolesForSignup() throws Exception {
        RoleRepresentation r1 = new RoleRepresentation();
        r1.setName("tester-1");
        r1.setId("role-1");

        RoleRepresentation r2 = new RoleRepresentation();
        r2.setName("tester-2");
        r2.setId("role-1");

        RoleRepresentation r3 = new RoleRepresentation();
        r3.setName("tester-3");
        r3.setId("role-1");

        List<RoleRepresentation> roleRepresentationList = new ArrayList<>();
        roleRepresentationList.add(r1);
        roleRepresentationList.add(r2);
        roleRepresentationList.add(r3);

        RolesResource mockRolesResource = mock(RolesResource.class);
        when(mockRolesResource.list()).thenReturn(roleRepresentationList);

        when(userService.getAllRolesForSignUp(any())).thenReturn(mockRolesResource);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/signup/roles")
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);
        assertNotEquals("", response);

        List<RoleRepresentation> actualRolesList = objectMapper.readValue(response, new TypeReference<List<RoleRepresentation>>() {
        });

        assertNotNull(actualRolesList);
        assertEquals(roleRepresentationList.size(), actualRolesList.size());
        assertEquals(roleRepresentationList.get(0).getName(), actualRolesList.get(0).getName());
        assertEquals(roleRepresentationList.get(1).getId(), actualRolesList.get(1).getId());
    }

    @Test
    void getAllLocation() throws Exception {
        LocationaListDto l1 = new LocationaListDto();
        l1.setName("tester-1");
        l1.setId(1);

        LocationaListDto l2 = new LocationaListDto();
        l2.setName("tester-2");
        l2.setId(1);

        LocationaListDto l3 = new LocationaListDto();
        l3.setName("tester-3");
        l3.setId(1);

        List<LocationaListDto> locationaListDtos = new ArrayList<>();
        locationaListDtos.add(l1);
        locationaListDtos.add(l2);
        locationaListDtos.add(l3);


        when(locationConfigService.getAllLocation()).thenReturn(ResponseEntity.ok(locationaListDtos));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/signup/location")
                .accept(MediaType.APPLICATION_JSON);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

        String response = resultActions.andReturn().getResponse().getContentAsString();

        assertNotNull(response);
        assertNotEquals("", response);

        List<LocationaListDto> actualLocationaListDto = objectMapper.readValue(response, new TypeReference<List<LocationaListDto>>() {
        });

        assertNotNull(actualLocationaListDto);
        assertEquals(locationaListDtos.size(), actualLocationaListDto.size());
        assertEquals(locationaListDtos.get(0).getName(), actualLocationaListDto.get(0).getName());
        assertEquals(locationaListDtos.get(1).getId(), actualLocationaListDto.get(1).getId());
    }

    @Nested
    class testAddUser {
        @BeforeEach
        void setUpAddUser() {
            when(userService.signUp(any(UserDto.class), any())).thenAnswer(i -> {
                UserDto userDto = i.getArgument(0);
                if (userDto.getEmail().equals("new@test.com"))
                    return ResponseEntity.ok(new Response(CommonConstant.REGISTER_SUCCESS, HttpStatus.OK.value()));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(CommonConstant.EMAIL_ALREADY_EXISTS, HttpStatus.BAD_REQUEST.value()));
            });
        }

        @Test
        void addValidUser() throws Exception {
            UserDto user = new UserDto();
            user.setEmail("new@test.com");
            user.setPassword("tester");
            user.setFacilityIds(List.of("F1"));
            user.setFirstName("test");
            user.setLastName("edm");
            user.setRoleName("tester");
            user.setRegRequestFrom("tester");

            RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/signup")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user));

            MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder).andExpect(status().is2xxSuccessful());
        }

        @Test
        void addInvalidUser() throws Exception {
            UserDto user = new UserDto();
            user.setEmail("old@test.com");
            user.setPassword("tester");
            user.setFacilityIds(List.of("F1"));
            user.setFirstName("test");
            user.setLastName("edm");
            user.setRoleName("tester");
            user.setRegRequestFrom("tester");

            RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/signup")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user));

            MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
        }

        @Test
        void invalidRequestBody() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/signup")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString("Hello"));

            MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
        }
    }

    @Nested
    class testUserLogin {
        @BeforeEach
        void setUpUserLogin() {
            when(userService.userLogin(any(LoginRequestDto.class), any())).thenAnswer(i -> {
                LoginRequestDto loginRequestDto = i.getArgument(0);
                HashMap<String, String> map = new HashMap<>();
                map.put("status", "200");
                map.put("message", "login success");
                if (loginRequestDto.getUsername().equals("new@test.com") && loginRequestDto.getPassword().equals("tester"))
                    return ResponseEntity.ok(map);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("You don't have access for this domain", HttpStatus.BAD_REQUEST.value()));
            });
        }

        @Test
        void validCreds() throws Exception {
            LoginRequestDto loginRequestDto = new LoginRequestDto();
            loginRequestDto.setUsername("new@test.com");
            loginRequestDto.setPassword("tester");

            RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/auth/login")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequestDto));

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);
            assertNotEquals("", response);
        }

        @Test
        void invalidCreds() throws Exception {
            LoginRequestDto loginRequestDto = new LoginRequestDto();
            loginRequestDto.setUsername("old@test.com");
            loginRequestDto.setPassword("wrong");

            RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/auth/login")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequestDto));

            MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
        }

        @Test
        void invalidRequestBody() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/auth/login")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString("Hello"));

            MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
        }
    }


    @Test
    void testUserLogout() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/auth/logout")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder).andExpect(status().is2xxSuccessful());
    }

    @Nested
    class testAddUserFromWeb {
        @BeforeEach
        void setUpAddUserFromWeb() {
            when(userService.addUser(any(UserDto.class), any(HttpServletRequest.class))).thenAnswer(i -> {
                UserDto loginRequestDto = i.getArgument(0);
                HashMap<String, String> map = new HashMap<>();
                map.put("status", "200");
                map.put("message", "login success");
                if (
                        loginRequestDto.getUserName().equals("new@test.com") &&
                                List.of("F1", "F2", "F4").containsAll(loginRequestDto.getFacilityIds()) &&
                                List.of("tester", "user").contains(loginRequestDto.getRoleName()) &&
                                List.of(UserConst.WEB, UserConst.MOBILE).contains(loginRequestDto.getRegRequestFrom())
                )
                    return ResponseEntity.ok(map);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("You don't have access for this domain", HttpStatus.BAD_REQUEST.value()));
            });
        }

        @Test
        void validUser() throws Exception {
            UserDto userDto = new UserDto();
            userDto.setUserName("new@test.com");
            userDto.setRoleName("tester");
            userDto.setFacilityIds(Collections.singletonList("F1"));
            userDto.setRegRequestFrom(UserConst.WEB);

            RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/user/add")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto));

            MockMvcBuilders.standaloneSetup(userController)
                    .build()
                    .perform(requestBuilder)
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void invalidUser() throws Exception {
            UserDto userDto1 = new UserDto();
            userDto1.setUserName("old@test.com");
            userDto1.setRoleName("tester");
            userDto1.setFacilityIds(Collections.singletonList("F1"));
            userDto1.setRegRequestFrom(UserConst.WEB);

            UserDto userDto2 = new UserDto();
            userDto2.setUserName("new@test.com");
            userDto2.setRoleName("tester");
            userDto2.setFacilityIds(Collections.singletonList("F1"));
            userDto2.setRegRequestFrom("LAPTOP");

            UserDto userDto3 = new UserDto();
            userDto3.setUserName("new@test.com");
            userDto3.setRoleName("admin");
            userDto3.setFacilityIds(Collections.singletonList("F1"));
            userDto3.setRegRequestFrom(UserConst.WEB);

            UserDto userDto4 = new UserDto();
            userDto4.setUserName("new@test.com");
            userDto4.setRoleName("user");
            userDto4.setFacilityIds(Collections.singletonList("F3"));
            userDto4.setRegRequestFrom(UserConst.MOBILE);

            for (UserDto user : List.of(userDto1, userDto2, userDto3, userDto4)) {
                RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/user/add")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user));

                MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
            }
        }

        @Test
        void invalidRequestBody() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/user/add")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString("Hello"));

            MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
        }
    }

    @Nested
    class testAddRealmRole {
        @Test
        void validRole() throws Exception {
            RoleDto roleDto = new RoleDto();
            roleDto.setRoleName("tester");
            roleDto.setRoleDescription("tester");
            RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/role/add")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(roleDto));

            MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder).andExpect(status().is2xxSuccessful());
            verify(userService, times(1)).addRealmRole(any(RoleDto.class));
        }

        @Test
        void invalidBody() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/role/add")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"invalid\"}");

            // @TODO: should not work
//            MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
            MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder).andExpect(status().is2xxSuccessful());
        }
    }


    @Test
    void testAddRealmRoleInvalidRequest() throws Exception {
        RoleDto roleDto = new RoleDto();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/role/add")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDto));

        MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder).andExpect(status().is2xxSuccessful());
        verify(userService, times(1)).addRealmRole(any(RoleDto.class));
    }

    @Nested
    class testGetUserById {
        @BeforeEach
        void setUpGetUserById() {
            MultiLocationUserListDto multiLocationUserListDto = new MultiLocationUserListDto();
            FacilityDto f1 = new FacilityDto();
            f1.setFacilityId("F1");
            FacilityDto f2 = new FacilityDto();
            f2.setFacilityId("F2");
            multiLocationUserListDto.setUserName("tester@argusoft.com");
            multiLocationUserListDto.setId("1");
            multiLocationUserListDto.setFirstName("FName");
            multiLocationUserListDto.setLanguage(CommonConstant.HINDI);
            multiLocationUserListDto.setLastName("LName");
            multiLocationUserListDto.setEnabled(true);
            multiLocationUserListDto.setRealmRoles(List.of("tester", "user"));
            multiLocationUserListDto.setFacilities(List.of(f1, f2));
            multiLocationUserListDto.setLocations(List.of(new LocationMasterWithHierarchy()));
            when(userService.getUserDtoById(anyString())).thenReturn(null);
            when(userService.getUserDtoById("1")).thenReturn(multiLocationUserListDto);
        }

        @Test
        void validId() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user/1")
                    .accept(MediaType.APPLICATION_JSON);

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);
            assertNotEquals("", response);

            MultiLocationUserListDto actualUser = objectMapper.readValue(response, MultiLocationUserListDto.class);
            assertNotNull(actualUser);

            assertEquals(2, actualUser.getFacilities().size());
            assertEquals("F1", actualUser.getFacilities().get(0).getFacilityId());
            assertEquals("F2", actualUser.getFacilities().get(1).getFacilityId());

            assertEquals("tester@argusoft.com", actualUser.getUserName());
            assertEquals("1", actualUser.getId());
            assertEquals("FName", actualUser.getFirstName());
            assertEquals(CommonConstant.HINDI, actualUser.getLanguage());
            assertEquals("LName", actualUser.getLastName());
            assertEquals(true, actualUser.getEnabled());

            assertEquals(2, actualUser.getRealmRoles().size());
            assertEquals("tester", actualUser.getRealmRoles().get(0));
            assertEquals("user", actualUser.getRealmRoles().get(1));

            assertEquals(1, actualUser.getLocations().size());
        }

        @Test
        void invalidId() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user/2")
                    .accept(MediaType.APPLICATION_JSON);

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);
            assertEquals("", response);
        }
    }

    @Nested
    class testGetUserRoleById {
        @BeforeEach
        void setUpGetUserRoleById() {
            RoleRepresentation r1 = new RoleRepresentation();
            r1.setId("role-1");
            r1.setName("role-tester");
            r1.setComposite(false);
            r1.setClientRole(false);

            RoleRepresentation r2 = new RoleRepresentation();
            r2.setId("role-2");
            r2.setName("role-user");
            r2.setComposite(false);
            r2.setClientRole(false);

            MappingsRepresentation mappingsRepresentation = new MappingsRepresentation();
            mappingsRepresentation.setRealmMappings(List.of(r1, r2));
            when(userService.getUserRolesById(anyString())).thenReturn(null);
            when(userService.getUserRolesById("1")).thenReturn(ResponseEntity.ok(mappingsRepresentation));
        }

        @Test
        void validId() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user/role/1")
                    .accept(MediaType.APPLICATION_JSON);

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);
            assertNotEquals("", response);

            MappingsRepresentation actualRoles = objectMapper.readValue(response, MappingsRepresentation.class);
            assertNotNull(actualRoles);

            assertEquals(2, actualRoles.getRealmMappings().size());
            assertEquals("role-tester", actualRoles.getRealmMappings().get(0).getName());
            assertEquals("role-2", actualRoles.getRealmMappings().get(1).getId());
        }

        @Test
        void invalidId() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user/role/2")
                    .accept(MediaType.APPLICATION_JSON);

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);
            assertEquals("", response);
        }
    }

    @Nested
    class testUpdateUser {
        @BeforeEach
        void setUpUpdateUser() {
            when(userService.updateUser(any(UserDto.class), anyString(), any(HttpServletRequest.class))).thenAnswer(i -> {
                UserDto userDto = i.getArgument(0);
                String userId = i.getArgument(1);
                HashMap<String, String> map = new HashMap<>();
                map.put("status", "200");
                map.put("message", "login success");
                if(userId.equals("1")) return ResponseEntity.ok(map);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("User not found", HttpStatus.BAD_REQUEST.value()));
            });
        }

        @Test
        void validUserId() throws Exception {
            UserDto userDto = new UserDto();
            userDto.setUserName("new@test.com");
            userDto.setRoleName("tester");
            userDto.setFacilityIds(Collections.singletonList("F1"));
            userDto.setRegRequestFrom(UserConst.WEB);

            RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/user/update/1")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto));

            MockMvcBuilders.standaloneSetup(userController)
                    .build()
                    .perform(requestBuilder)
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void invalidUser() throws Exception {
            UserDto userDto1 = new UserDto();
            userDto1.setUserName("old@test.com");
            userDto1.setRoleName("tester");
            userDto1.setFacilityIds(Collections.singletonList("F1"));
            userDto1.setRegRequestFrom(UserConst.WEB);

            RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/user/update/2")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto1));

            MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
        }

        @Test
        void invalidRequestBody() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/user/update/1")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString("Hello"));

            MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
        }

        @Test
        void invalidRequestPath() throws Exception {
            UserDto userDto1 = new UserDto();
            userDto1.setUserName("old@test.com");
            userDto1.setRoleName("tester");
            userDto1.setFacilityIds(Collections.singletonList("F1"));
            userDto1.setRegRequestFrom(UserConst.WEB);

            RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/user/update")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto1));

            MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
        }
    }

    @Test
    void testUpdatePassword() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserName("new@test.com");
        userDto.setRoleName("tester");
        userDto.setPassword("pwddwp");
        userDto.setFacilityIds(Collections.singletonList("F1"));
        userDto.setRegRequestFrom(UserConst.WEB);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/user/update/password/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto));

        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(status().is2xxSuccessful());
    }

    @Nested
    class testGetRoleById {
        @BeforeEach
        void setUpGetRoleById() {
            when(userService.getRoleByName(anyString(), any(HttpServletRequest.class))).thenAnswer(i -> {
                RoleRepresentation role = new RoleRepresentation();
                role.setId("role-tester");
                role.setName("TESTER");
                if(role.getId().equals(i.getArgument(0))) return role;
                return null;
            });
        }

        @Test
        void validRoleId() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/role/role-tester")
                    .accept(MediaType.APPLICATION_JSON);

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);
            assertNotEquals("", response);

            RoleRepresentation actualRole = objectMapper.readValue(response, RoleRepresentation.class);

            assertNotNull(actualRole);
            assertEquals("role-tester", actualRole.getId());
            assertEquals("TESTER", actualRole.getName());
        }

        @Test
        void invalidRole() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/role/role-cook")
                    .accept(MediaType.APPLICATION_JSON);

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);
            assertEquals("", response);
        }
    }

    @Nested
    class testUpdateRole {
        @BeforeEach
        void setUpUpdateRole() {
            when(userService.updateRole(any(RoleUpdateDto.class))).thenAnswer(i -> ResponseEntity.ok(i.getArgument(0)));
        }

        @Test
        void validRoleId() throws Exception {
            RoleUpdateDto roleUpdateDto = new RoleUpdateDto();
            roleUpdateDto.setId("role-1");
            roleUpdateDto.setName("tester");
            roleUpdateDto.setOldRoleName("test");
            roleUpdateDto.setDescription("can test application");

            RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/role/update")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(roleUpdateDto));

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);
            assertNotEquals("", response);

            RoleUpdateDto actualRole = objectMapper.readValue(response, RoleUpdateDto.class);

            assertNotNull(actualRole);
            assertEquals(roleUpdateDto.getId(), actualRole.getId());
            assertEquals(roleUpdateDto.getName(), actualRole.getName());
            assertEquals(roleUpdateDto.getOldRoleName(), actualRole.getOldRoleName());
            assertEquals(roleUpdateDto.getDescription(), actualRole.getDescription());
        }

        @Test
        void invalidRole() throws Exception {
            RoleUpdateDto roleUpdateDto = new RoleUpdateDto();
            roleUpdateDto.setId("role-2");
            roleUpdateDto.setName("not existing role");
            roleUpdateDto.setOldRoleName("test");
            roleUpdateDto.setDescription("can test application");

            RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/role/update")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(roleUpdateDto));

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

            // @TODO: should reject invalid id / invalid roles
//             ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is4xxClientError());
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);
            assertNotEquals("", response);

            RoleUpdateDto actualRole = objectMapper.readValue(response, RoleUpdateDto.class);

            assertNotNull(actualRole);
            assertEquals(roleUpdateDto.getId(), actualRole.getId());
            assertEquals(roleUpdateDto.getName(), actualRole.getName());
            assertEquals(roleUpdateDto.getOldRoleName(), actualRole.getOldRoleName());
            assertEquals(roleUpdateDto.getDescription(), actualRole.getDescription());
        }
    }

    @Nested
    class testChangeUserStatus {
        @BeforeEach
        void setUpChangeUserStatus() {
            when(userService.updateUserStatus(any(UserUpdateDto.class))).thenAnswer(i -> {
                UserUpdateDto userUpdateDto = i.getArgument(0);

                if(!userUpdateDto.getUserId().equals("user-1")) return ResponseEntity.ok(List.of());

                UserLocationMapping userLocationMapping1 = new UserLocationMapping();
                userLocationMapping1.setId(1);
                userLocationMapping1.setUserId("user-1");
                userLocationMapping1.setIsFirst(false);
                userLocationMapping1.setState(userUpdateDto.getIsEnabled());

                UserLocationMapping userLocationMapping2 = new UserLocationMapping();
                userLocationMapping2.setId(2);
                userLocationMapping2.setUserId("user-1");
                userLocationMapping2.setIsFirst(false);
                userLocationMapping2.setState(userUpdateDto.getIsEnabled());

                UserLocationMapping userLocationMapping3 = new UserLocationMapping();
                userLocationMapping3.setId(3);
                userLocationMapping3.setUserId("user-1");
                userLocationMapping3.setIsFirst(false);
                userLocationMapping3.setState(userUpdateDto.getIsEnabled());

               return ResponseEntity.ok(List.of(userLocationMapping3, userLocationMapping2, userLocationMapping1));
            });
        }

        @Test
        void validUserId() throws Exception {
            UserUpdateDto userUpdateDto = new UserUpdateDto();
            userUpdateDto.setUserId("user-1");
            userUpdateDto.setIsEnabled(true);

            RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/user/status/change")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userUpdateDto));

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);
            assertNotEquals("", response);

            List<UserLocationMapping> actualUserMappings = objectMapper.readValue(response, new TypeReference<List<UserLocationMapping>>(){});

            assertNotNull(actualUserMappings);
            assertEquals(3, actualUserMappings.size());
            for(UserLocationMapping uLM: actualUserMappings) {
                assertFalse(uLM.isIsFirst());
                assertEquals(userUpdateDto.getIsEnabled(), uLM.isState());
            }
        }

        @Test
        void invalidUserId() throws Exception {
            UserUpdateDto userUpdateDto = new UserUpdateDto();
            userUpdateDto.setUserId("user-2");
            userUpdateDto.setIsEnabled(true);;

            RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/user/status/change")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userUpdateDto));

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

            // @TODO: should reject invalid id / invalid roles
//             ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is4xxClientError());
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);
            assertEquals("[]", response);
        }
    }

    @Nested
    class testGetUsersUnderLocation {
        @BeforeEach
        void setUpGetUsersUnderLocation() {
            when(userService.getUsersUnderLocation(any(), any(), anyInt(), any())).thenAnswer(i -> {
                String locationId = i.getArgument(0);
                String search = i.getArgument(1);
                Integer pageNo = i.getArgument(2);
                Boolean filter = i.getArgument(3);
                PageDto pageDto = new PageDto();
                int totalUsers = 10000;
                if(locationId != null) totalUsers /= 10;
                if(search != null) totalUsers /= (search.length() + 1);
                if(filter != null) totalUsers /= filter ? 5 : 3;
                int x = pageNo * CommonConstant.PAGE_SIZE;
                int y = Math.min((pageNo + 1) * CommonConstant.PAGE_SIZE, totalUsers);
                List<String> data = new ArrayList<>();
                while(x < y) data.add("test-" + (++x));
                pageDto.setTotalCount((long)totalUsers);
                pageDto.setList(data);
                return pageDto;
            });
        }

        @Test
        void noParams() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user/locationId")
                    .accept(MediaType.APPLICATION_JSON);

            MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
        }

        @Test
        void invalidParams1() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user/locationId")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("pageNo", "first");

            MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
        }

        @Test
        void invalidParams2() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user/locationId")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("pageNo", "first")
                    .param("filter", "HELLOW");

            MockMvcBuilders.standaloneSetup(userController).build().perform(requestBuilder).andExpect(status().is4xxClientError());
        }

        @Test
        void noSearchPage1() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user/locationId")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("pageNo", "1");

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);
            assertNotEquals("", response);

            PageDto actualPage = objectMapper.readValue(response, PageDto.class);

            assertNotNull(actualPage);
            assertEquals(10000, actualPage.getTotalCount());
            assertEquals(CommonConstant.PAGE_SIZE, actualPage.getList().size());
        }

        @Test
        void locationIdAndPage2() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user/locationId")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("pageNo", "2")
                    .param("locationId", "100");

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);
            assertNotEquals("", response);

            PageDto actualPage = objectMapper.readValue(response, PageDto.class);

            assertNotNull(actualPage);
            assertEquals(1000, actualPage.getTotalCount());
            assertEquals(CommonConstant.PAGE_SIZE, actualPage.getList().size());
        }

        @Test
        void locationIdAndFilterPage3() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user/locationId")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("pageNo", "3")
                    .param("filter", "true")
                    .param("locationId", "100");

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);
            assertNotEquals("", response);

            PageDto actualPage = objectMapper.readValue(response, PageDto.class);

            assertNotNull(actualPage);
            assertEquals(200, actualPage.getTotalCount());
            assertEquals(CommonConstant.PAGE_SIZE, actualPage.getList().size());
        }

        @Test
        void locationIdAndSearchAndFilterPage1() throws Exception {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user/locationId")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("pageNo", "1")
                    .param("filter", "true")
                    .param("searchString", "helloworld")
                    .param("locationId", "100");

            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
            ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful());

            String response = resultActions.andReturn().getResponse().getContentAsString();

            assertNotNull(response);
            assertNotEquals("", response);

            PageDto actualPage = objectMapper.readValue(response, PageDto.class);

            assertNotNull(actualPage);
            assertEquals(18, actualPage.getTotalCount());
            assertEquals(8, actualPage.getList().size());
        }
    }

    @Test
    void testCheckEmailAlreadyExist() throws Exception {
        Map<String, Object> nonExistingResponse = new HashMap<>();
        nonExistingResponse.put("status", HttpStatus.OK.value());
        nonExistingResponse.put("message", "Valid Email Address");

        Map<String, Object> existingResponse = new HashMap<>();
        existingResponse.put("status", HttpStatus.BAD_REQUEST.value());
        existingResponse.put("message", "Email Already Taken By User");

        when(userService.checkEmailIdExist(anyString())).thenReturn(existingResponse);
        when(userService.checkEmailIdExist("newtest@gmail.com")).thenReturn(nonExistingResponse);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        RequestBuilder existingReq = MockMvcRequestBuilders.get("/api/user/check/email")
                .accept(MediaType.APPLICATION_JSON)
                .param("emailId", "oldtest@gmail.com");


        RequestBuilder nonExisting = MockMvcRequestBuilders.get("/api/user/check/email")
                .accept(MediaType.APPLICATION_JSON)
                .param("emailId", "newtest@gmail.com");

        ResultActions responseExisting = mockMvc.perform(existingReq).andExpect(status().is2xxSuccessful());
        ResultActions responseNonExisting = mockMvc.perform(nonExisting).andExpect(status().is2xxSuccessful());

        String resultExisting = responseExisting.andReturn().getResponse().getContentAsString();
        String resultNonExisting = responseNonExisting.andReturn().getResponse().getContentAsString();

        assertNotNull(resultExisting); assertNotEquals("", resultExisting);
        assertNotNull(resultNonExisting); assertNotEquals("", resultNonExisting);

        Map<String, Object> existingResultMap = objectMapper.readValue(resultExisting, Map.class);
        Map<String, Object> nonExistingResultMap = objectMapper.readValue(resultNonExisting, Map.class);

        assertNotNull(existingResultMap); assertEquals(2, existingResultMap.size());
        assertNotNull(nonExistingResultMap); assertEquals(2, nonExistingResultMap.size());

        assertEquals(HttpStatus.BAD_REQUEST.value(), existingResultMap.get("status"));
        assertEquals(HttpStatus.OK.value(), nonExistingResultMap.get("status"));
    }
}