package com.argusoft.who.emcare.web.applicationlog.repository;

import com.argusoft.who.emcare.web.applicationlog.entity.ApplicationLog;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
