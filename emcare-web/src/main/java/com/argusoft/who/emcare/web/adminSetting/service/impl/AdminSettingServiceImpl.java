package com.argusoft.who.emcare.web.adminSetting.service.impl;

import com.argusoft.who.emcare.web.adminSetting.Entity.Settings;
import com.argusoft.who.emcare.web.adminSetting.repository.AdminSettingRepository;
import com.argusoft.who.emcare.web.adminSetting.service.AdminSettingService;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.config.KeyCloakConfig;
import com.argusoft.who.emcare.web.mail.dao.MailRepository;
import com.argusoft.who.emcare.web.mail.entity.EmailContent;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class AdminSettingServiceImpl implements AdminSettingService {

    @Autowired
    AdminSettingRepository adminSettingRepository;

    @Autowired
    KeyCloakConfig keyCloakConfig;

    @Autowired
    UserService userService;

    @Autowired
    MailRepository mailRepository;

    @Override
    public List<Settings> getAdminSetting() {
        return adminSettingRepository.findAll();
    }

    @Override
    public Settings updateAdminSettings(Settings settings) {
        try {
            switch (settings.getSettingType()) {
                case CommonConstant.SETTING_TYPE_REGISTRATION_EMAIL_AS_USERNAME:
                    updateRegistrationEmailAsUsername(settings.getSettingStatus());
                    adminSettingRepository.save(settings);
                    break;
                case CommonConstant.SETTING_TYPE_WELCOME_EMAIL:
                case CommonConstant.SETTING_TYPE_SEND_CONFIRMATION_EMAIL:
                    adminSettingRepository.save(settings);
                    break;
                default:
                    throw new RuntimeException();
            }
            return settings;
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    @Override
    public List<EmailContent> getAllMailTemplate() {
        return mailRepository.findAll();
    }

    @Override
    public Settings getAdminSettingByName(String settingName) {
        return adminSettingRepository.findBySettingType(settingName);
    }

    private void updateRegistrationEmailAsUsername(Boolean status) {
        try {
            Keycloak keycloak = userService.getKeyCloakInstance();
            RealmResource realmResource = keycloak.realm(KeyCloakConfig.REALM);
            RealmRepresentation realmRepresentation = new RealmRepresentation();
            realmRepresentation.setRegistrationEmailAsUsername(status);
            realmResource.update(realmRepresentation);
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }
}
