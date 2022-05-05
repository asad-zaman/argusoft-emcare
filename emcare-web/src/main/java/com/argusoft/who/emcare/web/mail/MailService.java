package com.argusoft.who.emcare.web.mail;

public interface MailService {

    public void sendBasicMail(String to, String mailType, String bodyContent);
}
