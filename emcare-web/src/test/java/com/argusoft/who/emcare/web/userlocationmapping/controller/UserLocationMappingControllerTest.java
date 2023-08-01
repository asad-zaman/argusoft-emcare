package com.argusoft.who.emcare.web.userlocationmapping.controller;

import com.argusoft.who.emcare.web.userlocationmapping.model.UserLocationMapping;
import com.argusoft.who.emcare.web.userlocationmapping.service.UserLocationMappingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class UserLocationMappingControllerTest {

    @Mock
    private UserLocationMappingService userLocationMappingService;

    @InjectMocks
    private UserLocationMappingController userLocationMappingController;

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
    void updateUserLocationMapping() {
        List<UserLocationMapping> userLocationMappingList = Arrays.asList(new UserLocationMapping(), new UserLocationMapping());

        when(userLocationMappingService.saveOrUpdateUserLocationMapping(userLocationMappingList)).thenReturn(ResponseEntity.ok(userLocationMappingList));
        ResponseEntity<Object> response = userLocationMappingController.updateUserLocationMapping(userLocationMappingList);

        assertNotNull(response);
        assertEquals(userLocationMappingList, response.getBody());
        assertEquals(userLocationMappingList.size(), ((List<UserLocationMapping>) response.getBody()).size());
    }

    @Test
    void getUserLocationMappingByUserId() {
        String userId = "user123";
        List<UserLocationMapping> expectedMappingList = Arrays.asList(
                new UserLocationMapping(),
                new UserLocationMapping()
        );

        when(userLocationMappingService.getUserLocationMappingByUserId(userId)).thenReturn(ResponseEntity.ok(expectedMappingList));
        ResponseEntity<Object> response = userLocationMappingController.getUserLocationMappingByUserId(userId);

        assertNotNull(response);
        assertEquals(expectedMappingList, response.getBody());
        assertEquals(expectedMappingList.size(), ((List<UserLocationMapping>) response.getBody()).size());
    }
}