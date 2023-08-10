package com.argusoft.who.emcare.web.mail.entity;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = {EmailContent.class})
public class EmailContentTest {

    private EmailContent emailContent;

    @BeforeEach
    public void setUp(){
        emailContent = new EmailContent();
    }

    @Test
    public void testSubject(){
        String subject = "Subject1";
        emailContent.setSubject(subject);
        assertEquals(subject, emailContent.getSubject());
    }

    @Test
    public void testBody(){
        String body = "Body1";
        emailContent.setContent(body);
        assertEquals(body, emailContent.getContent());
    }

    @Test
    public void testVarList(){
        String varList = "List1";
        emailContent.setVarList(varList);
        assertEquals(varList, emailContent.getVarList());
    }

    @Test
    public void testCode(){
        String code = "code1";
        emailContent.setCode(code);
        assertEquals(code, emailContent.getCode());
    }

    @Test
    public void testId(){
        Long id = 1L;
        emailContent.setId(id);
        assertEquals(id, emailContent.getId());
    }


    @Test
    public void testCreatedAt(){
        Date date = new Date();
        emailContent.setCreatedAt(date);
        assertEquals(date, emailContent.getCreatedAt());
    }
}
