package com.argusoft.who.emcare.web.questionnaireresponse.respository;

import com.argusoft.who.emcare.web.questionnaireresponse.model.UserSyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSyncLogRepository extends JpaRepository<UserSyncLog, Long> {
}
