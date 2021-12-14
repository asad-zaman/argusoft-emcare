package com.argusoft.who.emcare.web.user.dao;

import com.argusoft.who.emcare.web.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
