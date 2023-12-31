package com.argusoft.who.emcare.web.admin_setting.service;

import com.argusoft.who.emcare.web.admin_setting.entity.Settings;
import com.argusoft.who.emcare.web.admin_setting.dto.SettingDto;
import com.argusoft.who.emcare.web.mail.entity.EmailContent;

import java.util.List;

public interface AdminSettingService {

    public List<Settings> getAdminSetting();

    public Settings updateAdminSettings(SettingDto settingDto);

    public List<EmailContent> getAllMailTemplate();

    public Settings getAdminSettingByName(String settingName);


}
