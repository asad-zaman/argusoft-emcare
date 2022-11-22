package com.argusoft.who.emcare.web.adminsetting.service.impl;

import com.argusoft.who.emcare.web.adminsetting.Entity.Settings;
import com.argusoft.who.emcare.web.adminsetting.dto.SettingDto;
import com.argusoft.who.emcare.web.adminsetting.repository.AdminSettingRepository;
import com.argusoft.who.emcare.web.adminsetting.service.AdminSettingService;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.config.KeyCloakConfig;
import com.argusoft.who.emcare.web.exception.EmCareException;
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
        return adminSettingRepository.findAllWithOrderById();
    }

    @Override
    public Settings updateAdminSettings(SettingDto settingDto) {
        try {
            Settings settings = new Settings();
            settings.setId(settingDto.getId());
            settings.setName(settingDto.getName());
            settings.setKey(settingDto.getKey());
            settings.setValue(settingDto.getValue());
            switch (settings.getKey()) {
                case CommonConstant.SETTING_TYPE_REGISTRATION_EMAIL_AS_USERNAME:
                    updateRegistrationEmailAsUsername(settings.getValue());

                    adminSettingRepository.save(settings);
                    break;
                case CommonConstant.SETTING_TYPE_WELCOME_EMAIL:
                case CommonConstant.SETTING_TYPE_SEND_CONFIRMATION_EMAIL:
                    adminSettingRepository.save(settings);
                    break;
                default:
                    throw new EmCareException("Case Not Found", new Exception());
            }
            return settings;
        } catch (Exception ex) {
            throw new EmCareException("Operation not successfully", ex);
        }
    }

    @Override
    public List<EmailContent> getAllMailTemplate() {
        return mailRepository.findAll();
    }

    @Override
    public Settings getAdminSettingByName(String settingName) {
        return adminSettingRepository.findByKey(settingName);
    }

    private void updateRegistrationEmailAsUsername(String status) {
        try {
            Keycloak keycloak = userService.getKeyCloakInstance();
            RealmResource realmResource = keycloak.realm(KeyCloakConfig.REALM);
            RealmRepresentation realmRepresentation = new RealmRepresentation();
            realmRepresentation.setRegistrationEmailAsUsername(false);
            if (CommonConstant.ACTIVE.equals(status)) {
                realmRepresentation.setRegistrationEmailAsUsername(true);
            }
            realmResource.update(realmRepresentation);
        } catch (Exception ex) {
            throw new EmCareException("Update registration of email as username is not performed", ex);
        }
    }
}
