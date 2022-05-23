package com.argusoft.who.emcare.web.adminSetting.service;

import com.argusoft.who.emcare.web.adminSetting.Entity.Settings;
import com.argusoft.who.emcare.web.mail.entity.EmailContent;

import java.util.List;

public interface AdminSettingService {

    public List<Settings> getAdminSetting();

    public Settings updateAdminSettings(Settings settings);

    public List<EmailContent> getAllMailTemplate();


}
