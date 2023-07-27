package com.argusoft.who.emcare.web.adminsetting.repository;

import com.argusoft.who.emcare.web.adminsetting.entity.Settings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ContextConfiguration(classes = AdminSettingRepository.class)
class AdminSettingRepositoryTest {

    @Autowired
    private AdminSettingRepository adminSettingRepository;

    private Settings settings;

    @BeforeEach
    void setUp() {
        settings = new Settings();
        for(int i = 0; i < settingsData.size(); i++) {
            settings.setId((Long) settingsData.get(i).get("id"));
            settings.setKey((String) settingsData.get(i).get("key"));
            settings.setName((String) settingsData.get(i).get("name"));
            settings.setValue((String) settingsData.get(i).get("value"));
            adminSettingRepository.save(settings);
        }
    }

    @AfterEach
    void tearDown() {
        settings = null;
        adminSettingRepository.deleteAll();
    }

//    @Test
    void findAllWithOrderById() {
        List<Settings> result = adminSettingRepository.findAllWithOrderById();
        assertEquals(result.size(), 3);
        for(int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i).getId() > result.get(i - 1).getId());
        }
    }

    List<Map<String, Object>> settingsData = new ArrayList<>() {{
        add(new HashMap<>(){{
            put("id", 1L);
            put("key", "REGISTRATION_EMAIL_AS_USERNAME");
            put("name", "Registration email as username");
            put("value", "Inactive");
        }});

        add(new HashMap<>(){{
            put("id", 2L);
            put("key", "WELCOME_EMAIL");
            put("name", "Welcome email");
            put("value", "Active");
        }});

        add(new HashMap<>(){{
            put("id", 3L);
            put("key", "SEND_CONFIRMATION_EMAIL");
            put("name", "Send confirmation email");
            put("value", "Active");
        }});
    }};
}