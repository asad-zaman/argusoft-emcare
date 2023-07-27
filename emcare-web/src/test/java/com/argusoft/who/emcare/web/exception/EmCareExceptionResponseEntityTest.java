package com.argusoft.who.emcare.web.exception;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class EmCareExceptionResponseEntityTest {

    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();

    }
    @Test
    public void testConstructorWithMessage() {
        String message = "This is a test message";

        EmCareExceptionResponseEntity responseEntity = new EmCareExceptionResponseEntity(message);

        assertEquals(message, responseEntity.getMessage());
        assertNull(responseEntity.getData());
        assertEquals(0, responseEntity.getErrorCode());
    }

    @Test
    public void testConstructorWithMessageAndData() {
        String message = "Test by passing both message and data";
        Object data = "Test for this method is passed as string";

        EmCareExceptionResponseEntity responseEntity = new EmCareExceptionResponseEntity(message, data);

        assertEquals(message, responseEntity.getMessage());
        assertEquals(data, responseEntity.getData());
        assertEquals(0, responseEntity.getErrorCode());
    }

    @Test
    public void testConstructorWithMessageDataAndErrorCode() {
        String message = "Test by passing test data";
        Object data = "Test data is passed as string";
        int errorCode = 404;

        EmCareExceptionResponseEntity responseEntity = new EmCareExceptionResponseEntity(message, data, errorCode);

        assertEquals(message, responseEntity.getMessage());
        assertEquals(data, responseEntity.getData());
        assertEquals(errorCode, responseEntity.getErrorCode());
    }

}