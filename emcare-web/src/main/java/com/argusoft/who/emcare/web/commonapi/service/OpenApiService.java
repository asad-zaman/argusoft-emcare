package com.argusoft.who.emcare.web.commonapi.service;

import com.argusoft.who.emcare.web.commonapi.dto.UserPasswordDto;
import org.springframework.http.ResponseEntity;

import java.security.NoSuchAlgorithmException;

public interface OpenApiService {

    public ResponseEntity<Object> generateOTP(String emailId) throws NoSuchAlgorithmException;

    public ResponseEntity<Object> verifyOTP(String emailId, String otp);

    public void invalidateOtp(String emailId, String otp);

    public ResponseEntity<Object> resetPassword(UserPasswordDto userPasswordDto);
}
