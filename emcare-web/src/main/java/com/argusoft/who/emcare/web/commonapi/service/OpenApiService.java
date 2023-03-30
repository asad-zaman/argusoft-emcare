package com.argusoft.who.emcare.web.commonapi.service;

import com.argusoft.who.emcare.web.commonapi.dto.UserPasswordDto;
import com.argusoft.who.emcare.web.tenant.dto.TenantDto;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface OpenApiService {

    public ResponseEntity<Object> generateOTP(String emailId) throws NoSuchAlgorithmException;

    public ResponseEntity<Object> verifyOTP(String emailId, String otp);

    public void invalidateOtp(String emailId, String otp);

    public ResponseEntity<Object> resetPassword(UserPasswordDto userPasswordDto);

    public Map<String, String> getCurrentCountry(HttpServletRequest request);
}
