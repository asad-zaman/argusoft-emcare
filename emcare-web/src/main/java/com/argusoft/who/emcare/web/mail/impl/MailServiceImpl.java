package com.argusoft.who.emcare.web.mail.impl;

import com.argusoft.who.emcare.web.mail.MailService;
import com.argusoft.who.emcare.web.mail.dao.MailRepository;
import com.argusoft.who.emcare.web.mail.entity.EmailContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    @Value("${sendinblue.key}")
    private String sendInBlueSecureKey;

    @Value("${spring.mail.username}")
    private String emailSentFrom;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    MailRepository mailRepository;

    @Async
    @Override
    public void sendBasicMail(String to, String mailType, String bodyContent) {

        EmailContent emailContent = mailRepository.findByCode(mailType);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("jaykalariya100@gmail.com");
            message.setTo(to);
            message.setSubject(emailContent.getSubject());
            message.setText(emailContent.getContent() + " " + bodyContent);
            javaMailSender.send(message);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }
}
