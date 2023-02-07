package com.argusoft.who.emcare.web.exception;

import lombok.Data;

/**
 * @author jay
 */
@Data
public class EmCareExceptionResponseEntity {

    private String message;
    private Object data;
    private int errorCode;

    public EmCareExceptionResponseEntity(String message) {
        this.message = message;
    }

    public EmCareExceptionResponseEntity(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public EmCareExceptionResponseEntity(String message, Object data, int errorCode) {
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
    }

}
