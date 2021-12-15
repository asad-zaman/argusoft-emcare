package com.argusoft.who.emcare.web.user.dto;

public class UserUpdateDto {

    private String userId;
    private boolean isEnabled;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
