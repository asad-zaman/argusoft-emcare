package com.argusoft.who.emcare.web.user.dao;

import com.argusoft.who.emcare.web.EmcareWebApplicationTest;
import com.argusoft.who.emcare.web.user.entity.RoleEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = EmcareWebApplicationTest.class)
class RoleEntityRepositoryTest {

    @Autowired
    private RoleEntityRepository roleEntityRepository;

    @Test
    void findByRoleName() {
        RoleEntity roleEntity = roleEntityRepository.findByRoleName("admin_user");
        assertNotEquals(roleEntity, null);
    }
}