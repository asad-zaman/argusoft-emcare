package com.argusoft.who.emcare.web.twilio.service.impl;

import com.argusoft.who.emcare.web.twilio.service.TwilioService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioServiceImpl implements TwilioService {

    @Value("${twilio.account.ssid}")
    private String twilioAccountSid;

    @Value("${twilio.account.token}")
    private String twilioAccountToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    @Value("${twilio.messaging.service.id}")
    private String twilioMessageServiceId;


    @Override
    public void sendSms(String to, String content) {
        sendMessage(to, content);
    }

    private void sendMessage(String to, String content) {
        Twilio.init(twilioAccountSid, twilioAccountToken);
        Message.creator(new com.twilio.type.PhoneNumber(to), twilioMessageServiceId, content).create();
    }
}
