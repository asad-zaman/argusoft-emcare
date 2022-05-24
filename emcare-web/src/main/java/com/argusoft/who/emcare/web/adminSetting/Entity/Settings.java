package com.argusoft.who.emcare.web.adminSetting.Entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "settings")
public class Settings implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "setting_type", nullable = false)
    private String settingType;

    @Column(name = "setting_status", nullable = false)
    private Boolean settingStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSettingType() {
        return settingType;
    }

    public void setSettingType(String settingType) {
        this.settingType = settingType;
    }

    public Boolean getSettingStatus() {
        return settingStatus;
    }

    public void setSettingStatus(Boolean settingStatus) {
        this.settingStatus = settingStatus;
    }
}
