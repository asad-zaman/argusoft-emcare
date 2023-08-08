package com.argusoft.who.emcare.web.mail.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = {MailDto.class})
public class MailDtoTest {

    private MailDto mailDto;

    @BeforeEach
    public void setUp(){
        mailDto = new MailDto();
    }

    @Test
    public void testSubject(){
        String subject = "Subject1";
        mailDto.setSubject(subject);
        assertEquals(subject, mailDto.getSubject());
    }

    @Test
    public void testBody(){
        String body = "Body1";
        mailDto.setBody(body);
        assertEquals(body, mailDto.getBody());
    }

    @Test
    public void testVarList(){
        String varList = "List1";
        mailDto.setVarList(varList);
        assertEquals(varList, mailDto.getVarList());
    }
}
