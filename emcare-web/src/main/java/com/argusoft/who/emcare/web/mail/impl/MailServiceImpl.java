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
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;

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
    public void sendBasicMail(String to, String mailType) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        // Configure API key authorization: api-key
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey(sendInBlueSecureKey);
        EmailContent emailContent = mailRepository.findByCode(mailType);

//        TransactionalEmailsApi api = new TransactionalEmailsApi();
//        SendSmtpEmailSender sender = new SendSmtpEmailSender();
//        sender.setEmail("who.emcare@gmail.com");
//        sender.setName("em care");
//        List<SendSmtpEmailTo> toList = new ArrayList<>();
//        SendSmtpEmailTo to = new SendSmtpEmailTo();
//        to.setEmail("jaykalariya100@gmail.com");
//        to.setName("jay kalariya");
//        toList.add(to);
//        SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
//        sendSmtpEmail.setHtmlContent("<html><body><h1>This is my first transactional email</h1></body></html>");
//        sendSmtpEmail.setSubject("My Subject");
//        sendSmtpEmail.setSender(sender);
//        sendSmtpEmail.setTo(toList);

        try {
//            CreateSmtpEmail response = api.sendTransacEmail(sendSmtpEmail);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("jaykalariya100@gmail.com");
            message.setTo(to);
            message.setSubject("Registration Successfully");
            message.setText(emailContent.getContent() + " " + to);
            javaMailSender.send(message);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }
}
