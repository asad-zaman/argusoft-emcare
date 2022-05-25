package com.argusoft.who.emcare.web.mail.impl;

import com.argusoft.who.emcare.web.mail.dao.MailRepository;
import com.argusoft.who.emcare.web.mail.dto.MailDto;
import com.argusoft.who.emcare.web.mail.entity.EmailContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailDataSetterServiceImpl implements MailDataSetterService {

    @Autowired
    MailRepository mailRepository;

    @Override
    public MailDto mailSubjectSetter(String code) {
        MailDto mailDto = new MailDto();
        EmailContent emailContent = mailRepository.findByCode(code);

        mailDto.setSubject(emailContent.getSubject());
        mailDto.setBody(emailContent.getContent());
        return mailDto;
    }
}
