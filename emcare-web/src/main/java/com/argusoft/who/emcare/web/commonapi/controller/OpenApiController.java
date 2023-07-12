package com.argusoft.who.emcare.web.commonapi.controller;

import com.argusoft.who.emcare.web.applicationlog.entity.ApplicationLog;
import com.argusoft.who.emcare.web.applicationlog.service.ApplicationLogService;
import com.argusoft.who.emcare.web.commonapi.dto.UserPasswordDto;
import com.argusoft.who.emcare.web.commonapi.service.OpenApiService;
import com.argusoft.who.emcare.web.exception.EmCareException;
import com.argusoft.who.emcare.web.fhir.dto.FacilityDto;
import com.argusoft.who.emcare.web.fhir.service.LocationResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "**")
@RequestMapping("/api/open")
public class OpenApiController {

    @Autowired
    OpenApiService openApiService;

    @Autowired
    LocationResourceService locationResourceService;

    @Autowired
    ApplicationLogService applicationLogService;

    @PostMapping("/forgotpassword/generateotp")
    public ResponseEntity<Object> generateOtp(@RequestBody UserPasswordDto userPasswordDto) {
        try {
            return openApiService.generateOTP(userPasswordDto.getEmailId());
        } catch (NoSuchAlgorithmException e) {
            throw new EmCareException("Opt not created", e);
        }
    }

    @PostMapping("/forgotpassword/verifyotp")
    public ResponseEntity<Object> verifyOTP(@RequestBody UserPasswordDto userPasswordDto) {
        return openApiService.verifyOTP(userPasswordDto.getEmailId(), userPasswordDto.getOtp());
    }

    @PutMapping("/resetpassword")
    public ResponseEntity<Object> resetPassword(@RequestBody UserPasswordDto userPasswordDto) {
        return openApiService.resetPassword(userPasswordDto);
    }

    @GetMapping("/active/facility")
    public List<FacilityDto> getActiveFacility() {
        return locationResourceService.getActiveFacility();
    }

    @GetMapping("/current/country")
    public Map<String, String> getCurrentCountry(HttpServletRequest request) {
        return openApiService.getCurrentCountry(request);
    }

    @GetMapping("/country/list")
    public List<String> getCountryList() {
        return openApiService.getCountryList();
    }

    @GetMapping("/country/global/app")
    public ResponseEntity<ApplicationLog> getLatestApplication() {
        return ResponseEntity.ok().body(applicationLogService.getLatestApplicationLogs());
    }

    @GetMapping("/country/application/log/all")
    public ResponseEntity<Object> getAllApplicationLog() throws Exception {
        return ResponseEntity.ok().body(applicationLogService.getAllApplicationLogs());
    }
}
