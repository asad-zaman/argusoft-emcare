package com.argusoft.who.emcare.web.common.dao;

import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class GenericRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public Session getSession() {
        Session session = entityManager.unwrap(Session.class);
        return session;
    }
}
