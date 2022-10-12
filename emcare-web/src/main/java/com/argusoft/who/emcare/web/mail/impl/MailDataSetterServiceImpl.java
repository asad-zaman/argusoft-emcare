package com.argusoft.who.emcare.web.mail.impl;

import com.argusoft.who.emcare.web.mail.dao.MailRepository;
import com.argusoft.who.emcare.web.mail.dto.MailDto;
import com.argusoft.who.emcare.web.mail.entity.EmailContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
        mailDto.setVarList(emailContent.getVarList());
        return mailDto;
    }

    @Override
    public String emailBodyCreator(Map<String, Object> mailData, String bodyContent, MailDto mailDto) {

        List<String> varList;
        if (mailDto.getVarList() != null) {
            varList = List.of(mailDto.getVarList().split(","));
        } else {
            return bodyContent;
        }
        for (String variable : varList) {
            bodyContent = bodyContent.replace("{{" + variable + "}}", getValueOfKeyByClass(variable, mailData));
        }

        return bodyContent;
    }

    private String getValueOfKeyByClass(String key, Map<String, Object> mailData) {
        return (String) mailData.get(key);
    }
}
