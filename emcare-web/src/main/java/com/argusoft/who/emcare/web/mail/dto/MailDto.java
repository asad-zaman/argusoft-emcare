package com.argusoft.who.emcare.web.mail.dto;

public class MailDto {

    private String subject;
    private String body;
    private String varList;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getVarList() {
        return varList;
    }

    public void setVarList(String varList) {
        this.varList = varList;
    }
}
