package com.argusoft.who.emcare.web.mail.dao;

import com.argusoft.who.emcare.web.mail.entity.EmailContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailRepository extends JpaRepository<EmailContent, Long> {

    EmailContent findByCode(String code);
}
