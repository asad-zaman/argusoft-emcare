package com.argusoft.who.emcare.web.applicationlog.repository;

import com.argusoft.who.emcare.web.applicationlog.entity.ApplicationLog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ApplicationLogRepositoryTest {

    @Mock
    ApplicationLogRepository applicationLogRepository;
    List<ApplicationLog> applicationLogs = new ArrayList<>();
    AutoCloseable autoCloseable;
    @BeforeEach
    void setUp() {
        // Mock data
        ApplicationLog mockLog1 = createMockApplicationLog(1, "Latest Log 1", new Date(1680000120000L));
        ApplicationLog mockLog2 = createMockApplicationLog(2, "Latest Log 2", new Date(1689315125370L));
        applicationLogs.add(mockLog2);
        applicationLogs.add(mockLog1);
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testGetAllApplicationLogs() {

        when(applicationLogRepository.findAllByOrderByCreatedOnDesc()).thenReturn(applicationLogs);

        List<ApplicationLog> result = applicationLogRepository.findAllByOrderByCreatedOnDesc();

        assertThat(result).isNotNull();
        assertEquals(2,result.get(0).getId());
    }
    @Test
    void testGetLatestApplicationLog(){
        when(applicationLogRepository.getLatestOne()).thenReturn(applicationLogs.get(0));

        ApplicationLog result1 = applicationLogRepository.getLatestOne();

        assertThat(result1).isNotNull();
        assertEquals(2, result1.getId());

    }

    private ApplicationLog createMockApplicationLog(Integer id, String log, Date createdOn) {
        ApplicationLog applicationLog = new ApplicationLog();
        applicationLog.setId(id);
        applicationLog.setApplicationName("Test Application");
        applicationLog.setApplicationVersion("1.0");
        applicationLog.setLogs(new String[]{log});
        applicationLog.setUrl("/path/to/application");
        applicationLog.setCreatedOn(createdOn);
        applicationLog.setCreatedBy("test_user");
        return applicationLog;
    }
}