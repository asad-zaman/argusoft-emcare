package com.argusoft.who.emcare.web.commonApi.controller;

import com.argusoft.who.emcare.web.commonApi.dto.UserPasswordDto;
import com.argusoft.who.emcare.web.commonApi.service.OpenApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "**")
@RequestMapping("/api/open")
public class OpenApiController {

    @Autowired
    OpenApiService openApiService;

    @PostMapping("/forgotpassword/generateotp")
    public ResponseEntity<?> generateOtp(@RequestBody UserPasswordDto userPasswordDto) {
        return openApiService.generateOTP(userPasswordDto.getEmailId());
    }

    @PostMapping("/forgotpassword/verifyotp")
    public ResponseEntity<?> verifyOTP(@RequestBody UserPasswordDto userPasswordDto) {
        return openApiService.verifyOTP(userPasswordDto.getEmailId(), userPasswordDto.getOtp());
    }

    @PutMapping("/resetpassword")
    public ResponseEntity<?> resetPassword(@RequestBody UserPasswordDto userPasswordDto) {
        return openApiService.resetPassword(userPasswordDto);
    }
}
