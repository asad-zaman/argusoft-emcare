package com.argusoft.who.emcare.web.common.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.hibernate.Session;


import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GenericRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private GenericRepositoryImpl genericRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetSession() {
        Session mockedSession = mock(Session.class);
        when(entityManager.unwrap(Session.class)).thenReturn(mockedSession);

        Session session = genericRepository.getSession();

        verify(entityManager, times(1)).unwrap(Session.class);

        assertEquals(mockedSession, session);
    }
}
