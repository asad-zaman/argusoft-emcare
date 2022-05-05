package com.argusoft.who.emcare.web.commonApi.service;

import com.argusoft.who.emcare.web.commonApi.dto.UserPasswordDto;
import org.springframework.http.ResponseEntity;

public interface OpenApiService {

    public ResponseEntity<?> generateOTP(String emailId);

    public ResponseEntity<?> verifyOTP(String emailId, String otp);

    public void invalidateOtp(String emailId, String otp);

    public ResponseEntity<?> resetPassword(UserPasswordDto userPasswordDto);
}
