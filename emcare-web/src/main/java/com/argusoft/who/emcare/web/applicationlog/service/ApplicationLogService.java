package com.argusoft.who.emcare.web.applicationlog.service;

import com.argusoft.who.emcare.web.applicationlog.entity.ApplicationLog;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 03/04/23  9:15 pm
 */
public interface ApplicationLogService {

    public Map<String, String> addApplicationLog(MultipartFile multipartFile, String logData) throws Exception;

    public List<ApplicationLog> getAllApplicationLogs();
}
