package com.argusoft.who.emcare.web.adminsetting.service;

import com.argusoft.who.emcare.web.adminsetting.dto.SettingDto;
import com.argusoft.who.emcare.web.adminsetting.entity.Settings;
import com.argusoft.who.emcare.web.adminsetting.repository.AdminSettingRepository;
import com.argusoft.who.emcare.web.adminsetting.service.impl.AdminSettingServiceImpl;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.config.KeyCloakConfig;
import com.argusoft.who.emcare.web.mail.dao.MailRepository;
import com.argusoft.who.emcare.web.mail.entity.EmailContent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {AdminSettingServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
class AdminSettingServiceTest {
    @Mock
    AdminSettingRepository adminSettingRepository;

    @Mock
    KeyCloakConfig keyCloakConfig;

    @Mock
    MailRepository mailRepository;

    @InjectMocks
    @Spy
    AdminSettingServiceImpl adminSettingService;

    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(adminSettingService, "realm", "master");
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testGetAdminSetting() {
        List<Settings> settingsList = new ArrayList<>();
        Settings list1 = new Settings();
        list1.setId(1L);
        list1.setName("test123");
        list1.setKey("key123");
        list1.setValue("111");

        Settings list2 = new Settings();
        list2.setId(2L);
        list2.setName("dummy");
        list2.setKey("tempkey");
        list2.setValue("123");

        settingsList.add(list1);
        settingsList.add(list2);

        when(adminSettingRepository.findAllWithOrderById()).thenReturn(settingsList);

        List<Settings> result = adminSettingService.getAdminSetting();

        assertEquals(settingsList, result);
        verify(adminSettingRepository, times(1)).findAllWithOrderById();
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Nested
    class testUpdateAdminSettings {
        Keycloak mockKeyCloak;
        RealmResource mockRealmResource;

        @BeforeEach
        void updateAdminSettingsSetUp() {
            mockKeyCloak = mock(Keycloak.class);
            mockRealmResource = mock(RealmResource.class);
            when(keyCloakConfig.getKeyCloakInstance()).thenReturn(mockKeyCloak);
            when(mockKeyCloak.realm(anyString())).thenReturn(mockRealmResource);
            when(adminSettingRepository.save(any(Settings.class))).thenAnswer(i -> i.getArgument(0));
        }

        @Test
        void updateWithREGISTRATION_EMAIL_AS_USERNAME() {
            SettingDto mockSettingDto = new SettingDto();
            mockSettingDto.setId(1L);
            mockSettingDto.setKey(CommonConstant.SETTING_TYPE_REGISTRATION_EMAIL_AS_USERNAME);
            mockSettingDto.setName("name");
            mockSettingDto.setValue("value");

            Settings actualSettings = adminSettingService.updateAdminSettings(mockSettingDto);
            verify(adminSettingRepository, times(1)).save(any(Settings.class));
            verify(mockRealmResource, times(1)).update(any());
            assertEquals(actualSettings.getId(), mockSettingDto.getId());
            assertEquals(actualSettings.getValue(), mockSettingDto.getValue());
            assertEquals(actualSettings.getKey(), mockSettingDto.getKey());
            assertEquals(actualSettings.getName(), mockSettingDto.getName());
        }

        @Test
        void updateWithWELCOME_EMAIL() {
            SettingDto mockSettingDto = new SettingDto();
            mockSettingDto.setId(1L);
            mockSettingDto.setKey(CommonConstant.SETTING_TYPE_WELCOME_EMAIL);
            mockSettingDto.setName("name");
            mockSettingDto.setValue("value");

            Keycloak mockKeyCloak = mock(Keycloak.class);
            RealmResource mockRealmResource = mock(RealmResource.class);
            when(keyCloakConfig.getKeyCloakInstance()).thenReturn(mockKeyCloak);
            when(mockKeyCloak.realm(anyString())).thenReturn(mockRealmResource);
            when(adminSettingRepository.save(any(Settings.class))).thenAnswer(i -> i.getArgument(0));

            Settings actualSettings = adminSettingService.updateAdminSettings(mockSettingDto);
            verify(adminSettingRepository, times(1)).save(any(Settings.class));
            verify(mockRealmResource, never()).update(any());
            assertEquals(actualSettings.getId(), mockSettingDto.getId());
            assertEquals(actualSettings.getValue(), mockSettingDto.getValue());
            assertEquals(actualSettings.getKey(), mockSettingDto.getKey());
            assertEquals(actualSettings.getName(), mockSettingDto.getName());
        }

        @Test
        void updateWithSEND_CONFIRMATION_EMAIL() {
            SettingDto mockSettingDto = new SettingDto();
            mockSettingDto.setId(1L);
            mockSettingDto.setKey(CommonConstant.SETTING_TYPE_SEND_CONFIRMATION_EMAIL);
            mockSettingDto.setName("name");
            mockSettingDto.setValue("value");

            Keycloak mockKeyCloak = mock(Keycloak.class);
            RealmResource mockRealmResource = mock(RealmResource.class);
            when(keyCloakConfig.getKeyCloakInstance()).thenReturn(mockKeyCloak);
            when(mockKeyCloak.realm(anyString())).thenReturn(mockRealmResource);
            when(adminSettingRepository.save(any(Settings.class))).thenAnswer(i -> i.getArgument(0));

            Settings actualSettings = adminSettingService.updateAdminSettings(mockSettingDto);
            verify(adminSettingRepository, times(1)).save(any(Settings.class));
            verify(mockRealmResource, never()).update(any());
            assertEquals(actualSettings.getId(), mockSettingDto.getId());
            assertEquals(actualSettings.getValue(), mockSettingDto.getValue());
            assertEquals(actualSettings.getKey(), mockSettingDto.getKey());
            assertEquals(actualSettings.getName(), mockSettingDto.getName());
        }
    }

    @Test
    void testGetAllMailTemplate() {
        List<EmailContent> emailContentList = new ArrayList<>();
        EmailContent obj1 = new EmailContent();
        obj1.setId(1L);
        obj1.setCode("ABC123");
        obj1.setSubject("tempSubject");
        obj1.setContent("ABC");
        obj1.setCreatedAt(new Date(System.currentTimeMillis()));
        obj1.setVarList("temp123");

        EmailContent obj2 = new EmailContent();
        obj2.setId(2L);
        obj2.setCode("sample");
        obj2.setSubject("sampleSubject");
        obj2.setContent("sample");
        obj2.setCreatedAt(new Date(System.currentTimeMillis()));
        obj2.setVarList("sample123");

        emailContentList.add(obj1);
        emailContentList.add(obj2);

        when(mailRepository.findAll()).thenReturn(emailContentList);

        List<EmailContent> result = adminSettingService.getAllMailTemplate();
        assertEquals(emailContentList, result);
        verify(mailRepository, times(1)).findAll();
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void getAdminSettingByName() {
        String settingName = "key123";

        List<Settings> settingsList = new ArrayList<>();
        Settings list1 = new Settings();
        list1.setId(1L);
        list1.setName("test123");
        list1.setKey("key123");
        list1.setValue("111");

        Settings list2 = new Settings();
        list2.setId(2L);
        list2.setName("dummy");
        list2.setKey("tempkey");
        list2.setValue("123");

        settingsList.add(list1);
        settingsList.add(list2);

        when(adminSettingRepository.findByKey(settingName)).thenReturn(list1);

        Settings result = adminSettingService.getAdminSettingByName(settingName);

        assertEquals(list1, result);
        verify(adminSettingRepository, times(1)).findByKey(settingName);
        assertEquals(1L, result.getId());
    }
}