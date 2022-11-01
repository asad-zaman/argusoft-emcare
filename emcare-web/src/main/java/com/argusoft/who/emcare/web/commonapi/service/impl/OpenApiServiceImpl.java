package com.argusoft.who.emcare.web.commonapi.service.impl;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.response.Response;
import com.argusoft.who.emcare.web.commonapi.dao.OTPRepository;
import com.argusoft.who.emcare.web.commonapi.dto.UserPasswordDto;
import com.argusoft.who.emcare.web.commonapi.model.OTP;
import com.argusoft.who.emcare.web.commonapi.service.OpenApiService;
import com.argusoft.who.emcare.web.mail.MailService;
import com.argusoft.who.emcare.web.mail.dto.MailDto;
import com.argusoft.who.emcare.web.mail.impl.MailDataSetterService;
import com.argusoft.who.emcare.web.twilio.service.TwilioService;
import com.argusoft.who.emcare.web.user.service.UserService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Transactional
@Service
public class OpenApiServiceImpl implements OpenApiService {

    @Autowired
    OTPRepository otpRepository;

    @Autowired
    MailService mailService;

    @Autowired
    UserService userService;

    @Autowired
    MailDataSetterService mailDataSetterService;

    @Autowired
    TwilioService twilioService;

    @Override
    public ResponseEntity<Object> generateOTP(String emailId) throws NoSuchAlgorithmException {
        UserRepresentation userRepresentation = userService.getUserByEmailId(emailId);
        if (userRepresentation == null) {
            return ResponseEntity.badRequest().body(new Response("Email id is not registered with em-care system", HttpStatus.BAD_REQUEST.value()));
        }
        otpRepository.deleteByEmailId(emailId);
        String newOTP = generateNewOTP();

        OTP otp = new OTP();
        otp.setOtpValue(newOTP);
        otp.setEmailId(emailId);
        otp.setExpiry(new Timestamp(new Date().getTime() + TimeUnit.MINUTES.toMillis(15)));
        otp.setVerified(false);
        otp.setCount(0);

        OTP savedOTP = otpRepository.save(otp);


        MailDto mailDto = mailDataSetterService.mailSubjectSetter(CommonConstant.MAIL_FOR_GENERATE_OTP);
        Map<String, Object> mailData = new HashMap<>();
        mailData.put("otp", newOTP);
        String mailBody = mailDataSetterService.emailBodyCreator(mailData, mailDto.getBody(), mailDto);
        mailService.sendBasicMail(emailId, mailDto.getSubject(), mailBody);
        return ResponseEntity.ok(savedOTP);
    }

    @Override
    public ResponseEntity<Object> verifyOTP(String emailId, String otp) {
        Optional<OTP> retrievOTP = otpRepository.retrievOTP(emailId);
        if (retrievOTP.isEmpty()) {
            return ResponseEntity.badRequest().body(new Response("Wrong OTP OR Change Password Request Isn't Initiated!", HttpStatus.BAD_REQUEST.value()));
        }
        OTP rOtp = retrievOTP.get();
        if (rOtp.getCount() <= 2 && rOtp.getOtpValue().equals(otp)) {
            return ResponseEntity.ok().body(new Response("OTP Successfully verified", HttpStatus.OK.value()));

        } else {
            otpRepository.updateOTPCount(rOtp.getCount() + 1, rOtp.getEmailId());
            if (rOtp.getCount() > 2) {
                return ResponseEntity.badRequest().body(new Response("OTP is expired. Please re-generate new OTP", HttpStatus.BAD_REQUEST.value()));
            }
            return ResponseEntity.badRequest().body(new Response("OTP is not matched", HttpStatus.BAD_REQUEST.value()));
        }
    }

    private String generateNewOTP() throws NoSuchAlgorithmException {
        Random rand = SecureRandom.getInstanceStrong();
        int otp = rand.nextInt(10000);
        return String.format("%04d",otp);
    }

    @Override
    public void invalidateOtp(String emailId, String otp) {
        otpRepository.invalidateOtp(otp, emailId);
    }

    @Override
    public ResponseEntity<Object> resetPassword(UserPasswordDto userPasswordDto) {
        Optional<OTP> retrievOTP = otpRepository.retrievOTP(userPasswordDto.getEmailId());
        OTP rOtp = null;
        if (!retrievOTP.isEmpty()) {
            rOtp = retrievOTP.get();
            if (Boolean.TRUE.equals(!rOtp.getVerified()) && rOtp.getOtpValue().equals(userPasswordDto.getOtp())) {
                userService.resetPassword(userPasswordDto.getEmailId(), userPasswordDto.getPassword());
                invalidateOtp(userPasswordDto.getEmailId(), userPasswordDto.getOtp());
                return ResponseEntity.ok().body(new Response("Password Reset Successfully", HttpStatus.OK.value()));
            } else {
                return ResponseEntity.badRequest().body(new Response("OTP Validation Failed, Retry", HttpStatus.BAD_REQUEST.value()));
            }
        } else {
            return ResponseEntity.badRequest().body(new Response("OTP Validation Failed, Retry", HttpStatus.BAD_REQUEST.value()));
        }
    }
}
