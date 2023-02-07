package com.argusoft.who.emcare.web.exception;

/**
 *
 * @author jay
 */
public class EmCareException extends RuntimeException {

    private final EmCareExceptionResponseEntity emCareExceptionResponseEntity;

    public EmCareException(String message, Exception exception) {
        super(message, exception);
        this.emCareExceptionResponseEntity = new EmCareExceptionResponseEntity(message);
    }

    public EmCareException(String message, int errorCode) {
        super(message);
        this.emCareExceptionResponseEntity = new EmCareExceptionResponseEntity(message, errorCode);
    }

    public EmCareException(String message, int errorCode, Object data) {
        super(message);
        this.emCareExceptionResponseEntity = new EmCareExceptionResponseEntity(message, data, errorCode);
    }

    public EmCareExceptionResponseEntity getResponse() {
        return emCareExceptionResponseEntity;
    }
}
