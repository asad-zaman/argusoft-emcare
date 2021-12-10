package com.argusoft.who.emcare.web.exception;

/**
 *
 * @author jay
 */
public class EmCareException extends RuntimeException {

    EmCareExceptionResopnseEntity agdRes;

    public EmCareException(String message, Exception exception) {
        super(message, exception);
        this.agdRes = new EmCareExceptionResopnseEntity(message);
    }

    public EmCareException(String message, int errorCode) {
        super(message);
        this.agdRes = new EmCareExceptionResopnseEntity(message, errorCode);
    }

    public EmCareException(String message, int errorCode, Object data) {
        super(message);
        this.agdRes = new EmCareExceptionResopnseEntity(message, data, errorCode);
    }

    public EmCareExceptionResopnseEntity getResponse() {
        return agdRes;
    }
}
