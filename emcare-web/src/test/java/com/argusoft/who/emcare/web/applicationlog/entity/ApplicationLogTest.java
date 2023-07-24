package com.argusoft.who.emcare.web.applicationlog.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meanbean.test.BeanTester;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ApplicationLogTest {


    private ApplicationLog applicationLog;

    @BeforeEach
    public void setUp() {
        applicationLog = new ApplicationLog();
    }

    @Test
    public void testIdGetterAndSetter() {
        Integer id = 1;
        applicationLog.setId(id);
        assertEquals(id, applicationLog.getId());
    }

    @Test
    public void testApplicationNameGetterAndSetter() {
        String applicationName = "Sample Application";
        applicationLog.setApplicationName(applicationName);
        assertEquals(applicationName, applicationLog.getApplicationName());
    }

    @Test
    public void testApplicationVersionGetterAndSetter() {
        String applicationVersion = "1.0.0";
        applicationLog.setApplicationVersion(applicationVersion);
        assertEquals(applicationVersion, applicationLog.getApplicationVersion());
    }

    @Test
    public void testLogsGetterAndSetter() {
        String[] logs = {"Log 1", "Log 2", "Log 3"};
        applicationLog.setLogs(logs);
        assertEquals(logs, applicationLog.getLogs());
    }

    @Test
    public void testUrlGetterAndSetter() {
        String url = "http://example.com";
        applicationLog.setUrl(url);
        assertEquals(url, applicationLog.getUrl());
    }

}
