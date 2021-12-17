package com.argusoft.who.emcare.web.common.response;

import org.springframework.http.HttpStatus;

public class Response {

    private String errorMessage;
    private Integer statusCode;

    public Response(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Response(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Response(String errorMessage, Integer statusCode) {
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}
