package com.argusoft.who.emcare.web.userlocationmapping.service;

import com.argusoft.who.emcare.web.userlocationmapping.dao.UserLocationMappingRepository;
import com.argusoft.who.emcare.web.userlocationmapping.model.UserLocationMapping;
import com.argusoft.who.emcare.web.userlocationmapping.service.impl.UserLocationMappingServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserLocationMappingServiceTest {

    @Mock
    private UserLocationMappingRepository userLocationMappingRepository;

    @InjectMocks
    private UserLocationMappingServiceImpl userLocationMappingService;

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
    void saveOrUpdateUserLocationMapping() {
        List<UserLocationMapping> userLocationMappingList = Arrays.asList(new UserLocationMapping(), new UserLocationMapping());

        ResponseEntity<Object> response = userLocationMappingService.saveOrUpdateUserLocationMapping(userLocationMappingList);

        assertNotNull(response.getBody());
        assertEquals(userLocationMappingList, response.getBody());
    }

    @Test
    void getUserLocationMappingByUserId() {
        String userId = "UserId For New User";
        List<UserLocationMapping> expectedMappingList = Arrays.asList(new UserLocationMapping(), new UserLocationMapping());

        when(userLocationMappingRepository.findByUserId(userId)).thenReturn(expectedMappingList);

        ResponseEntity<Object> response = userLocationMappingService.getUserLocationMappingByUserId(userId);

        assertNotNull(response.getBody());
        assertEquals(expectedMappingList, response.getBody());
        assertEquals(expectedMappingList.size(), ((List<UserLocationMapping>) response.getBody()).size());
    }
}