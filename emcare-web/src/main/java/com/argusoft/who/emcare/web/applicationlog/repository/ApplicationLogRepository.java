package com.argusoft.who.emcare.web.applicationlog.repository;

import com.argusoft.who.emcare.web.applicationlog.entity.ApplicationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 03/04/23  9:16 pm
 */
@Repository
public interface ApplicationLogRepository extends JpaRepository<ApplicationLog, Integer> {

    public List<ApplicationLog> findAllByOrderByCreatedOnDesc();

    @Query(value = "SELECT * FROM APPLICATION_LOG ORDER BY CREATED_ON DESC LIMIT 1;", nativeQuery = true)
    public ApplicationLog getLatestOne();
}
