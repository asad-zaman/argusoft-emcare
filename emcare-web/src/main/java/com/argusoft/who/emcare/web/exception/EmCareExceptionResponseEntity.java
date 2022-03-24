package com.argusoft.who.emcare.web.exception;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jay
 */
@Data
public class EmCareExceptionResponseEntity implements Serializable {

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
