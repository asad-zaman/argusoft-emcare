package com.argusoft.who.emcare.web.commonapi.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ContextConfiguration(classes = {UserPasswordDto.class})
public class UserPasswordDtoTest {

    private UserPasswordDto userPasswordDto;

    @BeforeEach
    public void setUp(){
        userPasswordDto = new UserPasswordDto();
    }

    @Test
    public void testSetAndGetEmailId(){
        String emailId = "abc@gmail.com";
        userPasswordDto.setEmailId(emailId);
        assertEquals(emailId, userPasswordDto.getEmailId());
    }

    @Test
    public void testSetAndGetOtp(){
        String otp = "newOtp";
        userPasswordDto.setOtp(otp);
        assertEquals(otp, userPasswordDto.getOtp());
    }

    @Test
    public void testSetAndGetPassword(){
        String password = "Password";
        userPasswordDto.setPassword(password);
        assertEquals(password, userPasswordDto.getPassword());
    }
}
