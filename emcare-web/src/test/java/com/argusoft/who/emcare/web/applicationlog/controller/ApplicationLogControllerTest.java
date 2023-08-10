package com.argusoft.who.emcare.web.applicationlog.controller;

import com.argusoft.who.emcare.web.applicationlog.entity.ApplicationLog;
import com.argusoft.who.emcare.web.applicationlog.service.ApplicationLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApplicationLogControllerTest {

    @Mock
    private ApplicationLogService applicationLogService;

    @InjectMocks
    private ApplicationLogController applicationLogController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddNewApplication() throws Exception {
        String logData = "{\"message\":\"Application Log Data\"}";
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", "Test data".getBytes(StandardCharsets.UTF_8));
        when(applicationLogService.addApplicationLog(any(), any())).thenReturn(ResponseEntity.ok().body("Successfully Uploaded"));

        ResponseEntity<Object> response = applicationLogController.addNewApplication(multipartFile, logData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully Uploaded", response.getBody());
        verify(applicationLogService, times(1)).addApplicationLog(any(), any());
    }

    @Test
    void testGetAllApplicationLog() throws Exception {
        List<ApplicationLog> applicationLogs = List.of(
                createApplicationLog("Application 1", "log data 1"),
                createApplicationLog("Application 2", "log data 2")
        );
        when(applicationLogService.getAllApplicationLogs()).thenReturn(applicationLogs);

        ResponseEntity<Object> response = applicationLogController.getAllApplicationLog();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicationLogs, response.getBody());
        verify(applicationLogService, times(1)).getAllApplicationLogs();
    }

    @Test
    void testGetLatestApplicationLog() throws Exception {
        ApplicationLog applicationLog = createApplicationLog("Latest Application", "latest log data");
        when(applicationLogService.getLatestApplicationLogs()).thenReturn(applicationLog);

        // Perform the test
        ResponseEntity<ApplicationLog> response = applicationLogController.getLatestApplicationLog();

        // Verify the results
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicationLog, response.getBody());
        verify(applicationLogService, times(1)).getLatestApplicationLogs();
    }

    private ApplicationLog createApplicationLog(String applicationName, String logData) {
        ApplicationLog applicationLog = new ApplicationLog();
        applicationLog.setApplicationName(applicationName);
        applicationLog.setLogs(new String[]{logData});
        return applicationLog;
    }
}