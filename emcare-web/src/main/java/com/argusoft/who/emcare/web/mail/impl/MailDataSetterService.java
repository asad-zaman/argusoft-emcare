package com.argusoft.who.emcare.web.mail.impl;

import com.argusoft.who.emcare.web.mail.dto.MailDto;

import java.util.Map;

public interface MailDataSetterService {

    public MailDto mailSubjectSetter(String code);

    public String emailBodyCreator(Map<String,Object> mailData , String bodyContent, MailDto mailDto);
}
