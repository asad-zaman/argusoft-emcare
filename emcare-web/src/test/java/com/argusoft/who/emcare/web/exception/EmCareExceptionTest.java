package com.argusoft.who.emcare.web.exception;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
class EmCareExceptionTest {
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
    public void testConstructorWithMessageAndException() {
        String message = "Test Exception with Exception";
        Exception exception = new RuntimeException("Root cause");

        EmCareException emCareException = new EmCareException(message, exception);

        assertEquals(message, emCareException.getMessage());
        assertNotNull(emCareException.getCause());
        assertEquals(exception, emCareException.getCause());
        assertThat(emCareException.getResponse()).isNotNull();
        assertEquals(message, emCareException.getResponse().getMessage());
    }
    @Test
    public void testConstructorWithMessageErrorCodeAndData() {
        String message = "Test Exception with ErrorCode and Data";
        int errorCode = 500;
        Object data = "Some Data";

        EmCareException emCareException = new EmCareException(message, errorCode, data);

        assertEquals(message, emCareException.getMessage());
        assertThat(emCareException.getResponse()).isNotNull();
        assertEquals(message, emCareException.getResponse().getMessage());
        assertEquals(errorCode, emCareException.getResponse().getErrorCode());
        assertEquals(data, emCareException.getResponse().getData());
    }

    @Test
    public void testGetResponse() {
        String message = "Test GetResponse method";
        int errorCode = 404;
        Object data = "Test Data";
        EmCareException emCareException = new EmCareException(message, errorCode, data);

        EmCareExceptionResponseEntity response = emCareException.getResponse();

        assertNotNull(response);
        assertEquals(message, response.getMessage());
        assertEquals(errorCode, response.getErrorCode());
        assertEquals(data, response.getData());
    }
}