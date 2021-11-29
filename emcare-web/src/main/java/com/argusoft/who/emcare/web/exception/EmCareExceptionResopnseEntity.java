package com.argusoft.who.emcare.web.exception;

import lombok.Data;

/**
 *
 * @author jay
 */
@Data
public class EmCareExceptionResopnseEntity {

    public String message;
    public Object data;
    public int errorcode;

    public EmCareExceptionResopnseEntity(String message) {
        this.message = message;
    }

    public EmCareExceptionResopnseEntity(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public EmCareExceptionResopnseEntity(String message, Object data, int errorcode) {
        this.message = message;
        this.data = data;
        this.errorcode = errorcode;
    }

}
