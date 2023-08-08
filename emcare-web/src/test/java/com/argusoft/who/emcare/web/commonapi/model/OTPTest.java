package com.argusoft.who.emcare.web.commonapi.model;

import java.sql.Timestamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = {OTP.class})
public class OTPTest {

    private OTP otp;

    @BeforeEach
    public void setUp(){
        otp = new OTP();
    }

    @Test
    public void testSetId(){

        Long id = 1L;
        otp.setId(id);
        assertEquals(id, otp.getId());
    }

    @Test
    public void testSetEmailId(){

        String emailId = "abc@gmail.com";
        otp.setEmailId(emailId);
        assertEquals(emailId, otp.getEmailId());
    }

    @Test
    public void testSetOtpValue(){

        String otpValue = "OTP";
        otp.setOtpValue(otpValue);
        assertEquals(otpValue, otp.getOtpValue());
    }

    @Test
    public void testSetExpiry(){

        Timestamp expiry = Timestamp.valueOf("2022-05-16 14:46:14.14");
        otp.setExpiry(expiry);
        assertEquals(expiry, otp.getExpiry());
    }

    @Test
    public void testSetVerified(){

        Boolean verified = true;
        otp.setVerified(verified);
        assertEquals(verified,otp.getVerified());
    }

    @Test
    public void testSetCount(){

        Integer count = 10;
        otp.setCount(count);
        assertEquals(count,otp.getCount());
    }

}
