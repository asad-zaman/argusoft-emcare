package com.argusoft.who.emcare.web.adminsetting.service;

import com.argusoft.who.emcare.web.adminsetting.entity.Settings;
import com.argusoft.who.emcare.web.adminsetting.repository.AdminSettingRepository;
import com.argusoft.who.emcare.web.adminsetting.service.impl.AdminSettingServiceImpl;
import com.argusoft.who.emcare.web.config.KeyCloakConfig;
import com.argusoft.who.emcare.web.mail.dao.MailRepository;
import com.argusoft.who.emcare.web.mail.entity.EmailContent;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminSettingServiceTest {
    @Mock
    private AdminSettingRepository adminSettingRepository;

    @Mock
    UserService userService;

    @Mock
    KeyCloakConfig keyCloakConfig;

    @Mock
    MailRepository mailRepository;

    @Mock
    Settings settings;

    @InjectMocks
    private AdminSettingServiceImpl adminSettingService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
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

        assertEquals(settingsList,result);
        verify(adminSettingRepository,times(1)).findAllWithOrderById();
        assertEquals(1L,result.get(0).getId());
        assertEquals(2L,result.get(1).getId());
    }

    @Test
    void updateAdminSettings() {

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
        assertEquals(emailContentList,result);
        verify(mailRepository,times(1)).findAll();
        assertEquals(1L,result.get(0).getId());
        assertEquals(2L,result.get(1).getId());
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

        assertEquals(list1,result);
        verify(adminSettingRepository,times(1)).findByKey(settingName);
        assertEquals(1L,result.getId());
    }

    @Test
    void testUpdateRegistrationEmailAsUsername(){

    }
}