package com.argusoft.who.emcare.web.mail.impl;

import com.argusoft.who.emcare.web.exception.EmCareException;
import com.argusoft.who.emcare.web.mail.MailService;
import com.argusoft.who.emcare.web.mail.dao.MailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class MailServiceImpl implements MailService {

    @Value("${spring.mail.username}")
    private String emailSentFrom;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    MailRepository mailRepository;


    @Async
    @Override
    public void sendBasicMail(String to, String subject, String bodyContent) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                mimeMessage.setFrom(new InternetAddress(emailSentFrom));
                mimeMessage.setSubject(subject);
                mimeMessage.setText(bodyContent, "text/html; charset=utf-8");
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setText(bodyContent, true);

            }
        };

        try {
            javaMailSender.send(preparator);
        } catch (MailException ex) {
            throw new EmCareException("Mail not sent", ex);
        }

    }
}
