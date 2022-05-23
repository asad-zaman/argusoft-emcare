package com.argusoft.who.emcare.web.adminSetting.controller;

import com.argusoft.who.emcare.web.adminSetting.Entity.Settings;
import com.argusoft.who.emcare.web.adminSetting.service.AdminSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api/admin")
public class AdminSettingController {

    @Autowired
    AdminSettingService adminSettingService;

    @GetMapping("/setting")
    public ResponseEntity<Object> getAdminPanelSetting() {
        return ResponseEntity.ok(adminSettingService.getAdminSetting());
    }

    @PutMapping("/update")
    public ResponseEntity<Object> getAdminPanelSetting(@RequestBody Settings settings) {
        return ResponseEntity.ok(adminSettingService.updateAdminSettings(settings));
    }

    @GetMapping("/mail/template")
    public ResponseEntity<Object> getAllMailTemplate() {
        return ResponseEntity.ok(adminSettingService.getAllMailTemplate());
    }
}
