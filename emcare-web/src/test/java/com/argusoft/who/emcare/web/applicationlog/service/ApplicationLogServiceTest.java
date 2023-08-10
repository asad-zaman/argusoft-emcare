package com.argusoft.who.emcare.web.applicationlog.service;
import com.argusoft.who.emcare.web.applicationlog.entity.ApplicationLog;
import com.argusoft.who.emcare.web.applicationlog.repository.ApplicationLogRepository;
import com.argusoft.who.emcare.web.applicationlog.service.impl.ApplicationLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationLogServiceTest {
    @Mock
    ApplicationLogRepository applicationLogRepository;
    @InjectMocks
    ApplicationLogServiceImpl applicationLogService;
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
    void addApplicationLog() {
    }

    @Test
    void testGetAllApplicationLogs() {

        // Mock data
        List<ApplicationLog> mockLogs = new ArrayList<>();
        mockLogs.add(createMockApplicationLog(1, "Log 1"));
        mockLogs.add(createMockApplicationLog(2, "Log 2"));

        when(applicationLogRepository.findAllByOrderByCreatedOnDesc()).thenReturn(mockLogs);

        List<ApplicationLog> result = applicationLogService.getAllApplicationLogs();

        assertEquals(2, result.size());
        assertEquals("Log 1", result.get(0).getLogs()[0]);
        assertEquals("Log 2", result.get(1).getLogs()[0]);

        for (int i = 1; i < result.size(); i++) {
            assertThat(result.get(i).getCreatedOn()).isBeforeOrEqualTo(result.get(i - 1).getCreatedOn());
        }

        verify(applicationLogRepository, times(1)).findAllByOrderByCreatedOnDesc();
    }

    @Test
    void testGetLatestApplicationLogs() {
        // Mock data
        ApplicationLog mockLog = createMockApplicationLog(1, "Latest Log");

        when(applicationLogRepository.getLatestOne()).thenReturn(mockLog);

        ApplicationLog result = applicationLogService.getLatestApplicationLogs();

        assertEquals("Latest Log", result.getLogs()[0]);

        verify(applicationLogRepository, times(1)).getLatestOne();

    }

    private ApplicationLog createMockApplicationLog(Integer id, String log) {
        ApplicationLog applicationLog = new ApplicationLog();
        applicationLog.setId(id);
        applicationLog.setApplicationName("Test Application");
        applicationLog.setApplicationVersion("1.0");
        applicationLog.setLogs(new String[]{log});
        applicationLog.setUrl("/path/to/application");
        applicationLog.setCreatedOn(new Date());
        applicationLog.setCreatedBy("test_user");
        return applicationLog;
    }
}