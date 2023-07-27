package com.argusoft.who.emcare.web.menu.dto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserFeatureJsonTest {
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
    void dtoGetterSetterTestWithNoNullFields() {
        UserFeatureJson mockDTO = mock(UserFeatureJson.class);
        when(mockDTO.getId()).thenReturn("id");
        when(mockDTO.getFeatureJson()).thenReturn("featureJSON");
        when(mockDTO.getMenuName()).thenReturn("menuName");
        when(mockDTO.getParent()).thenReturn(1L);
        when(mockDTO.getOrderNumber()).thenReturn(101L);

        assertEquals(mockDTO.getId(), "id");
        assertEquals(mockDTO.getFeatureJson(), "featureJSON");
        assertEquals(mockDTO.getMenuName(), "menuName");
        assertEquals(mockDTO.getParent(), 1L);
        assertEquals(mockDTO.getOrderNumber(), 101L);
    }

    @Test
    void dtoGetterSetterTestWithPartialNullFields() {
        UserFeatureJson mockDTO = mock(UserFeatureJson.class);
        when(mockDTO.getId()).thenReturn("id");
        when(mockDTO.getFeatureJson()).thenReturn("featureJSON");
        when(mockDTO.getMenuName()).thenReturn("menuName");
        when(mockDTO.getParent()).thenReturn(null);
        when(mockDTO.getOrderNumber()).thenReturn(null);

        assertEquals(mockDTO.getId(), "id");
        assertEquals(mockDTO.getFeatureJson(), "featureJSON");
        assertEquals(mockDTO.getMenuName(), "menuName");
        assertNull(mockDTO.getParent());
        assertNull(mockDTO.getOrderNumber());
    }
}