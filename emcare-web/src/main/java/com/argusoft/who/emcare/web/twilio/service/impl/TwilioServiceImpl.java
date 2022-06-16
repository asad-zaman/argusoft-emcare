package com.argusoft.who.emcare.web.twilio.service.impl;

import com.argusoft.who.emcare.web.twilio.service.TwilioService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioServiceImpl implements TwilioService {

    @Value("${twilio.account.ssid}")
    private String TWILIO_ACCOUNT_SID;

    @Value("${twilio.account.token}")
    private String TWILIO_ACCOUNT_TOKEN;

    @Value("${twilio.phone.number}")
    private String TWILIO_PHONE_NUMBER;

    @Value("${twilio.messaging.service.id}")
    private String TWILIO_MESSAGE_SERVICE_ID;


    @Override
    public void sendSms(String to, String content) {
        //thePatient.getTelecom().get(0).getValue()
        sendMessage(to, content);
    }

    private void sendMessage(String to, String content) {
        Twilio.init(TWILIO_ACCOUNT_SID, TWILIO_ACCOUNT_TOKEN);
        Message message = Message.creator(new com.twilio.type.PhoneNumber("+919979943100"), TWILIO_MESSAGE_SERVICE_ID, content).create();

        System.out.println(message.getSid());
    }
}
